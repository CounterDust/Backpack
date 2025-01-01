package com.backpack.item;

import com.backpack.network.OpenBackpackMessage;
import com.backpack.network.PacketHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemModBackpack extends ItemMod {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    public ItemModBackpack(String name) {
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
    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);

        // 客户端只负责发送请求，不直接执行打开背包的操作
        if (worldIn.isRemote && !itemStack.isEmpty() && itemStack.getItem() instanceof ItemModBackpack) {
            int slotIndex = findSlotIndex(playerIn, handIn);
            try {
                // 发送请求给服务器，要求打开背包
                PacketHandler.sendToServer(new OpenBackpackMessage(slotIndex));
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            } catch (Exception e) {
                LOGGER.error("无法打开玩家 {} 的背包 GUI: {}", playerIn.getName(), e.getMessage());
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, itemStack);
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
