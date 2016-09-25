package org.jglrxavpok.mods.mcdoom.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jglrxavpok.mods.mcdoom.client.particle.EntityGoreFX;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

import java.util.Random;

public class MessageSpawnGoreParticles implements IMessage {

    private int count;
    private float x;
    private float y;
    private float z;

    public MessageSpawnGoreParticles() {

    }

    public MessageSpawnGoreParticles(int count, float x, float y, float z) {
        this.count = count;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        count = buf.readInt();
        x = buf.readFloat();
        y = buf.readFloat();
        z = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(count);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
    }

    public static class Handler implements IMessageHandler<MessageSpawnGoreParticles, IMessage> {

        private final Random rand;

        public Handler() {
            rand = new Random();
        }

        @Override
        public IMessage onMessage(MessageSpawnGoreParticles message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT)
                throw new IllegalStateException("Cannot send MessageSpawnGoreParticles to a server!");
            double count = message.count * MCDoom.instance.getGoreProperty().getDouble();
            for (int i = 0; i < count; i++) {
                float dx = (float) rand.nextGaussian() * 0.5f;
                float dy = (float) rand.nextGaussian() * 0.5f;
                float dz = (float) rand.nextGaussian() * 0.5f;
                float mx = (float) rand.nextGaussian() * 0.1f;
                float my = (float) (rand.nextGaussian()/2f + 0.5f) * 0.75f;
                float mz = (float) rand.nextGaussian() * 0.1f;
                Particle particle = new EntityGoreFX(Minecraft.getMinecraft().theWorld, message.x+dx, message.y+dy, message.z+dz, mx, my, mz);
                MCDoom.proxy.spawnParticle(particle);
            }
            return null;
        }
    }
}
