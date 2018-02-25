
package com.avrgaming.civcraft.loregui.book;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigTradeGood;
import com.avrgaming.civcraft.loregui.GuiAction;
import com.avrgaming.civcraft.loregui.OpenInventoryTask;
import com.avrgaming.civcraft.loregui.book.BookGoodsGui;
import com.avrgaming.civcraft.lorestorage.LoreGuiItem;
import com.avrgaming.civcraft.lorestorage.LoreGuiItemListener;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.util.ItemManager;

public class BookGoodsTown
implements GuiAction {
    @Override
    public void performAction(InventoryClickEvent event, ItemStack stack) {
        Player player = (Player)event.getWhoClicked();
        Resident resident = CivGlobal.getResident(player);
        Civilization civ = resident.getCiv();
        Town town = resident.getSelectedTown();
        if (civ == null) {
            CivMessage.sendError((Object)player, CivSettings.localize.localizedString("var_virtualTG_noCiv"));
            return;
        }
        if (!civ.getLeaderGroup().hasMember(resident) && !resident.getSelectedTown().getMayorGroup().hasMember(resident)) {
            CivMessage.sendError((Object)player, CivSettings.localize.localizedString("var_virtualTG_noPermM", "\u00a76" + civ.getName() + "\u00a7c", "\u00a76" + town.getName() + "\u00a7c"));
            return;
        }
        if (StringUtils.isBlank((String)town.tradeGoods)) {
            CivMessage.sendError((Object)player, CivSettings.localize.localizedString("cmd_civ_trade_listtown_noGoods", "\u00a76" + town.getName() + "\u00a7c"));
            return;
        }
        Inventory listInventory = Bukkit.getServer().createInventory(player, 9, CivColor.GoldBold + CivSettings.localize.localizedString("cmd_civ_trade_listtown_listInvName", CivColor.RoseBold + town.getName()));
        int i = 0;
        for (String goodID : town.tradeGoods.split(", ")) {
            ConfigTradeGood configTradeGood = CivSettings.goods.get(goodID);
            if (configTradeGood == null) continue;
            String[] split = CivSettings.getBonusDisplayString(configTradeGood, null).split(";");
            ItemStack tradeGood = LoreGuiItem.build(configTradeGood.name, configTradeGood.material, configTradeGood.material_data, split);
            tradeGood = LoreGuiItem.setAction(tradeGood, "ShowTradeGoodInfo");
            listInventory.setItem(i, tradeGood);
            ++i;
        }
        ItemStack backButton = LoreGuiItem.build("\u041d\u0430\u0437\u0430\u0434", ItemManager.getId(Material.MAP), 0, CivSettings.localize.localizedString("var_virtualTG_backToMain"));
        backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
        backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
        backButton = LoreGuiItem.setActionData(backButton, "invName", BookGoodsGui.guiInventory.getName());
        listInventory.setItem(8, backButton);
        LoreGuiItemListener.guiInventories.put(listInventory.getName(), listInventory);
        TaskMaster.syncTask(new OpenInventoryTask(player, listInventory));
    }
}

