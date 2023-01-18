package io.github.galaxyvn.guihome.gui;

import com.cryptomorin.xseries.XMaterial;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.EssentialsPlayerListener;
import com.earth2me.essentials.EssentialsUserConf;
import com.earth2me.essentials.config.EssentialsUserConfiguration;
import io.github.galaxyvn.guihome.GuiHome;
import io.github.galaxyvn.guihome.utils.ItemBuilder;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Main {

    FileConfiguration config = GuiHome.plugin.getConfig();

    public void open(Player player) {
        IEssentials essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        IUser user = essentials.getUser(player.getUniqueId());

        List<String> homes = user.getHomes();

        RyseInventory.builder()
            .title(config.getString("title"))
            .rows(6)
            .provider(new InventoryProvider() {
                @Override
                public void init(Player player, InventoryContents contents) {
                    contents.fillBorders(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());

                    Pagination pagination = contents.pagination();
                    pagination.setItemsPerPage(28);
                    pagination.iterator(SlotIterator.builder()
                            .startPosition(1, 0)
                            .type(SlotIterator.SlotIteratorType.HORIZONTAL)
                            .build());

                    contents.set(5, 3, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                            .amount((pagination.isFirst() ? 1 : pagination.page() - 1))
                            .displayName(pagination.isFirst()
                                    ? "This is the first page"
                                    : "Page -> " + pagination.newInstance(pagination).previous().page())
                            .build(), event -> {
                        if (pagination.isFirst()) {
                            player.sendMessage(Component.text("You are already on the first page.",
                                    NamedTextColor.RED,
                                    TextDecoration.BOLD));
                            return;
                        }

                        RyseInventory currentInventory = pagination.inventory();
                        currentInventory.open(player, pagination.previous().page());
                    }));

                    for (String home : homes) {
                        pagination.addItem(new ItemBuilder(XMaterial.PAPER.parseMaterial())
                                .displayName(home)
                                .build());
                    }

                    int page = pagination.newInstance(pagination).next().page();
                    contents.set(5, 5, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                            .amount((pagination.isLast() ? 1 : page))
                            .displayName((!pagination.isLast()
                                    ? "Page -> " + page
                                    : "This is the last page"))
                            .build(), event -> {
                        if (pagination.isLast()) {
                            player.sendMessage(Component.text("You are already on the last page.",
                                    NamedTextColor.RED,
                                    TextDecoration.BOLD));
                            return;
                        }

                        RyseInventory currentInventory = pagination.inventory();
                        currentInventory.open(player, pagination.next().page());
                    }));
                }
            })
            .build(GuiHome.plugin)
            .open(player);
    }
}
