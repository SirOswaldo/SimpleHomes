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

package org.kayteam.simplehomes.inventories;

import org.bukkit.entity.Player;
import org.kayteam.api.inventory.SimpleInventoryBuilder;
import org.kayteam.api.yaml.Yaml;
import org.kayteam.simplehomes.SimpleHomes;
import org.kayteam.simplehomes.home.Home;
import org.kayteam.simplehomes.home.Homes;
import org.kayteam.simplehomes.home.HomesManager;
import net.milkbowl.vault.economy.Economy;

public class DeleteHomeConfirmInventory extends SimpleInventoryBuilder {

    private final SimpleHomes simpleHomes;
    private final Home home;

    public DeleteHomeConfirmInventory(SimpleHomes simpleHomes, Home home, String from, Player player) {
        super(simpleHomes.getSettings(), "inventory.deleteConfirm", from, player);
        this.simpleHomes = simpleHomes;
        this.home = home;
    }

    @Override
    public void openLastInventory() { }

    @Override
    public String[][] getReplacements() {
        return new String[][]{
                {"%home-name%", home.getName()},
                {"%home-display-name%", home.getDisplayName()},
                {"%home-world-name%", home.getWorld()},
                {"%home-world-x%", Math.round(home.getX()) + ""},
                {"%home-world-y%", Math.round(home.getY()) + ""},
                {"%home-world-z%", Math.round(home.getZ()) + ""},
                {"%home-world-yaw%", Math.round(home.getYaw()) + ""},
                {"%home-world-pitch%", Math.round(home.getPitch()) + ""}
        };
    }

    @Override
    public void prosesAction(String action, Player player) {
        if (action.startsWith("[accept]")) {
            Yaml messages = simpleHomes.getMessages();
            String name = home.getName();
            String owner = home.getOwner();
            if (SimpleHomes.getEconomy() != null) {
                Economy economy = SimpleHomes.getEconomy();
                if (player.getName().equals(owner)) {
                    if (!player.hasPermission("simple.bypass.home.cost")) {
                        double cost = simpleHomes.getSettings().getDouble("vault.deleteHome", 0.0);
                        economy.depositPlayer(player, cost);
                    }
                }
            }
            HomesManager homesManager = simpleHomes.getHomesManager();
            Homes homes = homesManager.getHomes(home.getOwner());
            homes.removeHome(home.getName());
            if (player.getName().equals(home.getOwner())) {
                if (SimpleHomes.getEconomy() != null) {
                    messages.sendMessage(player,  "deleteHome.delete", new String[][]{
                            {"%home%", name},
                            {"%cost%", simpleHomes.getSettings().getDouble("vault.deleteHome", 0.0) + ""}});
                } else {
                    messages.sendMessage(player,  "deleteHome.delete", new String[][]{
                            {"%home%", name},
                            {"%cost%", "0.0"}
                    });
                }
            } else {
                messages.sendMessage(player, "deleteHome.deleteOther", new String[][] {
                        {"%home%", name},
                        {"%player%", owner}
                });
            }
            homesManager.saveHomes(owner);
            player.closeInventory();
            if (homes.getHomes().size() > 0) {
                if (getFrom().equals("gui")) {
                    simpleHomes.getInventoryManager().openInventory(player, new HomesInventory(simpleHomes, player, homes, 1));
                }
            } else {
                if (player.getName().equals(owner)) {
                    messages.sendMessage(player, "homes.emptyHomes");
                } else {
                    messages.sendMessage(player, "homes.emptyHomesOther", new String[][] {
                            {"%player%", owner}
                    });
                }
            }
        } else if (action.startsWith("[cancel]")) {
            player.closeInventory();
            if (getFrom().equals("gui")) {
                HomesManager homesManager = simpleHomes.getHomesManager();
                Homes homes = homesManager.getHomes(home.getOwner());
                simpleHomes.getInventoryManager().openInventory(player, new HomesInventory(simpleHomes, player, homes, 1));
            }
        }
    }

}