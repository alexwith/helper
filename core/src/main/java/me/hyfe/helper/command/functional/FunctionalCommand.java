package me.hyfe.helper.command.functional;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.command.AbstractCommand;
import me.hyfe.helper.command.CommandInterruptException;
import me.hyfe.helper.command.context.CommandContext;

import java.util.function.Predicate;

class FunctionalCommand extends AbstractCommand {
    private final ImmutableList<Predicate<CommandContext<?>>> predicates;
    private final FunctionalCommandHandler handler;

    FunctionalCommand(ImmutableList<Predicate<CommandContext<?>>> predicates, FunctionalCommandHandler handler, String permission, String permissionMessage, String description) {
        this.predicates = predicates;
        this.handler = handler;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.description = description;
    }

    @Override
    public void call(CommandContext<?> context) throws CommandInterruptException {
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return;
            }
        }
        this.handler.handle(context);
    }
}