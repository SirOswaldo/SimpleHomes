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
import org.kayteam.simplehomes.SimpleHomes;
import org.kayteam.simplehomes.home.Home;
import org.kayteam.simplehomes.home.Homes;

public class EditHomeInventory extends SimpleInventoryBuilder {

    private final SimpleHomes simpleHomes;

    public EditHomeInventory(SimpleHomes simpleHomes, Home home, String from, Player player) {
        super(simpleHomes.getSettings(), "inventory.homeEditor", from, player);
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void openLastInventory() {

    }

    @Override
    public String[][] getReplacements() {
        return new String[0][];
    }

    @Override
    public void prosesAction(String action, Player player) {
        if (action.startsWith("[close]")) {
            player.closeInventory();
        } else if (action.startsWith("[return]")) {
            if (getFrom().equals("gui")) {
                Homes homes = simpleHomes.getHomesManager().getHomes(player.getName());
                simpleHomes.getInventoryManager().openInventory(player, new HomesInventory(simpleHomes, player, homes, 1));
            } else {
                player.closeInventory();
            }
        }
    }

}