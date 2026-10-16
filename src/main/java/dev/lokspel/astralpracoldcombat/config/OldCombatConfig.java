package dev.lokspel.astralpracoldcombat.config;

import dev.lokspel.astralpracoldcombat.config.settings.SettingsConfig;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.feature.comment.CommentFeature;
import me.whereareiam.strata.MigrationStream;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import me.whereareiam.strata.adapter.configura.ConfiguraTarget;
import me.whereareiam.strata.adapter.configura.StrataFeature;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

/**
 * Loads the configuration files of the plugin and holds what they contain.
 *
 * <p>Each file is bound to its model with Configura: a missing file is created from the defaults
 * the models declare, settings a user's file lacks are added, and what the user wrote is kept.
 * Before that, Strata brings files written by older versions up to date.
 */
public enum OldCombatConfig {
  ;

  private static final String VERSION_FILE = "config.yml";

  private static Documents documents;
  private static Map<String, String> normalizedLadderModes = Collections.emptyMap();
  private static Map<String, List<String>> normalizedModules = Collections.emptyMap();

  /**
   * Upgrades and loads configuration using Strata and Configura. If loading fails,
   * logs the error and falls back to default settings.
   *
   * @param dataDirectory the plugin's data folder
   * @param logger the component logger
   */
  public static void load(Path dataDirectory, ComponentLogger logger) {
    try {
      Configura configura = configura();
      migrate(configura, dataDirectory);

      documents = Documents.load(configura, dataDirectory);
    } catch (Exception e) {
      if (logger != null) {
          logger.error("Couldn't load configuration file: {}", describeFailure(e));
      }
      documents = new Documents(new SettingsConfig());
    }
    cacheMappings();
  }

  public static String describeFailure(Throwable failure) {
    if (failure == null) {
      return "Unknown error";
    }
    StringBuilder message = new StringBuilder(String.valueOf(failure.getMessage()));
    for (Throwable cause = failure.getCause(); cause != null; cause = cause.getCause()) {
      message.append(" <- ").append(cause.getMessage());
    }
    return message.toString();
  }

  public static SettingsConfig settings() {
    return documents != null ? documents.settings() : new SettingsConfig();
  }

  public static String getMode(String ladderName) {
    if (ladderName == null) {
      return null;
    }
    return normalizedLadderModes.get(ladderName.toLowerCase(Locale.ROOT));
  }

  public static List<String> getModeModules(String mode) {
    if (mode == null) {
      return Collections.emptyList();
    }
    return normalizedModules.getOrDefault(mode, Collections.emptyList());
  }

  private static void cacheMappings() {
    SettingsConfig config = settings();
    Map<String, String> mappings = new HashMap<>();
    if (config.getLadderModes() != null) {
      for (Map.Entry<String, String> entry : config.getLadderModes().entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null) {
          mappings.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }
      }
    }
    normalizedLadderModes = Collections.unmodifiableMap(mappings);

    Map<String, List<String>> modules = new HashMap<>();
    if (config.getModules() != null) {
      for (Map.Entry<String, List<String>> entry : config.getModules().entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null) {
          modules.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
      }
    }
    normalizedModules = Collections.unmodifiableMap(modules);
  }

  private static Configura configura() {
    return Configura.builder()
        .feature(new StrataFeature())
        .feature(CommentFeature.defaults())
        .build();
  }

  private static void migrate(Configura configura, Path dataDirectory) {
    MigrationStream<ConfigContext> stream =
        MigrationStream.<ConfigContext>builder()
            .id("astralpracoldcombat/config")
            .target(new ConfiguraTarget(configura, dataDirectory, VERSION_FILE))
            .baseline((files, latest) -> files.exists(VERSION_FILE) ? 0 : latest)
            .migration(1, "initial-setup", files -> {})
            .build();

    new Strata(List.of(stream)).migrate();
  }

  private record Documents(SettingsConfig settings) {
    static Documents load(Configura configura, Path directory) {
      return new Documents(
          configura.update(directory.resolve(VERSION_FILE), SettingsConfig.class));
    }
  }
}
