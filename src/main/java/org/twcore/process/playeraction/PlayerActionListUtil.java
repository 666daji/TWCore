package org.twcore.process.playeraction;

import org.twcore.TWCore;
import org.twcore.api.process.PlayerAction;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/**
 * 玩家操作列表工具类，用于序列化和反序列化操作列表。
 */
public class PlayerActionListUtil {

    private static final String ACTIONS_KEY = "Actions";
    private static final String ACTION_STRING_KEY = "ActionStr";

    /**
     * 将操作列表写入NBT。
     */
    public static void writeActionsToNbt(CompoundTag nbt, List<PlayerAction> actions) {
        ListTag actionsList = new ListTag();

        for (PlayerAction action : actions) {
            CompoundTag actionNbt = new CompoundTag();
            actionNbt.putString(ACTION_STRING_KEY, action.toString());
            actionsList.add(actionNbt);
        }

        nbt.put(ACTIONS_KEY, actionsList);
    }

    /**
     * 从NBT读取操作列表。
     */
    public static List<PlayerAction> readActionsFromNbt(CompoundTag nbt) {
        List<PlayerAction> actions = new ArrayList<>();

        if (nbt.contains(ACTIONS_KEY, Tag.TAG_LIST)) {
            ListTag actionsList = nbt.getList(ACTIONS_KEY, Tag.TAG_COMPOUND);

            for (int i = 0; i < actionsList.size(); i++) {
                CompoundTag actionNbt = actionsList.getCompound(i);
                String actionStr = actionNbt.getString(ACTION_STRING_KEY);

                try {
                    PlayerAction action = PlayerAction.fromString(actionStr);
                    actions.add(action);
                } catch (Exception e) {
                    // 记录错误但继续处理其他操作
                    TWCore.LOGGER.warn("Unable to resolve the operation string: {}", actionStr, e);
                }
            }
        }

        return actions;
    }

    /**
     * 将操作列表转换为物品堆栈列表（用于库存接口兼容）。
     */
    public static List<ItemStack> actionsToItemStacks(List<PlayerAction> actions) {
        List<ItemStack> stacks = new ArrayList<>();

        for (PlayerAction action : actions) {
            stacks.add(action.toItemStack());
        }

        return stacks;
    }
}