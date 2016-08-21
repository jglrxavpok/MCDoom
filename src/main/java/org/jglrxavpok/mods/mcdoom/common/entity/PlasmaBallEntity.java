package org.jglrxavpok.mods.mcdoom.common.entity;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoomDamages;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PlasmaBallEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> DEATH_COUNTER = EntityDataManager.createKey(PlasmaBallEntity.class, DataSerializers.VARINT);
    public static final int MAX_DEATH_COUNTER = 5;
    private List<Entity> targets;
    protected EntityLivingBase owner;

    public PlasmaBallEntity(World world) {
        super(world);
    }

    public PlasmaBallEntity(World world, EntityLivingBase owner) {
        super(world, owner);
        this.owner = owner;
        setHeadingFromThrower(owner, owner.rotationPitch, owner.rotationYaw, 0f, 2f, 0f);
    }

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
    protected void entityInit() {
        super.entityInit();
        setSize(1, 1);
        getDataManager().register(DEATH_COUNTER, -100);
        targets = Collections.emptyList();
    }

    @Override
    protected float getGravityVelocity() {
        return 0f;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(!worldObj.isRemote && result.typeOfHit != RayTraceResult.Type.MISS) {
            if(!isDying()) {
                if(result.typeOfHit == RayTraceResult.Type.ENTITY) {
                    result.entityHit.attackEntityFrom(MCDoomDamages.createBFG9000Damage(this, getThrower()), 10f);
                }
                prepareDeath();
            }
        }
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if(!isDying()) {
            targets = worldObj.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().expand(20, 20, 20), new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity input) {
                    return input != null && input != PlasmaBallEntity.this && !(input instanceof PlasmaBallEntity) && input != PlasmaBallEntity.this.getThrower() &&
                            worldObj.rayTraceBlocks(new Vec3d(posX+width/2f, posY+height/2f, posZ+width/2f), new Vec3d(input.posX, input.posY+input.getEyeHeight(), input.posZ), false, true, true) == null;
                }
            });

            if(!worldObj.isRemote) {
                for(Entity t : targets) {
                    if(t instanceof EntityDragon) {
                        EntityDragon dragon = ((EntityDragon) t);
                        dragon.attackEntityFromPart(dragon.dragonPartHead, MCDoomDamages.createBFG9000Damage(this, getThrower()), 15f);
                    } else {
                        t.attackEntityFrom(MCDoomDamages.createBFG9000Damage(this, getThrower()), 15f);
                    }
                }
            } else {
                for(Entity t : targets) {
                    if(Math.random() < 0.1) {
                        worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, t.posX, t.posY, t.posZ,0,0,0);
                        worldObj.playSound(t.posX, t.posY, t.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 5f, true);
                    }
                }
            }

            if(ticksExisted >= 10 * 20) {
                prepareDeath();
            }
        }


        int deathCounter = getDataManager().get(DEATH_COUNTER);
        if(deathCounter > 0) {
            deathCounter--;
        }

        getDataManager().set(DEATH_COUNTER, deathCounter);

        if(deathCounter == 0) {
            setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("deathCounter", getDeathCounter());
        if(owner != null)
            compound.setString("ownerUUID", owner.getUniqueID().toString());
        else
            compound.setString("ownerUUID", "null");
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        getDataManager().set(DEATH_COUNTER, compound.getInteger("deathCounter"));

        String throwerID = compound.getString("ownerUUID");
        if(!throwerID.equals("null") && !throwerID.isEmpty()) {
            owner = worldObj.getPlayerEntityByUUID(UUID.fromString(throwerID));
        }
    }

    @Override
    public boolean handleWaterMovement() {
        return false;
    }

    private void prepareDeath() {
        motionX = motionY = motionZ = 0;
        worldObj.newExplosion(this, posX, posY, posZ, 2f, false, true);
        worldObj.newExplosion(this, posX, posY, posZ, 4f, true, false); // does not damage the terrain
        getDataManager().set(DEATH_COUNTER, MAX_DEATH_COUNTER);

        System.out.println("! "+getThrower());
    }

    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    public boolean isDying() {
        return getDataManager().get(DEATH_COUNTER) >= 0;
    }

    public int getDeathCounter() {
        return getDataManager().get(DEATH_COUNTER);
    }

    public List<Entity> getTargets() {
        return targets;
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
