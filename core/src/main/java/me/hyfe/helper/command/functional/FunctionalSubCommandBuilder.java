package me.hyfe.helper.command.functional;

import com.google.common.collect.Sets;
import me.hyfe.helper.command.argument.Argument;
import me.hyfe.helper.command.argument.ArgumentTypes;
import me.hyfe.helper.command.command.SubCommand;
import me.hyfe.helper.command.context.SubCommandContext;
import me.hyfe.helper.command.tabcomplete.TabResolver;
import me.hyfe.helper.command.tabcomplete.TabResolvers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// methods can't be abstracted due to generics
public class FunctionalSubCommandBuilder<T extends CommandSender> extends AbstractCommandBuilder<T> {
    private final List<Argument<?>> arguments = new ArrayList<>();
    private boolean endless;

    public FunctionalSubCommandBuilder(Class<?> senderClass, String permission, String description, boolean endless) {
        super(senderClass, permission, description);
        this.endless = endless;
    }

    public FunctionalSubCommandBuilder<T> description(String description) {
        this.description = description;
        return this;
    }

    public FunctionalSubCommandBuilder<T> permission(String permission) {
        this.permission = permission;
        return this;
    }

    public FunctionalSubCommandBuilder<Player> player() {
        this.senderClass = Player.class;
        return new FunctionalSubCommandBuilder<>(this.senderClass, this.permission, this.description, this.endless);
    }

    public FunctionalSubCommandBuilder<T> endless() {
        this.endless = true;
        return this;
    }

    public FunctionalSubCommandBuilder<T> argument(String name, String... aliases) {
        return this.argument(null, name, null, aliases);
    }

    public <U> FunctionalSubCommandBuilder<T> argument(Class<U> clazz, String name, String... aliases) {
        return this.argument(clazz, name, TabResolvers.getResolver(clazz), aliases);
    }

    public <U> FunctionalSubCommandBuilder<T> argument(Class<U> clazz, String name, TabResolver tabResolver, String... aliases) {
        this.arguments.add(new Argument<U>(ArgumentTypes.getType(clazz), name, Sets.newHashSet(aliases), tabResolver));
        return this;
    }

    @SuppressWarnings("unchecked")
    public SubCommand<T> handler(FunctionalCommandHandler<T, SubCommandContext> handler) {
        SubCommand<T> command = new SubCommand<T>((Class<T>) this.senderClass, this.permission, this.description, this.endless) {

            @Override
            public void handleSub(T sender, SubCommandContext context) {
                handler.handle(sender, context);
            }
        };
        command.getArguments().addAll(this.arguments);
        return command;
    }
}