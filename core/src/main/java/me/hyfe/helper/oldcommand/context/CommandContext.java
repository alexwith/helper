package me.hyfe.helper.oldcommand.context;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.oldcommand.argument.Argument;
import me.hyfe.helper.text.Text;
import org.bukkit.command.CommandSender;

public interface CommandContext<T extends CommandSender> {

    T sender();

    default void reply(String... message) {
        Text.send(this.sender(), message);
    }

    ImmutableList<String> args();

    Argument arg(int index);

    String rawArg(int index);

    String label();
}