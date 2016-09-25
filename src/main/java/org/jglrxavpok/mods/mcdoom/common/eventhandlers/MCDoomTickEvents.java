package org.jglrxavpok.mods.mcdoom.common.eventhandlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

public class MCDoomTickEvents {

    @SubscribeEvent
    public void onTick(TickEvent evt) {
        MCDoom.proxy.onTickEvent(evt);
    }
}
