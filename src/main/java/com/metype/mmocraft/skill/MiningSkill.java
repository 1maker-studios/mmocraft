package com.metype.mmocraft.skill;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

public class MiningSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:mining");

    protected MiningSkill(boolean flag) {
        super();
        name = "Mining";
        description = "Gain XP by mining, more XP for more precious blocks. Passive chance to drop a second block based on level.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static MiningSkill defaultSkill() {
        return new MiningSkill(false);
    }

    @Override
    public MiningSkill copy() {
        return new MiningSkill(true);
    }

    private static final Map<Block, Integer> blockXP = Map.ofEntries(
            entry(Blocks.STONE, 1),
            entry(Blocks.GRANITE, 1),
            entry(Blocks.DIORITE, 1),
            entry(Blocks.ANDESITE, 1),
            entry(Blocks.DEEPSLATE, 2),
            entry(Blocks.TUFF, 3),
            entry(Blocks.COAL_ORE, 5),
            entry(Blocks.DEEPSLATE_COAL_ORE, 5),
            entry(Blocks.COPPER_ORE, 5),
            entry(Blocks.DEEPSLATE_COPPER_ORE, 5),
            entry(Blocks.IRON_ORE, 7),
            entry(Blocks.DEEPSLATE_IRON_ORE, 7),
            entry(Blocks.GOLD_ORE, 10),
            entry(Blocks.DEEPSLATE_GOLD_ORE, 10),
            entry(Blocks.NETHER_GOLD_ORE, 12),
            entry(Blocks.LAPIS_ORE, 17),
            entry(Blocks.DEEPSLATE_LAPIS_ORE, 17),
            entry(Blocks.REDSTONE_ORE, 17),
            entry(Blocks.DEEPSLATE_REDSTONE_ORE, 17),
            entry(Blocks.DIAMOND_ORE, 20),
            entry(Blocks.DEEPSLATE_DIAMOND_ORE, 20),
            entry(Blocks.NETHER_QUARTZ_ORE, 10),
            entry(Blocks.NETHERRACK, 1),
            entry(Blocks.END_STONE, 3),
            entry(Blocks.BLACKSTONE, 3),
            entry(Blocks.BASALT, 3),
            entry(Blocks.OBSIDIAN, 12),
            entry(Blocks.ANCIENT_DEBRIS, 40)
    );

    public static void playerMineHandler(@NotNull MMOPlayer mmoPlayer, ServerWorld world, BlockPos position, BlockState block) {
        MiningSkill skill = (MiningSkill) mmoPlayer.getSkill(ID);
        if(skill == null) return;
        Integer xpGive = blockXP.getOrDefault(block.getBlock(), null);
        if(xpGive == null) return;

        ServerPlayerEntity player = PlayerAdapter.getPlayer(mmoPlayer);
        ItemStack mainHandStack = player.getMainHandStack();

        new Thread(() -> {
            if(MMOCraft.CHUNK_MANAGER.isInvalid(world, position)) return;
            skill.giveExperience(xpGive);

            Random rng = new Random();
            if(skill.doubleDropChance() > rng.nextDouble()) {
                Utils.registerTickAction(server -> {
                    block.getBlock().afterBreak(world, player, position, block, world.getBlockEntity(position), mainHandStack);
                    return false;
                });
            }
        }).start();
    }

    private float doubleDropChance() {
        return getLevel() / 1000.0f;
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Double Drops", "Gives a chance to get a second drop from mined blocks", "{name} {value}%", doubleDropChance() * 100)
        );
    }
}
