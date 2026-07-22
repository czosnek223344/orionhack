package com.orionhack.addon.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.orionhack.addon.utils.Blacklist;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class BlacklistCommand extends Command {
    public BlacklistCommand() {
        super("blacklist", "Like friends, but you hate them.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("name", StringArgumentType.word()).executes(context -> {
            String name = StringArgumentType.getString(context, "name");
            Blacklist.add(name);
            info("Added to blacklist: " + name);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("name", StringArgumentType.word()).executes(context -> {
            String name = StringArgumentType.getString(context, "name");
            Blacklist.remove(name);
            info("Removed from blacklist: " + name);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("list").executes(context -> {
            info("Blacklisted: " + Blacklist.getList());
            return SINGLE_SUCCESS;
        }));
    }
}
