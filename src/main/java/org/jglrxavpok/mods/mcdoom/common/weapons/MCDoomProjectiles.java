package org.jglrxavpok.mods.mcdoom.common.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.jglrxavpok.mods.mcdoom.common.entity.MCDoomProjectileEntity;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;

public class MCDoomProjectiles {


    public static MCDoomProjectileEntity create(World world, String type, EntityLivingBase shooter) {
        return new PlasmaBallEntity(world, shooter);
    }
}
