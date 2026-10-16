package dev.lokspel.astralpracoldcombat.listener;

import dev.lokspel.api.AstralPracticeApi;
import dev.lokspel.api.enums.PlayerState;
import dev.lokspel.api.event.PlayerStateChangeEvent;
import dev.lokspel.api.event.match.MatchEndEvent;
import dev.lokspel.api.event.match.MatchRoundStartEvent;
import dev.lokspel.api.event.match.MatchStartEvent;
import dev.lokspel.api.interfaces.Match;
import dev.lokspel.astralpracoldcombat.config.OldCombatConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import kernitus.plugin.OldCombatMechanics.api.PlayerModuleOverride;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MatchListener implements Listener {

    private final OldCombatMechanicsAPI ocmApi;
    private final JavaPlugin plugin;

    public MatchListener(OldCombatMechanicsAPI ocmApi, JavaPlugin plugin) {
        this.ocmApi = ocmApi;
        this.plugin = plugin;
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Bukkit.getScheduler().runTaskLater(plugin, () -> applyMode(match), 1L);
    }

    @EventHandler
    public void onMatchRoundStart(MatchRoundStartEvent event) {
        applyMode(event.getMatch());
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        event.getMatch().getPlayers().forEach(ocmApi::clearAllModuleOverridesForPlayer);
    }

    @EventHandler
    public void onPlayerStateChange(PlayerStateChangeEvent event) {
        if (event.getOldState() == PlayerState.MATCH && event.getNewState() != PlayerState.MATCH) {
            ocmApi.clearAllModuleOverridesForPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ocmApi.clearAllModuleOverridesForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/leave")) {
            return;
        }

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            AstralPracticeApi api = AstralPracticeApi.getInstance();
            if (api == null || api.getMatches().getMatch(player) == null) {
                ocmApi.clearAllModuleOverridesForPlayer(player);
            }
        }, 1L);
    }

    private void applyMode(Match match) {
        List<Player> players = match.getPlayers();
        if (players.isEmpty()) {
            return;
        }
        players.forEach(ocmApi::clearAllModuleOverridesForPlayer);

        String ladder = match.getLadderName();
        if (ladder == null) {
            return;
        }

        String mode = OldCombatConfig.getMode(ladder);
        if (mode == null) {
            return;
        }

        List<String> modules = OldCombatConfig.getModeModules(mode);
        if (modules.isEmpty()) {
            return;
        }
        players.forEach(player -> ocmApi.setModuleOverridesForPlayer(player, createOverrides(modules)));
    }

    private Map<String, PlayerModuleOverride> createOverrides(List<String> modules) {
        Map<String, PlayerModuleOverride> overrides = new HashMap<>(modules.size());
        for (String module : modules) {
            overrides.put(module, PlayerModuleOverride.FORCE_ENABLED);
        }
        return overrides;
    }
}
