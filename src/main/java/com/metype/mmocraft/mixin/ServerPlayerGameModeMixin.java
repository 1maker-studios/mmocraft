package com.metype.mmocraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.MiningSkill;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerGameModeMixin {

    @Shadow protected ServerWorld world;
    @Final
    @Shadow protected ServerPlayerEntity player;

    @WrapOperation(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;afterBreak(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/item/ItemStack;)V"))
    public void breakBlock(Block instance, World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, Operation<Void> original) {
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(this.player);
        if(mmoPlayer == null) {
            original.call(instance, world, player, pos, state, blockEntity, tool);
            return;
        }

        if(MiningSkill.playerMineHandler(mmoPlayer, (ServerWorld) world, pos, state)) {
            original.call(instance, world, player, pos, state, blockEntity, tool);
        }

        original.call(instance, world, player, pos, state, blockEntity, tool);
    }

    @WrapOperation(method="interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult placeBlock(ItemStack instance, ItemUsageContext context, Operation<ActionResult> original, @Local() BlockPos blockPos) {
        ActionResult result = original.call(instance, context);
        if(result instanceof ActionResult.Success success) {
            if(success.shouldIncrementStat()) {
                //TODO: Actually log placed blocks and remove them from the XP earning pool
            }
        }
        return result;
    }
}
