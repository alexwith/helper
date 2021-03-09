package me.hyfe.helper.oldcommand.functional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import me.hyfe.helper.oldcommand.Command;
import me.hyfe.helper.oldcommand.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

class FunctionalCommandBuilderImpl<T extends CommandSender> implements FunctionalCommandBuilder<T> {
    private final ImmutableList.Builder<Predicate<CommandContext<?>>> predicates;
    private final Set<Command> subs = new LinkedHashSet<>();
    private final Set<String> help = Sets.newLinkedHashSet();

    private String permission;
    private String usage;
    private String description;
    private String permissionMessage;

    private FunctionalCommandBuilderImpl(ImmutableList.Builder<Predicate<CommandContext<?>>> predicates, String permission, String usage, String description, String permissionMessage) {
        this.predicates = predicates;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.permissionMessage = permissionMessage;
    }

    FunctionalCommandBuilderImpl() {
        this(ImmutableList.builder(), null, null, null, null);
    }

    public FunctionalCommandBuilder<T> description(String description) {
        Objects.requireNonNull(description, "description");
        this.description = description;
        return this;
    }

    public FunctionalCommandBuilder<T> bindSubs(Command... commands) {
        Objects.requireNonNull(commands, "commands");
        this.subs.clear();
        for (Command command : commands) {
            this.subs.add(command);
            this.help.add();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test) {
        this.predicates.add((Predicate<CommandContext<?>>) test);
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertPermission(String permission, String failureMessage) {
        Objects.requireNonNull(permission, "permission");
        this.permission = permission;
        this.permissionMessage = failureMessage;
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertOp(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender().isOp()) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<Player> assertPlayer(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof Player) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return new FunctionalCommandBuilderImpl<>(this.predicates, this.permission, this.usage, this.description, this.help, this.permissionMessage);
    }

    @Override
    public FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof ConsoleCommandSender) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return new FunctionalCommandBuilderImpl<>(this.predicates, this.permission, this.usage, this.description, this.help, this.permissionMessage);
    }

    @Override
    public FunctionalCommandBuilder<T> assertUsage(String usage, String failureMessage) {
        Objects.requireNonNull(usage, "usage");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.usage = usage;
        this.help = failureMessage;
        List<String> usageParts = Splitter.on(" ").splitToList(usage);
        List<String> flatArgs = new ArrayList<>();
        for (String usagePart : usageParts) {
            if (!usagePart.startsWith("[") && !usagePart.endsWith("]")) {
                flatArgs.add(usagePart);
            }
        }
        this.predicates.add(context -> {
            if (context.args().size() >= usageParts.size() && context.args().containsAll(flatArgs)) {
                return true;
            }
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertArgument(int index, Predicate<String> test, String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            String arg = context.rawArg(index);
            if (test.test(arg)) {
                return true;
            }
            context.reply(failureMessage.replace("{arg}", arg).replace("{index}", Integer.toString(index)));
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertSender(Predicate<T> test, String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            T sender = (T) context.sender();
            if (test.test(sender)) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @Override
    public Command handler(FunctionalCommandHandler handler) {
        Objects.requireNonNull(handler, "handler");
        return new FunctionalCommand(this.predicates.build(), this.subs, handler, this.permission, this.usage, this.description, this.help, this.permissionMessage);
    }

    private void createHelp() {
        this.help.add("");
        this.help.add("/".concat());
    }
}