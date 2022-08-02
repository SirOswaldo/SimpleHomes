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
import org.kayteam.simplehomes.inputs.VaultDeleteHomeInput;
import org.kayteam.simplehomes.inputs.VaultOverwriteHomeInput;
import org.kayteam.simplehomes.inputs.VaultSetHomeInput;
import org.kayteam.simplehomes.inputs.VaultTeleportInput;

public class VaultInventory extends SimpleInventoryBuilder {

    private final SimpleHomes simpleHomes;

    public VaultInventory(SimpleHomes simpleHomes, Player player) {
        super(simpleHomes.getSettings(), "inventory.vault", "gui", player);
        this.simpleHomes = simpleHomes;
    }

    @Override
    public void openLastInventory() {
        simpleHomes.getInventoryManager().openInventory(getPlayer(), new SimpleHomesInventory(simpleHomes, getPlayer()));
    }

    @Override
    public String[][] getReplacements() {
        String vaultStatus;
        if (simpleHomes.getSettings().getBoolean("vault.enable")) {
            vaultStatus = getYaml().getString(getPath() + ".texts.enabled");
        } else {
            vaultStatus = getYaml().getString(getPath() + ".texts.disabled");
        }
        return new String[][] {
                {"%vault-status%", vaultStatus},
                {"%set-home-cost%", Math.round(simpleHomes.getSettings().getDouble("vault.setHome", 0.0)) + ""},
                {"%overwrite-home-cost%", Math.round(simpleHomes.getSettings().getDouble("vault.overwriteHome", 0.0)) + ""},
                {"%delete-home-cost%", Math.round(simpleHomes.getSettings().getDouble("vault.deleteHome", 0.0)) + ""},
                {"%teleport-home-cost%", Math.round(simpleHomes.getSettings().getDouble("vault.teleport", 0.0)) + ""}
        };
    }

    @Override
    public void prosesAction(String action, Player player) {
        switch (action) {
            case "[close]": {
                player.closeInventory();
                break;
            }
            case "[return]": {
                simpleHomes.getInventoryManager().openInventory(player, new SimpleHomesInventory(simpleHomes, player));
                break;
            }
            case "[toggle-vault-status]": {
                simpleHomes.getSettings().set("vault.enable", !simpleHomes.getSettings().getBoolean("vault.enable"));
                simpleHomes.getSettings().saveFileConfiguration();
                break;
            }
            case "[change-set-home-cost]": {
                player.closeInventory();
                simpleHomes.getInputManager().addInput(player, new VaultSetHomeInput(simpleHomes));
                simpleHomes.getMessages().sendMessage(player, "admin.vault.setHome.enterAmount");
                break;
            }
            case "[change-overwrite-home-cost]": {
                player.closeInventory();
                simpleHomes.getInputManager().addInput(player, new VaultOverwriteHomeInput(simpleHomes));
                simpleHomes.getMessages().sendMessage(player, "admin.vault.overwriteHome.enterAmount");
                break;
            }
            case "[change-delete-home-cost]": {
                player.closeInventory();
                simpleHomes.getInputManager().addInput(player, new VaultDeleteHomeInput(simpleHomes));
                simpleHomes.getMessages().sendMessage(player, "admin.vault.deleteHome.enterAmount");
                break;
            }
            case "[change-teleport-home-cost]": {
                player.closeInventory();
                simpleHomes.getInputManager().addInput(player, new VaultTeleportInput(simpleHomes));
                simpleHomes.getMessages().sendMessage(player, "admin.vault.teleport.enterAmount");
                break;
            }
        }
    }

}