package org.jglrxavpok.mods.mcdoom.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

import java.util.List;

public class MCDoomConfigGui extends GuiConfig {
    public MCDoomConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(MCDoom.instance.getCategoryGraphical()).getChildElements(),
                MCDoom.modid, false, false, "MCDoom Graphical Config");
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        MCDoom.instance.getConfig().save();
    }
}
