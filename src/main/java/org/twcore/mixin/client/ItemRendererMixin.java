package org.twcore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.twcore.client.api.render.ReplaceItemModel;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModelShaper itemModelShaper;
    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(
            method = "render",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BakedModel renderFlourItem(
            BakedModel originalModel,
            ItemStack stack,
            ItemDisplayContext renderMode,
            boolean leftHanded,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            BakedModel model) {

        if (stack == null || stack.isEmpty()) {
            return originalModel;
        }

        // 跳过三叉戟和望远镜的原版特殊处理
        if (stack.is(Items.TRIDENT) || stack.is(Items.SPYGLASS)) {
            return originalModel;
        }

        ReplaceItemModel replacer = ReplaceItemModel.getReplace(stack.getItem());
        if (replacer != null) {
            ReplaceItemModel.ReplaceContext context = new ReplaceItemModel.ReplaceContext(
                    stack,
                    renderMode,
                    leftHanded,
                    originalModel,
                    this.itemModelShaper.getModelManager(),
                    this.minecraft.level,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay
            );
            BakedModel replacedModel = replacer.ReplaceModel(context);
            return replacedModel != null ? replacedModel : originalModel;
        }

        return originalModel;
    }
}