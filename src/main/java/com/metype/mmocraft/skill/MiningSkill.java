package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.trait.ITrait;
import com.metype.mmocraft.util.LedgerUtil;
import com.metype.mmocraft.util.UIUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

public class MiningSkill implements ISkill {

    public static final Identifier ID = Identifier.of("mmocraft:mining");;

    private int level = 0;
    private int xp = 0;
    private int sp = 0;
    private MMOPlayer player;

    private final Map<Identifier, ITrait> traits = new HashMap<>();

    public MiningSkill(boolean init){}

    public MiningSkill() {
        for(ITrait trait : SkillManager.getTraitsForSkill(ID)) {
            traits.put(trait.getID(), trait);
            trait.associatePlayer(player);
        }
    }

    public MiningSkill(MMOPlayer associatedPlayer, int level, int xp, int sp) {
        this.level = level;
        this.xp = xp;
        this.sp = sp;
        this.player = associatedPlayer;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getExperience() {
        return this.xp;
    }

    @Override
    public int getExperienceUntilNextLevel() {
        return (int) (100 + Math.pow(this.level, 2));
    }

    @Override
    public int getSkillPointsAvailable() {
        return this.sp;
    }

    @Override
    public void giveExperience(int amount) {
        Audience player = PlayerAdapter.getPlayer(this.player);
        xp += amount;
        while(xp >= getExperienceUntilNextLevel()) { // We don't want "100/100" XP, so >= lol
            xp -= getExperienceUntilNextLevel();
            level++;
            player.playSound(Sound.sound(SoundEvents.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 1.0f, 1.0f));
            UIUtil.showLevelUpMessage(this.player, this);
            if(level % 3 == 0) {
                sp++;
                UIUtil.showSkillPointGainMessage(this.player, this);
            }
        }
        UIUtil.showSkillBossBar(this.player, this);
    }

    @Override
    public void associatePlayer(MMOPlayer player) {
        this.player = player;
        for(ITrait trait: getTraits()) {
            trait.associatePlayer(player);
        }
    }

    @Override
    public void useSkillPoint() {
        this.sp--;
    }

    @Override
    public String getName() {
        return "Mining";
    }

    @Override
    public Collection<ITrait> getTraits() {
        return traits.values().stream().toList();
    }

    @Override
    public ITrait getTrait(Identifier ID) {
        return traits.getOrDefault(ID, null);
    }

    @Override
    public MiningSkill copy() {
        return new MiningSkill();
    }

    @Override
    public Identifier getID() {
        return ID;
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
            entry(Blocks.DIAMOND_ORE, 20),
            entry(Blocks.DEEPSLATE_DIAMOND_ORE, 20),
            entry(Blocks.NETHER_QUARTZ_ORE, 10),
            entry(Blocks.NETHERRACK, 1),
            entry(Blocks.BLACKSTONE, 3),
            entry(Blocks.BASALT, 3),
            entry(Blocks.OBSIDIAN, 12),
            entry(Blocks.ANCIENT_DEBRIS, 40)
    );

    public static boolean playerMineHandler(@NotNull MMOPlayer player, ServerWorld world, BlockPos position, BlockState block) {
        MiningSkill skill = (MiningSkill) player.getSkill(ID);
        if(skill == null) return false;
        Integer xpGive = blockXP.getOrDefault(block.getBlock(), null);
        if(xpGive == null) return false;

        if(LedgerUtil.getPlayerPlacedBlock(world, position)) return false;

        skill.giveExperience(xpGive);

        player.save();

        Random rng = new Random();
        return skill.getLevel() / 1000.0f > rng.nextDouble();
    }

    @Override
    public String serialize() {
        return "mining=" + level + "," + xp + "," + sp;
    }

    @Override
    public boolean deserialize(String data) {
        for(ITrait trait : SkillManager.getTraitsForSkill(ID)) {
            traits.put(trait.getID(), trait);
            trait.associatePlayer(player);
        }
        if(data == null) return false;
        String[] dataSplitOnEQ = data.split("=");
        if(dataSplitOnEQ.length < 2) return false;
        if(!dataSplitOnEQ[0].equalsIgnoreCase("mining")) return false;
        String[] params = dataSplitOnEQ[1].split(",");
        if(params.length < 3) return false;
        level = Integer.parseInt(params[0]);
        xp = Integer.parseInt(params[1]);
        sp = Integer.parseInt(params[2].split("\\|")[0]);
        String[] traitData = data.split("\\|");
        for(ITrait trait : getTraits()) {
            for(String traitInfo: traitData) {
                if (trait.deserialize(traitInfo)) break;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Gain XP by mining, more XP for more precious blocks. Passive chance to drop a second block based on level.";
    }
}
