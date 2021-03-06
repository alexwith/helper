package me.hyfe.helper.command;

import me.hyfe.helper.command.context.CommandContext;
import me.hyfe.helper.command.context.ImmutableCommandContext;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.utils.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Set;

public abstract class AbstractCommand implements Command, CommandExecutor {
    protected Set<Command> subs;
    protected String permission;
    protected String permissionMessage;
    protected String description;
    protected String usage;
    protected String failureMessage;

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public String getFailureMessage() {
        return this.failureMessage;
    }

    @Override
    public void register(String... aliases) {
        CommandMapUtil.registerCommand(LoaderUtils.getPlugin(), this, permission, permissionMessage, description, aliases);
    }

    @Override
    public void close() {
        CommandMapUtil.unregisterCommand(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args);
        try {
            this.call(context);
        } catch (CommandInterruptException ex) {
            ex.getAction().accept(context.sender());
        }
        return true;
    }
}