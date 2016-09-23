package org.jglrxavpok.mods.mcdoom.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class MCDoomDamages {

    public static DamageSource createBFG9000Damage(Entity plasmaBall, EntityLivingBase shooter) {
        return new EntityDamageSourceIndirect("bfg9000", plasmaBall, shooter).setProjectile();
    }

    public static DamageSource createChainsawDamage(EntityLivingBase user) {
        return new EntityDamageSource("chainsaw", user).setProjectile();
    }
}
