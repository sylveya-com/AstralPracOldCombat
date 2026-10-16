package dev.lokspel.astralpracoldcombat.config.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.whereareiam.configura.feature.comment.api.annotation.Comment;

@Getter
public class SettingsConfig {

  @Comment({
    "Which ladders use which mode.",
    "Ladder names are matched case-insensitively (e.g. Nodebuff, nodebuff)."
  })
  @JsonProperty("ladder-modes")
  private Map<String, String> ladderModes = defaultLadderModes();

  @Comment({
    "Modules to enable for each mode (must match OCM module names).",
    "Module overrides are applied to players when match begins."
  })
  @JsonProperty("modules")
  private Map<String, List<String>> modules = defaultModules();

  private static Map<String, String> defaultLadderModes() {
    Map<String, String> modes = new LinkedHashMap<>();
    modes.put("BedWars", "old");
    modes.put("SkyWars", "old");
    modes.put("Bridges", "old");
    modes.put("MLGRush", "old");
    modes.put("TNTSumo", "old");
    modes.put("BattleRush", "old");
    modes.put("Boxing", "old");
    modes.put("BuildUHC", "old");
    modes.put("Debuff", "old");
    modes.put("Nodebuff", "old");
    modes.put("PearlFight", "old");
    modes.put("Soup", "old");
    modes.put("Sumo", "old");
    modes.put("SG", "old");
    modes.put("Fireball", "old");
    modes.put("Spleef", "old");
    return modes;
  }

  private static Map<String, List<String>> defaultModules() {
    Map<String, List<String>> modules = new LinkedHashMap<>();
    modules.put(
        "old",
        List.of(
            "attack-frequency",
            "old-armour-durability",
            "old-fishing-knockback",
            "fishing-rod-velocity",
            "projectile-knockback",
            "disable-crafting",
            "old-brewing-stand",
            "disable-enderpearl-cooldown",
            "disable-attack-cooldown",
            "disable-sword-sweep",
            "old-tool-damage",
            "sword-blocking",
            "shield-damage-reduction",
            "old-golden-apples",
            "old-player-knockback",
            "old-player-regen",
            "old-armour-strength",
            "old-potion-effects",
            "old-critical-hits",
            "disable-attack-sounds"));
    modules.put("new", List.of());
    return modules;
  }
}
