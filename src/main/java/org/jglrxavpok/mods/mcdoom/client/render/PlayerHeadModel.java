package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class PlayerHeadModel extends ModelBase {

    private final ModelRenderer bipedHead;
    private final ModelRenderer bipedHeadwear;

    public PlayerHeadModel(float modelSize) {
        this.textureHeight = 64;
        this.textureWidth = 64;
        // TAKEN FROM ModelBiped.java
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + 0, 0.0F);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + 0, 0.0F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        quickRender(scale);
    }

    public void quickRender(float scale) {
        GlStateManager.pushMatrix();
        bipedHead.render(scale);
        bipedHeadwear.render(scale);
        GlStateManager.popMatrix();
    }
}
