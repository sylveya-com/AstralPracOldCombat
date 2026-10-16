# AstralPracOldCombat

1.8 PvP mode enforcement for [AstralPractice](https://modrinth.com/plugin/astralpractice) via [OldCombatMechanics](https://github.com/kernitus/BukkitOldCombatMechanics/releases)

## » About

This addon bridges AstralPractice and OldCombatMechanics. Maps arena ladders to OCM combat modes (`old` / `new`) and applies module overrides per-player at match start. No world restriction headaches, no manual `/ocm mode` commands — players get the right combat mode automatically.

## » Dependencies

- **[AstralPractice](https://modrinth.com/plugin/astralpractice)** — required
- **[OldCombatMechanics](https://github.com/kernitus/BukkitOldCombatMechanics/releases)** — required

## » Installation

1. Install AstralPractice and OldCombatMechanics
2. Drop `AstralPracOldCombat-1.0.0.jar` into your `plugins/` folder
3. Restart the server
4. Edit `plugins/AstralPracOldCombat/config.yml` to map ladders to modes

The plugin disables itself on startup if either the AstralPractice or OldCombatMechanics API is missing.

## » Building

```sh
./gradlew build
```

The compiled plugin jar will be generated in `build/libs/AstralPracOldCombat-1.0.0.jar`.

## » Configuration

### `config.yml`

```yaml
_version: 1

# Which ladders use which mode.
# Ladder names are matched case-insensitively (e.g. Nodebuff, nodebuff).
ladder-modes:
  BedWars: "old"
  SkyWars: "old"
  Bridges: "old"
  MLGRush: "old"
  TNTSumo: "old"
  BattleRush: "old"
  Boxing: "old"
  BuildUHC: "old"
  Debuff: "old"
  Nodebuff: "old"
  PearlFight: "old"
  Soup: "old"
  Sumo: "old"
  SG: "old"
  Fireball: "old"
  Spleef: "old"

# Modules to enable for each mode (must match OCM module names).
# Module overrides are applied to players when match begins.
modules:
  old:
    - "attack-frequency"
    - "old-armour-durability"
    - "old-fishing-knockback"
    - "fishing-rod-velocity"
    - "projectile-knockback"
    - "disable-crafting"
    - "old-brewing-stand"
    - "disable-enderpearl-cooldown"
    - "disable-attack-cooldown"
    - "disable-sword-sweep"
    - "old-tool-damage"
    - "sword-blocking"
    - "shield-damage-reduction"
    - "old-golden-apples"
    - "old-player-knockback"
    - "old-player-regen"
    - "old-armour-strength"
    - "old-potion-effects"
    - "old-critical-hits"
    - "disable-attack-sounds"
  new: []
```

Ladder names are matched case-insensitively, so `nodebuff`, `Nodebuff` and `NODEBUFF` are equivalent. The mode key is **not** case-insensitive — `old` and `new` must match the keys under `modules`.

An `example-ocm-config.yml` is generated next to `config.yml` with recommended OldCombatMechanics settings for the modules listed above.

## » How it works

- Listens to `MatchStartEvent` and `MatchRoundStartEvent` from AstralPractice
- Resolves the ladder name from the match at runtime
- Looks up the configured mode (`old` / `new`) for that ladder
- Applies `setModuleOverridesForPlayer()` with `FORCE_ENABLED` on every module listed under that mode — bypasses OCM world-based modeset restrictions
- Clears all overrides on `MatchEndEvent`
- Listens to `PlayerStateChangeEvent` to clear overrides whenever a player leaves `MATCH` state
- Clears overrides on `/leave` and `PlayerQuitEvent`

## » Compatibility

- Compatible with Minecraft versions supported by both **AstralPractice** and **OldCombatMechanics**.

Enjoy AstralPracOldCombat!
