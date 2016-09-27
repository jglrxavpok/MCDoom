package org.jglrxavpok.mods.mcdoom.common.eventhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;

public class MCDoomWeaponUpdater {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        if(evt.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) evt.getEntityLiving());
            InventoryPlayer inventory = player.inventory;
            updateWeapons(inventory.offHandInventory);
            updateWeapons(inventory.mainInventory);
            updateWeapons(inventory.armorInventory);
        }
    }

    private void updateWeapons(ItemStack[] inventory) {
        for(ItemStack s : inventory) {
            if(s == null)
                continue;
            if(s.getItem() instanceof WeaponItem) {
                if(s.getItemDamage() != 0) {
                    s.setItemDamage(s.getItemDamage()-1); // TODO: Change depending on weapon
                }
            }
        }
    }
}
