package me.hyfe.helper.command.context;

import com.google.common.collect.Sets;
import me.hyfe.helper.command.argument.Argument;
import me.hyfe.helper.command.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SubCommandContext extends CommandContext {
    private final List<Argument<?>> arguments;
    private final boolean endless;

    public SubCommandContext(String[] args, SubCommand<?> sub) {
        super(null, args);
        this.arguments = sub.getArguments();
        this.endless = sub.isEndless();
    }

    @SuppressWarnings("unchecked")
    public <T> T arg(int index) {
        return ((Argument<T>) this.arguments.get(index)).getType().parse(this.args[index]);
    }

    public String rawArg(int index) {
        return this.args[index];
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
            if (!this.matchArgument(i)) {
                return false;
            }
        }
        return true;
    }

    public List<String> suggestions(CommandSender sender, int index) {
        if (index > this.arguments.size() - 1) {
            return Collections.emptyList();
        }
        return this.arguments.get(index).getTabResolver().resolve(sender);
    }

    public boolean matchArgument(int index) {
        if (this.arguments.size() - 1 < index && this.endless) {
            return true;
        }
        if (this.arguments.size() - 1 < index) {
            return false;
        }
        Argument<?> argument = this.arguments.get(index);
        if (argument.getType() == null) {
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
