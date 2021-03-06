package me.hyfe.helper.command.functional;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.command.AbstractCommand;
import me.hyfe.helper.command.Command;
import me.hyfe.helper.command.CommandInterruptException;
import me.hyfe.helper.command.context.CommandContext;

import java.util.Set;
import java.util.function.Predicate;

class FunctionalCommand extends AbstractCommand {
    private final ImmutableList<Predicate<CommandContext<?>>> predicates;
    private final FunctionalCommandHandler handler;

    FunctionalCommand(ImmutableList<Predicate<CommandContext<?>>> predicates, Set<Command> subs, FunctionalCommandHandler handler, String permission, String usage, String description, String failureMessage, String permissionMessage) {
        this.predicates = predicates;
        this.subs = subs;
        this.handler = handler;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.failureMessage = failureMessage;
        this.permissionMessage = permissionMessage;
    }

    public ImmutableList<Predicate<CommandContext<?>>> getPredicates() {
        return this.predicates;
    }

    public FunctionalCommandHandler getHandler() {
        return this.handler;
    }

    @Override
    public void call(CommandContext<?> context) throws CommandInterruptException {
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return;
            }
        }
        this.handler.handle(context);
        boolean handleExecuted = false;
        for (Command sub : this.subs) {
            FunctionalCommand functionalSub = (FunctionalCommand) sub;
            boolean allValid = true;
            for (Predicate<CommandContext<?>> predicate : functionalSub.getPredicates()) {
                if (!predicate.test(context)) {
                    allValid = false;
                    break;
                }
            }
            if (allValid) {
                functionalSub.getHandler().handle(context);
                handleExecuted = true;
            }
        }
        if (!handleExecuted) {
            for (Command sub : this.subs) {
                context.reply(sub.getFailureMessage().replace("{usage}", "/" + context.label() + " " + sub.getUsage()));
            }
        }
    }
}