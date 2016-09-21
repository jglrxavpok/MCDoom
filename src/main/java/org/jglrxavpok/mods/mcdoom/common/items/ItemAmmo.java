package org.jglrxavpok.mods.mcdoom.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemAmmo extends Item {
    private final String ammoType;

    public ItemAmmo(String ammoType) {
        this.ammoType = ammoType;
        setUnlocalizedName("ammo_"+ammoType);
        setCreativeTab(CreativeTabs.COMBAT);
    }

    public String getAmmoType() {
        return ammoType;
    }
}
