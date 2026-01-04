package com.metype.mmocraft.command.provider;

import com.metype.mmocraft.skill.Skill;
import com.metype.mmocraft.skill.SkillManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkillIDProvider implements SuggestionProvider<ServerCommandSource> {
    private SkillIDProvider() {

    }
//
//    public static SkillIDProvider personal() {
//        SkillIDProvider provider = new SkillIDProvider();
//        provider.suggestAllRanks = false;
//        return provider;
//    }

    public static SkillIDProvider all() {
        return new SkillIDProvider();
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        List<Skill> skill = SkillManager.getSkills();

        String input = builder.getRemainingLowerCase();
        for(Skill s : skill) {
            if(s.getID().toString().contains(input)) {
                builder.suggest(s.getID().toString());
            }
        }
        return builder.buildFuture();
    }
}
