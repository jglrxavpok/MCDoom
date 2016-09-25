package org.jglrxavpok.mods.mcdoom.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityGoreFX extends Particle {
    public static final ResourceLocation GORE_PARTICLE_LOCATION  = new ResourceLocation(MCDoom.modid, "textures/particle/gore.png");
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public EntityGoreFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        particleMaxAge = 10 * 300;
        particleGravity = 0.6f;

        // Takes a random 4x4 region of the texture
        minX = (int) Math.floor(Math.random()*(16-4));
        minY = (int) Math.floor(Math.random()*(16-4));
        maxX = minX + 4;
        maxY = minY + 4;

        this.field_190014_F = (float) (Math.random()* Math.PI*2f); // sets the rotation of the particle
        field_190015_G = field_190014_F; // set 'lastRotation' variable
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        particleBlue = 0.9f;
        particleGreen = 0.9f;

        motionX *= 0.99f;
        motionZ *= 0.99f;

        BlockPos blockpos = new BlockPos(this.posX, this.posY-0.1f, this.posZ);
        IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
        Material below = iblockstate.getMaterial();

        if (below.isSolid())
        {
            motionY = 0f;

            motionX *= 0.5f;
            motionZ *= 0.5f;
        }

        blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        iblockstate = this.worldObj.getBlockState(blockpos);
        Material current = iblockstate.getMaterial();

        if(current.isLiquid()) {
            particleAge -= 2;
        }

        if(current == Material.FIRE) {
            setExpired();
        }

    }

    public void renderParticle(VertexBuffer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

    }

    public void renderParticle0(VertexBuffer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

        float minU = (minX)/16f;
        float maxU = (maxX)/16f;
        float minV = (minY)/16f;
        float maxV = (maxY)/16f;
        float scale = 0.05F * this.particleScale;

        float xPos = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float yPos = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY) + 0.025f;
        float zPos = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

        int brightness = this.getBrightnessForRender(partialTicks);
        int j = brightness >> 16 & 65535;
        int k = brightness & 65535;
        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};

        if (this.field_190014_F != 0.0F)
        {
            float interpolatedAngle = this.field_190014_F + (this.field_190014_F - this.field_190015_G) * partialTicks;
            float f9 = MathHelper.cos(interpolatedAngle * 0.5F);
            float f10 = MathHelper.sin(interpolatedAngle * 0.5F) * (float)field_190016_K.xCoord;
            float f11 = MathHelper.sin(interpolatedAngle * 0.5F) * (float)field_190016_K.yCoord;
            float f12 = MathHelper.sin(interpolatedAngle * 0.5F) * (float)field_190016_K.zCoord;
            Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

            for (int l = 0; l < 4; ++l)
            {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
            }
        }

        buffer.pos((double)xPos + avec3d[0].xCoord, (double)yPos + avec3d[0].yCoord, (double)zPos + avec3d[0].zCoord).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)xPos + avec3d[1].xCoord, (double)yPos + avec3d[1].yCoord, (double)zPos + avec3d[1].zCoord).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)xPos + avec3d[2].xCoord, (double)yPos + avec3d[2].yCoord, (double)zPos + avec3d[2].zCoord).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)xPos + avec3d[3].xCoord, (double)yPos + avec3d[3].yCoord, (double)zPos + avec3d[3].zCoord).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
