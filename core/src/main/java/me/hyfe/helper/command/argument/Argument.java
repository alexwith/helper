package me.hyfe.helper.command.argument;

import com.google.common.collect.Sets;
import me.hyfe.helper.command.tabcomplete.TabResolver;

import java.util.Collections;
import java.util.Set;

public class Argument<T> {
    private final ArgumentType<T> type;
    private final String argument;
    private final Set<String> aliases;
    private final TabResolver tabResolver;

    public Argument(ArgumentType<T> type, String argument, Set<String> aliases, TabResolver tabResolver) {
        this.type = type;
        this.argument = argument;
        this.aliases = aliases;
        this.tabResolver = tabResolver == null ? (sender) -> Collections.singletonList(argument) : tabResolver;
    }

    public Argument(ArgumentType<T> type, String argument, String... aliases) {
        this(type, argument, Sets.newHashSet(argument), (sender) -> Collections.singletonList(argument));
    }

    public ArgumentType<T> getType() {
        return this.type;
    }

    public String getArgument() {
        return this.argument;
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public TabResolver getTabResolver() {
        return this.tabResolver;
    }
}
