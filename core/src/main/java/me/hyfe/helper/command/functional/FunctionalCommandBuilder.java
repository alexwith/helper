package me.hyfe.helper.command.functional;

import me.hyfe.helper.command.command.Command;
import me.hyfe.helper.command.command.SubCommand;
import me.hyfe.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// methods can't be abstracted due to generics
public class FunctionalCommandBuilder<T extends CommandSender> extends AbstractCommandBuilder<T> {
    private String[] aliases;
    private SubCommand<?>[] subs;

    public FunctionalCommandBuilder(Class<?> senderClass, String permission, String description, String[] aliases) {
        super(senderClass, permission, description);
        this.aliases = aliases;
    }

    public FunctionalCommandBuilder<T> aliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    public FunctionalCommandBuilder<T> description(String description) {
        this.description = description;
        return this;
    }

    public FunctionalCommandBuilder<T> permission(String permission) {
        this.permission = permission;
        return this;
    }

    public FunctionalCommandBuilder<Player> player() {
        return new FunctionalCommandBuilder<>(this.senderClass, this.permission, this.description, this.aliases);
    }

    public FunctionalCommandBuilder<T> subs(SubCommand<?>... subs) {
        this.subs = subs;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Command<T> handler(FunctionalCommandHandler<T, CommandContext> handler) {
        Command<T> command = new Command<T>((Class<T>) this.senderClass, this.permission, this.description, this.aliases) {

            @Override
            public void handle(T sender, CommandContext context) {
                handler.handle(sender, context);
            }
        };
        if (this.subs != null) {
            command.setSubs(this.subs);
        }
        command.register();
        return command;
    }
}