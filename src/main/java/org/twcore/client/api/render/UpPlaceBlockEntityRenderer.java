package org.twcore.client.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.twcore.api.block.UpPlaceBlock;
import org.twcore.api.block.UpPlaceBlockEntity;

/**
 * 用于渲染放置在{@link UpPlaceBlock}中的内容的渲染器。
 * @param <T> 对应的{@link UpPlaceBlock}的子类
 */
public abstract class UpPlaceBlockEntityRenderer<T extends UpPlaceBlockEntity> implements BlockEntityRenderer<T> {
    protected final BlockEntityRendererProvider.Context context;

    public UpPlaceBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    /**
     * 渲染一个摆放在该方块中的物品堆栈。
     *
     * @param stack 要渲染的物品堆栈
     * @param entity 当前的方块实体
     * @apiNote 需要子类在render方法中主动调用该方法渲染
     */
    protected void fromStackRender(ItemStack stack, T entity, float tickDelta, PoseStack matrices,
                                   MultiBufferSource vertexConsumers, int light, int overlay) {
        // 获取渲染器
        UpPlaceStackRenderer renderer = UpPlaceStackRenderer.get(stack.getItem());

        // 构建渲染上下文
        UpPlaceStackRenderer.RenderContext renderContext = new UpPlaceStackRenderer.RenderContext(
                stack, entity,
                context, tickDelta,
                matrices, vertexConsumers,
                light, overlay);

        // 使用渲染器渲染物品堆栈
        renderer.fromStackRender(renderContext);
    }
}
