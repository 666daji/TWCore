package org.twcore.api.block;

import org.twcore.api.sound.Item2BlockSounds;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 放置物品方块实体基类，用于管理方块上的物品放置和取出功能。
 * <p>
 * 该实体实现了{@link Container}接口，支持物品的存储和管理，并提供了一套完整的物品放置和取出机制。
 * 子类需要实现特定的物品验证、形状计算和交互逻辑。
 * </p>
 *
 * @see UpPlaceBlock
 */
public abstract class UpPlaceBlockEntity extends BlockEntity implements Container {
    /**
     * 物品栏列表，存储方块上放置的所有物品
     */
    protected NonNullList<ItemStack> inventory;

    public UpPlaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize) {
        super(type, pos, state);
        this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        ContainerHelper.saveAllItems(nbt, this.inventory, registryLookup);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        validateSlotIndex(slot);
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        validateSlotIndex(slot);
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        validateSlotIndex(slot);
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.inventory.set(slot, stack);
        limitStackSizeIfNeeded(stack);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * 限制物品堆叠大小不超过最大值。
     *
     * @param stack 需要限制堆叠大小的物品堆栈
     */
    protected void limitStackSizeIfNeeded(ItemStack stack) {
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    /**
     * 验证槽位索引是否在有效范围内。
     *
     * @param slot 要验证的槽位索引
     * @throws IllegalArgumentException 如果槽位索引超出有效范围
     */
    public void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.getContainerSize()) {
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + this.getContainerSize() + ")");
        }
    }

    /**
     * 获取容器中物品的碰撞形状。
     * <p>
     * 该方法用于计算物品在方块世界中的视觉表现和碰撞体积。
     * </p>
     *
     * @param state 当前方块状态
     * @param world 方块所在的世界
     * @param pos 方块位置
     * @param context 形状计算上下文
     * @return 物品的碰撞形状
     */
    public abstract VoxelShape getContentShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    /**
     * 验证物品是否可以放入该方块实体的物品栏。
     *
     * @param stack 待验证的物品堆栈
     * @return 如果可以放入返回true，否则返回false
     */
    public abstract boolean isValidItem(ItemStack stack);

    /**
     * 尝试向容器中添加物品。
     * <p>
     * 默认每次添加会消耗数量为1的物品。如果需要不同的消耗数量，
     * 一般需要重写{@link #onPlace(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult, ItemStack, List)}方法，
     * 来扣除玩家不同数量的物品
     * </p>
     *
     * @param stack 要添加的物品堆栈
     * @return 操作结果，成功返回{@link InteractionResult#SUCCESS}，失败返回{@link InteractionResult#FAIL}
     */
    public abstract Result tryAddItem(ItemStack stack, BlockHitResult hit);

    /**
     * 尝试从容器中取出物品。
     *
     * @param player 执行取出操作的玩家
     * @return 操作结果，成功返回{@link InteractionResult#SUCCESS}，失败返回{@link InteractionResult#FAIL}
     */
    public abstract Result tryFetchItem(Player player, BlockHitResult hit);

    /**
     * 当物品成功取出时调用的回调方法。
     * <p>
     * 默认实现会播放取出音效。子类可以重写此方法来添加自定义逻辑，
     * 如播放粒子效果、执行特殊操作等。如果重写此方法，请确保根据需要
     * 调用父类方法以保持默认的音效行为。
     * </p>
     *
     * @param state 当前方块状态
     * @param world 方块所在的世界
     * @param pos 方块位置
     * @param player 执行取出操作的玩家
     * @param hand 玩家使用的手
     * @param hit 方块击中结果
     * @param fetchStacks 此次取出操作获得的所有物品堆栈
     */
    public void onFetch(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, List<ItemStack> fetchStacks) {
        playSound(world, pos, fetchStacks.get(0), false);
    }

    /**
     * 当物品成功放置时调用的回调方法。
     * <p>
     * 默认实现会播放放置音效并消耗玩家手中1个物品（创造模式除外）。
     * 子类可以重写此方法来添加自定义逻辑。如果重写此方法，请确保根据需要
     * 调用父类方法以保持默认的音效和物品消耗行为。
     * </p>
     *
     * @param state      当前方块状态
     * @param world      方块所在的世界
     * @param pos        方块位置
     * @param player     执行放置操作的玩家
     * @param hand       玩家使用的手
     * @param hit        方块击中结果
     * @param placeStack 放置的物品堆栈
     * @param itemStacks 操作影响的物品堆栈，一般是长度1的placeStack列表
     */
    public void onPlace(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack placeStack, List<ItemStack> itemStacks) {
        playSound(world, pos, placeStack, true);

        if (!player.isCreative()) {
            placeStack.shrink(1);
        }
    }

    /**
     * 统一的音效播放入口。
     * <p>
     * 根据方块上绑定的 {@link UpPlaceBlock.UpSounds} 决定播放策略：
     *
     * @param world        当前世界
     * @param pos          播放位置
     * @param stack        交互的物品堆栈（用于动态声音获取）
     * @param isPlaceSound true 为放置音效，false 为取出音效
     * @see UpPlaceBlock.UpSounds
     */
    protected void playSound(Level world, BlockPos pos, ItemStack stack, boolean isPlaceSound) {
        if (getBlockState().getBlock() instanceof UpPlaceBlock upPlaceBlock) {
            UpPlaceBlock.UpSounds sounds = upPlaceBlock.upSounds;

            if (sounds == UpPlaceBlock.UpSounds.EMPTY) {
                return;
            }

            if (sounds == UpPlaceBlock.UpSounds.DYNAMIC) {
                SoundEvent sound = getDynamicPlaceSound(stack, isPlaceSound);
                if (sound != null) {
                    world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                sounds.playSound(world, pos, isPlaceSound);
            }
        }
    }

    /**
     * 动态获取物品对应的放置/取出音效。
     * <p>
     * 该方法仅在 {@link UpPlaceBlock.UpSounds#DYNAMIC} 模式下被调用。
     * 默认实现：
     * <ul>
     *     <li>如果物品是 {@link net.minecraft.world.item.BlockItem}，则返回其对应方块的放置/破坏音效。</li>
     *     <li>否则使用 {@link UpPlaceBlock.UpSounds#DYNAMIC} 中预置的兜底音效（石头音效）。</li>
     * </ul>
     * 子类可以重写此方法以实现完全自定义的动态音效选择逻辑。
     *
     * @param stack        被交互的物品堆栈
     * @param isPlaceSound true 请求放置音效，false 请求取出音效
     * @return 要播放的 {@link SoundEvent}，返回 {@code null} 则不播放任何声音
     */
    protected SoundEvent getDynamicPlaceSound(ItemStack stack, boolean isPlaceSound) {
        SoundType soundGroup = Item2BlockSounds.getSoundGroup(stack);

        return isPlaceSound ? soundGroup.getPlaceSound() : soundGroup.getBreakSound();
    }

    /**
     * 获取容器中的第一个物品的类型。
     *
     * @return 容器中第一个物品的类型，如果容器为空则返回null
     */
    public Item getContentItem() {
        ItemStack contentStack = this.getItem(0);
        return contentStack.isEmpty() ? null : contentStack.getItem();
    }

    /**
     * 检查容器是否已满。
     * <p>
     * 容器已满的条件是所有槽位都有物品且每个物品都达到了最大堆叠数量。
     * </p>
     *
     * @return 如果容器已满返回true，否则返回false
     */
    public boolean isFull() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            if (stack.isEmpty() || stack.getCount() < this.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 标记方块实体数据已更改，并同步到客户端。
     * <p>
     * 这是一个便捷方法，结合了{@link #setChanged()}和{@link #sync()}的调用。
     * </p>
     */
    public void markDirtyAndSync() {
        this.setChanged();
        this.sync();
    }

    /**
     * 同步方块实体数据到客户端。
     * <p>
     * 当方块实体数据发生变化时调用此方法，确保客户端能够及时更新显示。
     * </p>
     */
    public void sync() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    /**
     * 表示一次操作和取出的结果。
     *
     * @param opsStacks 操作的物品堆栈列表
     * @param result 操作的结果
     */
    public record Result(List<ItemStack> opsStacks, InteractionResult result) {
        public static Result of(List<ItemStack> opsStacks, InteractionResult result) {
            return new Result(opsStacks, result);
        }

        public static Result of(InteractionResult result) {
            return new Result(List.of(), result);
        }

        public static Result of(ItemStack stack, InteractionResult result) {
            return new Result(List.of(stack), result);
        }

        public boolean isAccepted() {
            return result.consumesAction();
        }
    }
}