package com.redpxnda.protectioncorrection.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.protectioncorrection.registry.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import static com.redpxnda.protectioncorrection.ProtectionCorrection.MODID;

public class ProtectionBar {
    private static final ResourceLocation PROTECTION_ICON = new ResourceLocation(MODID, "textures/gui/protection.png");

    public static final IGuiOverlay HUD_PROTECTION = ((gui, poseStack, partialTick, width, height) -> {
        int x, y;
        x = width/2-91;
        y = height-gui.leftHeight;
        Player player = Minecraft.getInstance().player;
        if (player != null && gui.shouldDrawSurvivalElements() && !gui.getMinecraft().options.hideGui) {
            poseStack.translate(0, 0, -5);
            gui.setupOverlayRenderState(true, false);
            double overallProt = player.getAttributeValue(Registry.PROTECTION.get()); // overall protection attribute
            double frontProt = player.getAttributeValue(Registry.FRONTAL_PROTECTION.get()); // frontal protection attribute
            double rearProt = player.getAttributeValue(Registry.REARWARD_PROTECTION.get()); // rearwards protection attribute
            double sideProt = player.getAttributeValue(Registry.SIDEWARD_PROTECTION.get()); // sidewards protection attribute
            double avg = ((overallProt+frontProt)+(overallProt+rearProt)+(overallProt+sideProt))/3; // average of all 3
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            double reductionPercent = Math.round((avg / (avg+20))*1000);
            reductionPercent/=10;
            if (reductionPercent > 0) {
                RenderSystem.setShaderColor(1f, 1f, 1f, (float) (reductionPercent / 100f) * 2f);
                GuiComponent.drawString(poseStack, Minecraft.getInstance().font, reductionPercent + "%", x + 52, y, 16777215);
                for (int i = 0; i < 6; i++) {
                    double side = overallProt;
                    switch (i) {
                        case 0, 1 -> side += frontProt;
                        case 2, 3 -> side += rearProt;
                        case 4, 5 -> side += sideProt;
                    }
                    double applyAmount = Math.abs(side - avg) / (Math.abs(side - avg) + 10);
                    float redDenom = (side - avg > 0) ? (float) applyAmount : 0f;
                    float greenDenom = (side - avg < 0) ? (float) applyAmount : 0f;
                    RenderSystem.setShaderColor(1 - redDenom, 1 - greenDenom, 0f, (float) (reductionPercent / 100f) * 2f);
                    RenderSystem.setShaderTexture(0, PROTECTION_ICON);
                    GuiComponent.blit(poseStack, x + i * 8, y, 0, 0, 9, 9, 9, 9);
                }
                gui.leftHeight+=10;
            }
        }
    });
}
