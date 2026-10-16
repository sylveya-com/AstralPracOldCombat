package dev.lokspel.astralpracoldcombat;

import dev.lokspel.api.AstralPracticeApi;
import dev.lokspel.astralpracoldcombat.config.OldCombatConfig;
import dev.lokspel.astralpracoldcombat.listener.MatchListener;
import java.io.File;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class AstralPracOldCombat extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!AstralPracticeApi.isAvailable()) {
            getComponentLogger().error("AstralPractice API not found! Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        RegisteredServiceProvider<OldCombatMechanicsAPI> registration =
                Bukkit.getServicesManager().getRegistration(OldCombatMechanicsAPI.class);
        if (registration == null) {
            getComponentLogger().error("OldCombatMechanics API not found! Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        OldCombatMechanicsAPI ocmApi = registration.getProvider();

        OldCombatConfig.load(getDataFolder().toPath(), getComponentLogger());

        File exampleConfig = new File(getDataFolder(), "example-ocm-config.yml");
        if (!exampleConfig.exists()) {
            saveResource("example-ocm-config.yml", false);
        }

        Bukkit.getPluginManager().registerEvents(
                new MatchListener(ocmApi, this), this);
        getComponentLogger().info("AstralPracOldCombat enabled.");
    }
}
