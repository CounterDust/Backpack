package com.backpack.item;

import com.Backpack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BackpackItem extends ModItem {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    public BackpackItem(String name) {
        super(name);
        // 背包物品的最大堆叠数为1
        this.setMaxStackSize(1);
    }

    /**
     * 当玩家右击物品时触发。
     *
     * @param worldIn  世界对象
     * @param playerIn 玩家对象
     * @param handIn   玩家用哪只手使用物品
     * @return 使用物品后的结果
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);

        if (!worldIn.isRemote && !itemStack.isEmpty() && itemStack.getItem() instanceof BackpackItem) {
            int slotIndex = findSlotIndex(playerIn, handIn);
            try {
                // 打开背包GUI
                playerIn.openGui(Backpack.INSTANCE, Backpack.GUI_ID_BACKPACK, worldIn, slotIndex, 0, 0);
            } catch (Exception e) {
                BackpackItem.LOGGER.error("Failed to open backpack GUI for player {}: {}", playerIn.getName(), e.getMessage());
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    /**
     * 查找当前手持物品的槽位索引。
     *
     * @param player 玩家对象
     * @param hand   玩家用哪只手使用物品
     * @return 槽位索引
     */
    private int findSlotIndex(EntityPlayer player, EnumHand hand) {
        // 40 是副手的槽位索引
        return hand == EnumHand.MAIN_HAND ? player.inventory.currentItem : 40;
    }
}
