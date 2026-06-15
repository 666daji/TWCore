package org.twcore.client.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface ReplaceItemModel {
    Map<Item, ReplaceItemModel> REPLACE = new HashMap<>();

    /**
     * 注册物品模型替换器
     * @param item 要替换模型的物品
     * @param replace 替换逻辑
     */
    static void registry(Item item, ReplaceItemModel replace) {
        REPLACE.put(item, replace);
    }

    /**
     * 获取物品的模型替换器
     * @param item 物品
     * @return 对应的替换器，如果没有则返回null
     */
    @Nullable
    static ReplaceItemModel getReplace(Item item) {
        return REPLACE.get(item);
    }

    /**
     * 检查物品是否有模型替换器
     * @param item 物品
     * @return 是否有替换器
     */
    static boolean hasReplace(Item item) {
        return REPLACE.containsKey(item);
    }

    /**
     * 清除所有注册的模型替换器
     */
    static void clearAll() {
        REPLACE.clear();
    }

    /**
     * 移除指定物品的模型替换器
     * @param item 物品
     */
    static void remove(Item item) {
        REPLACE.remove(item);
    }

    /**
     * 创建简单的模型替换器，通过模型ID替换
     * @param modelId 新的模型ID
     * @return 替换器函数
     */
    static ReplaceItemModel createSimpleReplacer(ResourceLocation modelId) {
        return context -> {
            ModelManager modelManager = context.modelManager();
            if (modelManager != null) {
                BakedModel newModel = modelManager.getModel(
                        ModelResourceLocation.vanilla(
                                modelId.getPath(), "inventory"
                        )
                );
                return newModel != null ? newModel : context.originalModel();
            }
            return context.originalModel();
        };
    }

    /**
     * 创建条件模型替换器，根据不同渲染模式使用不同模型
     * @param modeMapper 根据渲染模式返回模型ID的函数
     * @return 替换器函数
     */
    static ReplaceItemModel createConditionalReplacer(
            Function<ItemDisplayContext, ResourceLocation> modeMapper) {
        return context -> {
            ResourceLocation modelId = modeMapper.apply(context.renderMode());
            if (modelId != null) {
                ModelManager modelManager = context.modelManager();
                if (modelManager != null) {
                    BakedModel newModel = modelManager.getModel(
                            ModelResourceLocation.vanilla(
                                    modelId.getPath(), "inventory"
                            )
                    );
                    if (newModel != null) {
                        return newModel;
                    }
                }
            }
            return context.originalModel();
        };
    }

    /**
     * 替换模型的接口方法
     * @param context 替换上下文
     * @return 替换后的烘焙模型，返回null将使用原始模型
     */
    BakedModel ReplaceModel(ReplaceContext context);

    /**
     * 替换上下文类，包含替换模型所需的所有信息
     */
    record ReplaceContext(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded,
                          BakedModel originalModel, ModelManager modelManager, Level world, PoseStack matrices,
                          MultiBufferSource vertexConsumers, int light, int overlay) {

        public ClientLevel getClientWorld() {
            return world instanceof ClientLevel ? (ClientLevel) world : null;
        }

        /**
         * 判断是否在GUI中渲染
         */
        public boolean isGuiRender() {
            return renderMode == ItemDisplayContext.GUI;
        }

        /**
         * 判断是否在第一人称手中渲染
         */
        public boolean isFirstPersonRender() {
            return renderMode.firstPerson();
        }

        /**
         * 判断是否在第三人称手中渲染
         */
        public boolean isThirdPersonRender() {
            return renderMode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                    renderMode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        }

        /**
         * 判断是否在地面渲染
         */
        public boolean isGroundRender() {
            return renderMode == ItemDisplayContext.GROUND;
        }

        /**
         * 判断是否在固定位置渲染（如展示框）
         */
        public boolean isFixedRender() {
            return renderMode == ItemDisplayContext.FIXED;
        }

        /**
         * 判断是否在头部渲染（如头盔）
         */
        public boolean isHeadRender() {
            return renderMode == ItemDisplayContext.HEAD;
        }
    }
}