package com.metype.mmocraft.skill;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.command.trait.LumberjackCommand;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.trait.woodcutting.LumberjackTrait;
import com.metype.mmocraft.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class WoodcuttingSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:woodcutting");

    protected WoodcuttingSkill(boolean flag) {
        super();
        name = "Woodcutting";
        description = "Gain XP by chopping down trees.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static WoodcuttingSkill defaultSkill() {
        return new WoodcuttingSkill(false);
    }

    @Override
    public WoodcuttingSkill copy() {
        return new WoodcuttingSkill(true);
    }

    public static void playerChopHandler(@NotNull MMOPlayer player, ServerWorld world, BlockPos position, BlockState block) {
        if(!block.getBlock().getDefaultState().isIn(BlockTags.LOGS)) {
            return;
        }

        WoodcuttingSkill skill = (WoodcuttingSkill) player.getSkill(ID);

        if (MMOCraft.CHUNK_MANAGER.isInvalid(world, position)) return;

        LumberjackTrait lumberjack = (LumberjackTrait) skill.getTrait(LumberjackTrait.ID);

        if (lumberjack != null) {
            ServerPlayerEntity playerEntity = PlayerAdapter.getPlayer(player);
            boolean tryLj = false;
            if(LumberjackCommand.playersDesiringLumberjack.containsKey(player.uuid)) {
                long activationTime = LumberjackCommand.playersDesiringLumberjack.get(player.uuid);
                if(System.currentTimeMillis() - activationTime < 30000) {
                    tryLj = true;
                }
                LumberjackCommand.playersDesiringLumberjack.remove(player.uuid);
            }
            if (tryLj) {
                if (lumberjack.tryUseAbility(world.getServer())) {
                    playerEntity.sendMessage(
                            Text.of(
                                    CommandUtils.format(
                                            "Activated Lumberjack for {length} seconds!",
                                            Map.of("length", lumberjack.calculateLength() / 20)
                                    )
                            )
                    );
                } else {
                    playerEntity.sendActionBar(
                            Component.text(
                                    CommandUtils.format(
                                            "Lumberjack is on cooldown for {length} seconds.",
                                            Map.of("length", (lumberjack.getNextAvailable() - world.getServer().getTicks()) / 20)
                                    )
                            )
                    );
                }
            }
            lumberjack.playerBrokeLog(world, position, block);
        }

        Random rng = new Random();

        skill.giveExperience(rng.nextInt(3, 10));
    }
}
