package club.spfmc.simplehomes;

import club.spfmc.simplehomes.commands.*;
import club.spfmc.simplehomes.home.HomesManager;
import club.spfmc.simplehomes.inventories.DeleteHomeConfirmInventory;
import club.spfmc.simplehomes.inventories.HomesInventory;
import club.spfmc.simplehomes.listeners.PlayerJoinListener;
import club.spfmc.simplehomes.listeners.PlayerMoveListener;
import club.spfmc.simplehomes.listeners.PlayerQuitListener;
import club.spfmc.simplehomes.util.bStats.Metrics;
import club.spfmc.simplehomes.util.kayteam.KayTeam;
import club.spfmc.simplehomes.util.updatechecker.UpdateChecker;
import club.spfmc.simplehomes.util.yaml.Yaml;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;

public class SimpleHomes extends JavaPlugin {

    private final Yaml settings = new Yaml(this, "settings");
    public Yaml getSettings() {
        return settings;
    }
    private final Yaml messages = new Yaml(this, "messages");
    public Yaml getMessages() {
        return messages;
    }

    private final HomesManager homesManager = new HomesManager(this);
    public HomesManager getHomesManager() {
        return homesManager;
    }

    private HomesInventory homesInventory;
    public HomesInventory getHomesInventory() {
        return homesInventory;
    }
    private DeleteHomeConfirmInventory deleteHomeConfirmInventory;
    public DeleteHomeConfirmInventory getDeleteHomeConfirmInventory() {
        return deleteHomeConfirmInventory;
    }

    // Update Checker
    private UpdateChecker updateChecker;
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    @Override
    public void onEnable() {
        // bStats
        int pluginId = 12209;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("homes", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int homes = 0;
                for (FileConfiguration fileConfiguration:Yaml.getFolderFiles(getDataFolder() + "/players")) {
                    homes = homes + fileConfiguration.getKeys(false).size();
                }
                return homes;
            }
        }));
        // Yaml Files
        settings.registerFileConfiguration();
        messages.registerFileConfiguration();
        //Commands
        new SetHomeCommand(this);
        new DeleteHomeCommand(this);
        new HomeCommand(this);
        new HomesCommand(this);
        new SimpleHomesCommand(this);
        // Listeners
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerMoveListener(this);
        homesInventory = new HomesInventory(this);
        getServer().getPluginManager().registerEvents(homesInventory, this);
        deleteHomeConfirmInventory = new DeleteHomeConfirmInventory(this);
        getServer().getPluginManager().registerEvents(deleteHomeConfirmInventory, this);
        // Load Player Homes
        for (Player player:getServer().getOnlinePlayers()) {
            homesManager.loadHomes(player.getName());
        }
        // MaxHomes
        homesManager.loadMaxHomes();
        // Enabled
        KayTeam.sendBrandMessage(this, "&aEnabled");
        // UpdateChecker
        updateChecker = new UpdateChecker(this, 94734);
        if (updateChecker.getUpdateCheckResult().equals(UpdateChecker.UpdateCheckResult.OUT_DATED)) {
            updateChecker.sendOutDatedMessage(getServer().getConsoleSender());
        }
    }

    @Override
    public void onDisable() {
        // Unload Player Homes
        for (Player player:getServer().getOnlinePlayers()) {
            player.closeInventory();
            homesManager.unloadHomes(player.getName());
        }
        KayTeam.sendBrandMessage(this, "&cDisabled");
    }

}