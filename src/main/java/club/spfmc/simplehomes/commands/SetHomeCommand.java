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

package club.spfmc.simplehomes.commands;

import club.spfmc.simplehomes.SimpleHomes;
import club.spfmc.simplehomes.home.Home;
import club.spfmc.simplehomes.home.Homes;
import club.spfmc.simplehomes.home.HomesManager;
import club.spfmc.simplehomes.util.command.SimpleCommand;
import club.spfmc.simplehomes.util.yaml.Yaml;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends SimpleCommand {

    private final SimpleHomes simpleHomes;

    public SetHomeCommand(SimpleHomes simpleHomes) {
        super(simpleHomes, "SetHome");
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void onPlayerExecute(Player player, Command command, String[] arguments) {
        Yaml messages = simpleHomes.getMessages();
        if (player.hasPermission("simple.set.home")) {
            if (arguments.length > 0) {
                HomesManager homesManager = simpleHomes.getHomesManager();
                int maxHomes = 0;
                for (Integer key:homesManager.getMaxHomes().keySet()) {
                    String name = homesManager.getMaxHomes().get(key);
                    if (name.equals("default") || player.hasPermission("simple.max.homes." + name) ) {
                        maxHomes = key;
                    }
                }
                Homes homes = homesManager.getHomes(player.getName());
                if (homes.getHomes().size() < maxHomes || homes.containHome(arguments[0])) {
                    Location location = player.getLocation();
                    Home home = new Home(player.getName(), arguments[0]);
                    home.setWorld(location.getWorld().getName());
                    home.setX(location.getX());
                    home.setY(location.getY());
                    home.setZ(location.getZ());
                    home.setYaw(location.getYaw());
                    home.setPitch(location.getPitch());
                    if (homes.containHome(home.getName())) {
                        messages.sendMessage(player, "setHome.overwritten", new String[][]{{"%home%", arguments[0]}});
                    } else {
                        messages.sendMessage(player, "setHome.set", new String[][]{{"%home%", arguments[0]}});
                    }
                    homes.addHome(home);
                } else {
                    messages.sendMessage(player, "setHome.maxHomesReached");
                }
            } else {
                messages.sendMessage(player, "setHome.emptyName");
            }
        } else {
            messages.sendMessage(player, "setHome.noPermission");
        }
    }

    @Override
    public void onConsoleExecute(ConsoleCommandSender console, Command command, String[] arguments) {
        Yaml messages = simpleHomes.getMessages();
        messages.sendMessage(console, "setHome.isConsole");
    }

}