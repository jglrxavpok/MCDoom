package org.jglrxavpok.mods.mcdoom.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class MCDoomProjectileEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
    private EntityLivingBase owner;

    public MCDoomProjectileEntity(World worldIn) {
        super(worldIn);
    }

    public MCDoomProjectileEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public MCDoomProjectileEntity(World worldIn, EntityLivingBase owner) {
        super(worldIn, owner);
        this.owner = owner;
        setHeadingFromThrower(owner, owner.rotationPitch, owner.rotationYaw, 0f, 2f, 0f);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        onProjectileUpdate();
    }

    protected abstract void onProjectileUpdate();

    public boolean isInRangeToRenderDist(double distance)
    {
        return true;
    }

    @Nullable
    @Override
    public EntityLivingBase getThrower() {
        return owner;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if(owner != null)
            compound.setString("ownerUUID", owner.getUniqueID().toString());
        else
            compound.setString("ownerUUID", "null");
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        String throwerID = compound.getString("ownerUUID");
        if(!throwerID.equals("null") && !throwerID.isEmpty()) {
            owner = worldObj.getPlayerEntityByUUID(UUID.fromString(throwerID));
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if(getThrower() != null)
            ByteBufUtils.writeUTF8String(buffer, getThrower().getUniqueID().toString());
        else
            ByteBufUtils.writeUTF8String(buffer, "null");
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        String throwerID = ByteBufUtils.readUTF8String(additionalData);
        if(!throwerID.equals("null")) {
            owner = worldObj.getPlayerEntityByUUID(UUID.fromString(throwerID));
        }
    }

}
