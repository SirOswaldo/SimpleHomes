/*
 * Copyright (C) 2021  SirOswaldo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.kayteam.simplehomes.commands;

import org.kayteam.api.command.SimpleCommand;
import org.kayteam.api.updatechecker.UpdateChecker;
import org.kayteam.api.yaml.Yaml;
import org.kayteam.simplehomes.SimpleHomes;
import org.kayteam.simplehomes.inventories.SimpleHomesInventory;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleHomesCommand extends SimpleCommand {

    private final SimpleHomes simpleHomes;

    public SimpleHomesCommand(SimpleHomes simpleHomes) {
        super("SimpleHomes");
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void onPlayerExecute(Player player, String[] arguments) {
        Yaml messages = simpleHomes.getMessages();
        if (player.hasPermission("simple.admin.homes")) {
            if (arguments.length > 0) {
                switch (arguments[0].toLowerCase()) {
                    case "gui":
                        simpleHomes.getInventoryManager().openInventory(player, new SimpleHomesInventory(simpleHomes, player));
                        break;
                    case "reload":
                        simpleHomes.onReload();
                        messages.sendMessage(player, "admin.reloaded", new String[][] {{"%plugin%", simpleHomes.getDescription().getName()}});
                        break;
                    case "version":
                        messages.sendMessage(player, "admin.version", new String[][] {
                                {"%version%", simpleHomes.getDescription().getVersion()}
                        });
                        if (simpleHomes.getUpdateChecker().getUpdateCheckResult().equals(UpdateChecker.UpdateCheckResult.OUT_DATED)) {
                            simpleHomes.getUpdateChecker().sendOutDatedMessage(player);
                        }
                        break;
                    case "help":
                        messages.sendMessage(player, "admin.help");
                        break;
                    default:
                        messages.sendMessage(player, "admin.invalidOption", new String[][] {
                                {"%option%", arguments[0]}
                        });
                }
            } else {
                simpleHomes.getInventoryManager().openInventory(player, new SimpleHomesInventory(simpleHomes, player));
            }
        } else {
            messages.sendMessage(player, "admin.noPermission");
        }
    }

    @Override
    public List<String> onPlayerTabComplete(Player player, String[] arguments) {
        List<String> options = new ArrayList<>();
        if (arguments.length == 1) {
            if (player.hasPermission("simple.home")) {
                options.add("gui");
                options.add("reload");
                options.add("version");
                options.add("help");
            }
        }
        return options;
    }

    @Override
    public void onConsoleExecute(ConsoleCommandSender console, String[] arguments) {
        Yaml messages = simpleHomes.getMessages();
        if (arguments.length > 0) {
            switch (arguments[0].toLowerCase()) {
                case "reload":
                    // Files
                    simpleHomes.getSettings().reloadFileConfiguration();
                    messages.reloadFileConfiguration();
                    // MaxHomes
                    simpleHomes.getHomesManager().loadMaxHomes();
                    // Players
                    for (Player player:simpleHomes.getServer().getOnlinePlayers()) {
                        player.closeInventory();
                        simpleHomes.getHomesManager().unloadHomes(player.getName());
                    }
                    for (Player player:simpleHomes.getServer().getOnlinePlayers()) {
                        simpleHomes.getHomesManager().loadHomes(player);
                    }
                    messages.sendMessage(console, "admin.reloaded", new String[][] {
                            {"%plugin%", simpleHomes.getDescription().getName()}
                    });
                    break;
                case "version":
                    messages.sendMessage(console, "admin.version", new String[][] {
                            {"%version%", simpleHomes.getDescription().getVersion()}
                    });
                    if (simpleHomes.getUpdateChecker().getUpdateCheckResult().equals(UpdateChecker.UpdateCheckResult.OUT_DATED)) {
                        simpleHomes.getUpdateChecker().sendOutDatedMessage(console);
                    }
                    break;
                case "help":
                    messages.sendMessage(console, "admin.help");
                    break;
                default:
                    messages.sendMessage(console, "admin.invalidOption", new String[][] {
                            {"%option%", arguments[0]}
                    });
            }
        } else {
            messages.sendMessage(console, "admin.emptyOption");
        }
    }

    @Override
    public List<String> onConsoleTabComplete(ConsoleCommandSender console, String[] arguments) {
        List<String> options = new ArrayList<>();
        if (arguments.length == 1) {
            options.add("reload");
            options.add("version");
            options.add("help");
        }
        return options;
    }
}