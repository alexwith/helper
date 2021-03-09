package me.hyfe.helper.command.tabcomplete;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabResolver {

    List<String> resolve(CommandSender sender);
}
