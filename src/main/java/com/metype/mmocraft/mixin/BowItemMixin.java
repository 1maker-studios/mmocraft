package com.metype.mmocraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.ArcherySkill;
import com.metype.mmocraft.trait.archery.SteadyAimTrait;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BowItem.class)
public class BowItemMixin {
    @WrapMethod(method="shoot")
    public void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, Operation<Void> original) {
        if(shooter.isPlayer()) {
            ServerPlayerEntity player = (ServerPlayerEntity) shooter;
            if(player.isSneaking()) {
                MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(player);
                SteadyAimTrait trait = (SteadyAimTrait) mmoPlayer.getSkill(ArcherySkill.ID).getTrait(SteadyAimTrait.ID);
                divergence *= (float) trait.modulate(divergence, 1);
            }
        }
        original.call(shooter, projectile, index, speed, divergence, yaw, target);
    }
}
