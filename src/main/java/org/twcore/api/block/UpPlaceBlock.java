package org.twcore.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 可放置物品的方块基类，提供统一的物品放置和取出交互机制。
 * <p>
 * 该方块通过与实现{@link Container}接口的方块实体配合，允许玩家在方块上放置和取出物品。
 * 支持自定义放置和取出条件、音效以及物品的视觉表现。
 * </p>
 *
 * @see UpPlaceBlockEntity
 */
public abstract class UpPlaceBlock extends BaseEntityBlock {
    /**
     * 方块交互音效配置
     */
    public final UpSounds upSounds;

    public UpPlaceBlock(Properties settings, UpSounds upSounds) {
        super(settings);
        this.upSounds = upSounds;
    }

    public UpPlaceBlock(Properties settings) {
        this(settings, UpSounds.DYNAMIC);
    }

    /**
     * 当方块被替换或破坏时，将方块实体中的库存物品掉落出来
     * <p>
     * 确保方块被破坏时不会丢失其中的物品。
     * </p>
     */
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container inventory) {
                Containers.dropContents(world, pos, inventory);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    /**
     * 获取方块的轮廓形状，合并基础形状与容器中物品的形状
     * <p>
     * 当方块实体中有物品时，轮廓形状会是基础形状和物品形状的并集，
     * 这样可以正确显示物品在方块上的视觉表现。
     * </p>
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof UpPlaceBlockEntity blockEntity && !blockEntity.isEmpty()) {
            return Shapes.or(
                    getBaseShape(state, world, pos, context),
                    blockEntity.getContentShape(state, world, pos, context)
            );
        }
        return getBaseShape(state, world, pos, context);
    }

    /**
     * 获取方块的基准轮廓形状
     * <p>
     * 子类必须实现此方法来定义方块本身的基本碰撞体积。
     * </p>
     *
     * @param state 当前方块状态
     * @param world 方块所在的世界
     * @param pos 方块位置
     * @param context 形状计算上下文
     * @return 方块的基准轮廓形状
     */
    public abstract VoxelShape getBaseShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack handStack = player.getItemInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof UpPlaceBlockEntity upPlaceBlockEntity) {
            // 尝试取出物品
            if (canFetched(upPlaceBlockEntity, handStack)) {
                UpPlaceBlockEntity.Result fetchResult = upPlaceBlockEntity.tryFetchItem(player, hit);
                if (fetchResult.isAccepted()) {
                    upPlaceBlockEntity.onFetch(state, world, pos, player, hand, hit, fetchResult.opsStacks());
                    return fetchResult.result();
                }
            }

            // 尝试放置物品
            if (canPlace(upPlaceBlockEntity, handStack)) {
                UpPlaceBlockEntity.Result placeResult = upPlaceBlockEntity.tryAddItem(handStack, hit);
                if (placeResult.isAccepted()) {
                    upPlaceBlockEntity.onPlace(state, world, pos, player, hand, hit, handStack, placeResult.opsStacks());
                    return placeResult.result();
                }
            }
        }

        return InteractionResult.FAIL;
    }

    /**
     * 检查当前条件下是否可以执行取出操作
     * <p>
     * 子类必须实现此方法来确定取出操作的触发条件，
     * 例如：手中是否持有特定工具、方块中是否有物品等。
     * </p>
     *
     * @param blockEntity 目标方块实体
     * @param handStack 玩家手中的物品堆栈
     * @return 如果可以取出物品返回true，否则返回false
     */
    public abstract boolean canFetched(UpPlaceBlockEntity blockEntity, ItemStack handStack);

    /**
     * 检查当前条件下是否可以执行放置操作
     * <p>
     * 子类必须实现此方法来确定放置操作的触发条件，
     * 例如：手中物品是否有效、方块是否有空位等。
     * </p>
     *
     * @param blockEntity 目标方块实体
     * @param handStack 玩家手中的物品堆栈
     * @return 如果可以放置物品返回true，否则返回false
     */
    public abstract boolean canPlace(UpPlaceBlockEntity blockEntity, ItemStack handStack);

    /**
     * 定义物品在 {@link UpPlaceBlock} 上放置和取出时的音效配置。
     * <p>
     * 提供三种预置模式：
     * <ul>
     *     <li><b>空音效 {@link #EMPTY}</b>：完全不播放任何声音。</li>
     *     <li><b>动态物品音效 {@link #DYNAMIC}</b>：根据交互的物品类型自动决定声音。
     *         此时记录中的 {@code placeSound} 和 {@code fetchSound} 仅作为动态获取失败时的兜底值（默认为石头声音）。</li>
     *     <li><b>固定音效</b>：通过构造方法直接指定一个固定的 {@link SoundEvent}，播放时不再动态计算。</li>
     * </ul>
     *
     * @param placeSound 放置时使用的固定音效（动态模式下作为兜底）
     * @param fetchSound 取出时使用的固定音效（动态模式下作为兜底）
     */
    public record UpSounds(SoundEvent placeSound, SoundEvent fetchSound) {

        /** 空音效：不播放任何声音 */
        public static final UpSounds EMPTY = new UpSounds(null, null);

        /**
         * 动态物品音效：根据交互物品的材质自动选择声音。
         * 兜底音效为石头放置/破坏音效。
         */
        public static final UpSounds DYNAMIC = new UpSounds(
                SoundEvents.STONE_PLACE,
                SoundEvents.STONE_BREAK
        );

        /**
         * 播放固定音效（仅在非动态模式下由 {@link UpPlaceBlockEntity#playSound} 调用）。
         * 如果内部音效为 {@code null} 则不会播放。
         *
         * @param world        当前世界
         * @param pos          播放位置
         * @param isPlaceSound true 播放放置音效，false 播放取出音效
         */
        public void playSound(Level world, BlockPos pos, boolean isPlaceSound) {
            if (isPlaceSound) {
                playPlaceSound(world, pos);
            } else {
                playFetchSound(world, pos);
            }
        }

        /** 播放放置固定音效（服务端，音效非空时） */
        public void playPlaceSound(Level world, BlockPos pos) {
            if (placeSound != null && !world.isClientSide) {
                world.playSound(null, pos, placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        /** 播放取出固定音效（服务端，音效非空时） */
        public void playFetchSound(Level world, BlockPos pos) {
            if (fetchSound != null && !world.isClientSide) {
                world.playSound(null, pos, fetchSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}