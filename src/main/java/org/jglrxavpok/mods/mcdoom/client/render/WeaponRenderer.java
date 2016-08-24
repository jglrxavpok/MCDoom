package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class WeaponRenderer {

    public abstract void renderWeapon(EntityPlayer player, ItemStack currentItem, ScaledResolution resolution, float partialTicks);

    protected float getBobbingX(Entity renderView, float partialTicks) {
        float walkedDistance = renderView.distanceWalkedModified - renderView.prevDistanceWalkedModified;
        float partialDistance = (renderView.distanceWalkedModified + walkedDistance * partialTicks);

        float delta = 10f;
        float halfDelta = delta/2f;
        float f5 = ((partialDistance*2f% delta)/delta);
        float bobbingOffsetX;
        if(f5 > 0.5f) {
            bobbingOffsetX = (((partialDistance*2f% halfDelta)/halfDelta) *2f -1f);
        } else {
            bobbingOffsetX = ((1f-(partialDistance*2f% halfDelta)/halfDelta) *2f -1f);
        }
        return bobbingOffsetX;
    }

    protected float getBobbingY(Entity renderView, float partialTicks) {
        return (pseudoBellCurve(getBobbingX(renderView, partialTicks)/2f+0.5f));
    }

    private float pseudoBellCurve(float normalizedX) {
        // Actual function:
        // pseudoBellCurve(normX) = 2.64^{-\frac{1}{2}\cdot \left(\frac{x^2-x+.25}{0.0625}\right)} (made with graph.tk)
        // with normX = x / width
        //
        // Based on the normal distribution density function
        float exponent = normalizedX*normalizedX - normalizedX + 0.25f; // expanded version of (x-0.5)² where 0.5 is the mean
        exponent /= 0.0325; // magic number (very roughly the equivalent of the standard deviation
        exponent *= -0.5f; // untouched from normal distribution
        return (float) Math.exp(exponent);
    }
}
