/*
 * Copyright (C) 2014 CodeLanx , All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * along with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 * Manages commands abstractly for the plugin
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <T> The specific plugin to use
 */
public class CommandHandler<T extends Plugin> implements CommandExecutor {

    /** Private {@link Plugin} instance */
    private final T plugin;
    /** Private {@link HashMap} of subcommands */
    private final Map<String, SubCommand> commands = new HashMap<>();
    private final String command;

    /**
     * {@link CommandHandler} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param plugin The main {@link Nations} instance
     * @param command The command to write subcommands under
     */
    public CommandHandler(T plugin, String command) {
        this.plugin = plugin;
        
        this.command = command;
        
        final CommandHandler chand = this;
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(command);
        if (cmd == null) {
            throw new NullPointerException("Attempted to register a non-existant command!");
        } else {
            cmd.setExecutor(chand);
        }
    }

    /**
     * Executes the proper {@link SubCommand}
     *
     * @since 1.3.0
     * @version 1.4.2
     *
     * @param sender The command executor
     * @param cmd The command instance
     * @param commandLabel The command name
     * @param args The command arguments
     *
     * @return Success of command, false if no command is found
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 1 || (args.length == 1 && "help".equalsIgnoreCase(args[0]))) {
            args = new String[]{"help", "1"};
        }
        SubCommand command = this.getCommand(args[0]);
        if (command != null) {
            String[] newArgs = new String[args.length - 1];
            for (int i = 0; i < newArgs.length; i++) {
                newArgs[i] = args[i + 1];
            }
            if (command.execute(sender, newArgs)) {
                return true;
            } else {
                //sender.sendMessage(__("Usage: " + command.getUsage()));
                //sender.sendMessage(__(command.info()));
            }
        } else {
            sender.sendMessage("[Nations] Unknown Command");
        }
        return false;
    }
    
    /**
     * Returns a subcommand, or <code>null</code> if none exists.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param name The name of the subcommand
     * @return A relevant {@link Succommand}, or null if it does not exist
     */
    public SubCommand getCommand(String name) {
        return this.commands.get(name);
    }
    
    /**
     * Returns all subcommands as a {@link Collection}.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return A {@link Collection} of all registered {@link SubCommand}
     */
    public Collection<SubCommand> getCommands() {
        return this.commands.values();
    }

    /**
     * Returns a permissions check for
     * {@code <plugin-name>.cmd.<subcommand-name>}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param sender The {@link CommandSender} executing the command
     * @param cmd The {@link SubCommand} being executed
     * @return {@code true} if they have permission, false otherwise
     */
    public boolean hasPermission(CommandSender sender, SubCommand cmd) {
        return sender.hasPermission(this.plugin.getName() + ".cmd." + cmd.getName());
    }

    /**
     * Registers a {@link SubCommand} under "/nations"
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param command The {@link SubCommand} to register
     * @throws CommandInUseException If the command's name is already in use
     */
    public void registerSubCommand(SubCommand command) throws CommandInUseException {
        if (this.commands.containsKey(command.getName())) {
            throw new CommandInUseException();
        } else {
            this.commands.put(command.getName(), command);
        }
    }

    public String getMainCommand() {
        return this.command;
    }

    public T getPlugin() {
        return this.plugin;
    }

}