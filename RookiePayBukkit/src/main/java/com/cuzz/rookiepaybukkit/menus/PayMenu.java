package com.cuzz.rookiepaybukkit.menus;

import lombok.Setter;
import me.trytofeel.rookieFonts.manager.TemplateManager;
import me.trytofeel.rookieFonts.models.Template;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import nl.odalitadevelopments.menus.OdalitaMenus;
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
    type = MenuType.CHEST_6_ROW
)

public final class PayMenu implements PlayerMenuProvider {
    private MenuContents menuContent;

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents menuContents) {
        this.menuContent = menuContents;

        // title
        Template template = TemplateManager.getTemplateManager().TemplateList.get("pay");
        Component defaultComponentByString = template.getDefaultComponentByString(player.getName());
        final String jsonText = GsonComponentSerializer.gson().serialize(defaultComponentByString);
        menuContents.setTitle(jsonText);

        ItemMeta closeItemMeta = new ItemStack(Material.RED_WOOL).getItemMeta();
        assert closeItemMeta != null;
        closeItemMeta.setDisplayName("§c取消支付");
        ItemStack closeItemStack = new ItemStack(Material.BARRIER);
        closeItemStack.setItemMeta(closeItemMeta);
        ClickableItem closeItem = ClickableItem.of(closeItemStack, event -> {
            player.sendMessage("§c已取消支付");
            player.closeInventory();
        });
        menuContents.set(0, 7, closeItem);

//        menuContents.events().onClose(() -> canClose ? MenuCloseResult.CLOSE : MenuCloseResult.KEEP_OPEN);
    }

    public void updateTitle(String title) {
        if(menuContent != null) {
            this.menuContent.setTitle(title);
        }
    }
}