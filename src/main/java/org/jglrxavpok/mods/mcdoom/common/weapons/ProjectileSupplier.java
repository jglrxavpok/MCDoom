package org.jglrxavpok.mods.mcdoom.common.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.jglrxavpok.mods.mcdoom.common.entity.MCDoomProjectileEntity;

public interface ProjectileSupplier {
    MCDoomProjectileEntity create(World world, EntityLivingBase shooter);
}
