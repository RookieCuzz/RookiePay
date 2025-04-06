package com.cuzz.rookiepaybukkit.menus;

import lombok.Setter;
import nl.odalitadevelopments.menus.annotations.Menu;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.contents.action.MenuCloseResult;
import nl.odalitadevelopments.menus.items.ClickableItem;
import nl.odalitadevelopments.menus.items.MenuItem;
import nl.odalitadevelopments.menus.items.buttons.CloseItem;
import nl.odalitadevelopments.menus.menu.providers.GlobalMenuProvider;
import nl.odalitadevelopments.menus.menu.providers.PlayerMenuProvider;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import nl.odalitadevelopments.menus.providers.providers.DefaultItemProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Setter
@Menu(
    title = "支付窗口",
    type = MenuType.CHEST_1_ROW
)

public final class PayMenu implements PlayerMenuProvider {
    private boolean canClose = false;
    private MenuContents menuContent;
    private String title;

    public PayMenu(String title) {
        this.title = title;
    }

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents menuContents) {
        this.menuContent = menuContents;
        menuContents.setTitle(this.title);

        ItemMeta closeItemMeta = new ItemStack(Material.BARRIER).getItemMeta();
        assert closeItemMeta != null;
        closeItemMeta.setDisplayName("§c取消支付");
        ItemStack closeItemStack = new ItemStack(Material.BARRIER);
        closeItemStack.setItemMeta(closeItemMeta);
        ClickableItem closeItem = ClickableItem.of(closeItemStack, event -> {
            canClose = true;
            player.sendMessage("§a已取消支付");
            event.getWhoClicked().closeInventory();
        });
        menuContents.set(8, closeItem);
        menuContents.events().onClose(() -> canClose ? MenuCloseResult.CLOSE : MenuCloseResult.KEEP_OPEN);
    }

    public void updateTitle(String title) {
        if(menuContent != null) {
            this.menuContent.setTitle(title);
        }
    }
}