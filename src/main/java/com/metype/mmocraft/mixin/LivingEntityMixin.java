package com.metype.mmocraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.*;
import com.metype.mmocraft.trait.acrobatics.RollTrait;
import com.metype.mmocraft.util.UIUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Random;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    protected int itemUseTimeLeft;

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

        if(rollTrait != null && rollTrait.modulate(0,0) > rng.nextDouble()) {
            fallDamage *= 0.3;
            UIUtil.showTraitAction(mmoSelf, rollTrait.getActionMessage());
        }

        original.call(instance, damageSource, (float) fallDamage);
    }

    @WrapMethod(method="damage")
    public boolean damage(ServerWorld world, DamageSource source, float damage, Operation<Boolean> original) {
        Entity attackingEntity = source.getAttacker();
        if(attackingEntity == null || !attackingEntity.isPlayer()) {
            return original.call(world, source, damage);
        }

        LivingEntity victimEntity = (LivingEntity) (Object) (this);

        if(victimEntity.isInvulnerableTo(world, source)) return original.call(world, source, damage);

        ServerPlayerEntity attackingPlayer = (ServerPlayerEntity) attackingEntity;
        MMOPlayer attackingMMOPlayer = PlayerAdapter.getPlayer(attackingPlayer);
        ItemStack heldItem = attackingPlayer.getStackInHand(Hand.MAIN_HAND);

        if(!source.isDirect()) {
            if(heldItem.isOf(Items.BOW)) {
                damage = ArcherySkill.playerShootHandler(attackingMMOPlayer, victimEntity, damage);
            }
        } else {
            if (heldItem.isIn(ItemTags.SWORDS)) {
                damage = SwordsSkill.playerSwordHandler(attackingMMOPlayer, damage);
            } else if (heldItem.isIn(ItemTags.AXES)) {
                damage = AxesSkill.playerAxeHandler(attackingMMOPlayer, damage);
            } else if (!heldItem.isIn(ItemTags.WEAPON_ENCHANTABLE)) {
                damage = UnarmedSkill.playerUnarmedHandler(attackingMMOPlayer, damage);
            }
        }

        return original.call(world, source, damage);
    }
}
