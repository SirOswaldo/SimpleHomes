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

public class SimpleHomesInventory extends SimpleInventoryBuilder {

    private final SimpleHomes simpleHomes;

    public SimpleHomesInventory(SimpleHomes simpleHomes, Player player) {
        super(simpleHomes.getSettings(), "inventory.admin", "cmd", player);
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void openLastInventory() { }

    @Override
    public String[][] getReplacements() {
        return new String[0][];
    }

    @Override
    public void prosesAction(String action, Player player) {
        switch (action) {
            case "[close]": {
                player.closeInventory();
                break;
            }
            case "[world-settings]": {
                simpleHomes.getInventoryManager().openInventory(player, new WorldInventory(simpleHomes, player));
                break;
            }
            case "[vault-settings]": {
                simpleHomes.getInventoryManager().openInventory(player, new VaultInventory(simpleHomes, player));
                break;
            }
            case "[reload]": {
                player.closeInventory();
                simpleHomes.onReload();
                simpleHomes.getMessages().sendMessage(player, "admin.reloaded", new String[][] {{"%plugin%", simpleHomes.getDescription().getName()}});
                break;
            }
        }
    }
}