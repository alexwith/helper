package me.hyfe.helper.command.context;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.command.argument.Argument;
import me.hyfe.helper.command.argument.SimpleArgument;
import org.bukkit.command.CommandSender;

public class ImmutableCommandContext<T extends CommandSender> implements CommandContext<T> {
    private final T sender;
    private final String label;
    private final ImmutableList<String> args;

    public ImmutableCommandContext(T sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = ImmutableList.copyOf(args);
    }

    @Override
    public T sender() {
        return this.sender;
    }

    @Override
    public ImmutableList<String> args() {
        return this.args;
    }

    @Override
    public Argument arg(int index) {
        return new SimpleArgument(index, rawArg(index));
    }

    @Override
    public String rawArg(int index) {
        if (index < 0 || index >= this.args.size()) {
            return null;
        }
        return this.args.get(index);
    }

    @Override
    public String label() {
        return this.label;
    }
}