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

public class WorldInventory extends SimpleInventoryBuilder {

    private final SimpleHomes simpleHomes;

    public WorldInventory(SimpleHomes simpleHomes, Player player) {
        super(simpleHomes.getSettings(), "inventory.world", "gui", player);
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void openLastInventory() {
        simpleHomes.getInventoryManager().openInventory(getPlayer(), new SimpleHomesInventory(simpleHomes, getPlayer()));
    }

    @Override
    public String[][] getReplacements() {
        String enabled = getYaml().getString(getPath() + ".texts.enabled", "&a&lEnabled");
        String disabled = getYaml().getString(getPath() + ".texts.disabled", "&c&lDisabled");
        String setHomeStatus;
        if (getYaml().getBoolean("world.disableSetHome.enable")) {
            setHomeStatus = enabled;
        } else {
            setHomeStatus = disabled;
        }

        String teleportFromStatus;
        if (getYaml().getBoolean("world.disableTeleportFrom.enable")) {
            teleportFromStatus = enabled;
        } else {
            teleportFromStatus = disabled;
        }

        String teleportToStatus;
        if (getYaml().getBoolean("world.disableTeleportTo.enable")) {
            teleportToStatus = enabled;
        } else {
            teleportToStatus = disabled;
        }
        return new String[][] {
                {"%set-home-status%", setHomeStatus},
                {"%teleport-from-status%", teleportFromStatus},
                {"%teleport-to-status%", teleportToStatus}
        };
    }

    @Override
    public void prosesAction(String action, Player player) {
        switch (action) {
            case "[toggle-set-home]": {
                getYaml().set("world.disableSetHome.enable", !getYaml().getBoolean("world.disableSetHome.enable", false));
                getYaml().saveFileConfiguration();
                break;
            }
            case "[manage-set-home-worlds]": {
                simpleHomes.getInventoryManager().openInventory(player, new WorldSetHomeInventory(simpleHomes, 1));
                break;
            }
            case "[toggle-teleport-from]": {
                getYaml().set("world.disableTeleportFrom.enable", !getYaml().getBoolean("world.disableTeleportFrom.enable", false));
                getYaml().saveFileConfiguration();
                break;
            }
            case "[manage-teleport-from-worlds]": {
                simpleHomes.getInventoryManager().openInventory(player, new WorldTeleportFrom(simpleHomes, 1));
                break;
            }
            case "[toggle-teleport-to]": {
                getYaml().set("world.disableTeleportTo.enable", !getYaml().getBoolean("world.disableTeleportTo.enable", false));
                getYaml().saveFileConfiguration();
                break;
            }
            case "[manage-teleport-to-worlds]": {
                simpleHomes.getInventoryManager().openInventory(player, new WorldTeleportTo(simpleHomes, 1));
                break;
            }
        }
    }

}