package com.metype.mmocraft.mixin;

import com.metype.mmocraft.MMOCraft;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "postPlacement", at = @At("HEAD"))
    public void postPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld serverWorld = (ServerWorld) world;
        if(player != null) {
            MMOCraft.CHUNK_MANAGER.setInvalid(serverWorld, pos);
        } else {
            MMOCraft.CHUNK_MANAGER.setValid(serverWorld, pos);
        }
    }
}
