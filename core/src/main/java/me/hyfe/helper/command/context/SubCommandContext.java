package me.hyfe.helper.command.context;

import com.google.common.collect.Sets;
import me.hyfe.helper.command.argument.Argument;
import me.hyfe.helper.command.command.SubCommand;

import java.util.List;
import java.util.Set;

public class SubCommandContext extends CommandContext {
    private final List<Argument<?>> arguments;
    private final boolean endless;

    public SubCommandContext(String[] args, SubCommand<?> sub) {
        super(args);
        this.arguments = sub.getArguments();
        this.endless = sub.isEndless();
    }

    public String[] endlessResult() {
        Set<String> end = Sets.newLinkedHashSet();
        for (int i = 0; i < this.args.length; i++) {
            if (i < this.arguments.size() - 1) {
                continue;
            }
            end.add(this.args[i]);
        }
        return end.toArray(new String[]{});
    }

    public boolean matchUntil(int index) {
        for (int i = 0; i < index; i++) {
            if (!this.matchArgument(index)) {
                return false;
            }
        }
        return true;
    }

    public boolean matchArgument(int index) {
        if (this.arguments.size() - 1 < index && this.endless) {
            return true;
        }
        if (this.arguments.size() - 1 < index) {
            return false;
        }
        Argument<?> argument = this.arguments.get(index);
        if (argument.getArgument() == null) {
            for (String alias : argument.getAliases()) {
                if (this.args[index].equalsIgnoreCase(alias)) {
                    return true;
                }
            }
            return this.args[index].equalsIgnoreCase(argument.getArgument());
        }
        return true;
    }
}
