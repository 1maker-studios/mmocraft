package com.metype.mmocraft.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.Map;
import java.util.Optional;

public class CommandUtils {

    public static final int COMMAND_FAILURE = 0;
    public static final int COMMAND_SUCCESS = 1;

    public static Optional<ServerPlayerEntity> commandContextServerPlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(!source.isExecutedByPlayer()) {
            return Optional.empty();
        }
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(player);
    }

    private static void appendLore(ItemStack stack, MutableText line) {
        LoreComponent existing = stack.get(DataComponentTypes.LORE);
        if (existing == null) {
            existing = LoreComponent.DEFAULT;
        }
        existing = existing.with(line);
        stack.set(DataComponentTypes.LORE, existing);
    }

    public static String format(String input, Map<String, Object> args) {
        for(String argID : args.keySet()) {
            if(!input.contains("{" + argID  + "}")) continue;
            input = input.replaceFirst("\\{" + argID  + "}", args.get(argID).toString());
        }
        return input;
    }
}
