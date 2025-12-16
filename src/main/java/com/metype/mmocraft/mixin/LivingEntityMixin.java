package com.metype.mmocraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.AcrobaticsSkill;
import com.metype.mmocraft.trait.acrobatics.RollTrait;
import com.metype.mmocraft.util.UIUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;serverDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    public void handleFallDamage(LivingEntity instance, DamageSource damageSource, float v, Operation<Void> original) {
        if(!instance.isPlayer()) {
            original.call(instance, damageSource, v);
            return;
        }

        ServerPlayerEntity self = MMOCraft.SERVER.getPlayerManager().getPlayer(instance.getUuid());
        if(self == null) {
            MMOCraft.LOGGER.warn("Entity says is player, but does not map to player! UUID: {}", instance.getUuid());
            original.call(instance, damageSource, v);
            return;
        }

        MMOPlayer mmoSelf = PlayerAdapter.getPlayer(self);
        double fallDamage = AcrobaticsSkill.playerFallHandler(mmoSelf, v);
        Random rng = new Random();

        RollTrait rollTrait = (RollTrait) mmoSelf.getSkill(AcrobaticsSkill.ID).getTrait(RollTrait.ID);

        if(rollTrait.modulate(0,0) > rng.nextDouble()) {
            fallDamage *= 0.3;
            UIUtil.showTraitAction(mmoSelf, rollTrait.getActionMessage());
        }

        original.call(instance, damageSource, (float) fallDamage);
    }
}
