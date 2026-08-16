# MTR JourneyMap Integration

A Minecraft Forge 1.20.1 mod that displays [Minecraft Transit Railway (MTR)](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) stations and depots as landmarks on [JourneyMap](https://modrinth.com/mod/journeymap).

This is the **JourneyMap edition** of MTR Surveyor, maintained on the `journeymap` branch. The Xaero edition (waypoint sync + world-map route lines) lives on the [`main`](https://github.com/teamCreating/MTR-Xareo-Mapper/tree/main) branch of the same repository.

## Features

- **Station landmarks** - every MTR station appears as a JourneyMap marker, labeled with the formatted station name and placed at the station center
- **Depot landmarks** - depots can be shown as markers too (off by default)
- **Colored, mode-aware icons** - each marker uses a per-transport-mode icon (train / boat / cable car / airplane), tinted with the station's own MTR color
- **Informative tooltips** - hovering a station marker shows its fare zone and the routes that serve it (hidden routes optional)
- **Automatic sync** - landmarks refresh automatically whenever MTR data syncs (client data hook, plus a server-side simulator hook)
- **Soft dependency** - integrates through the JourneyMap client API behind a reflection bridge, so the mod loads safely even when JourneyMap is not installed

## Requirements

| Mod | Required |
|-----|----------|
| Minecraft Forge 1.20.1 | ✅ |
| Minecraft Transit Railway 4.x | ✅ |
| JourneyMap (1.20.1) | ⚠️ Optional (enables landmarks) |

## Commands

| Command | Description |
|---------|-------------|
| `/mtrjourneymap syncLandmarks` | Force a landmark sync for all MTR simulator dimensions |
| `/mtrjourneymap syncLandmarks <dimension>` | Force a landmark sync for one dimension only |

Both commands are registered as server commands and require **permission level 4** (operator). Automatic syncing via the data hooks does not require any command.

## Configuration

The config file is located at `.minecraft/config/mtrjourneymap.json` (JSON, not TOML) and is generated on first launch.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | boolean | `true` | Master switch for landmark sync |
| `visibility.showStationLandmarks` | boolean | `true` | Show station landmarks |
| `visibility.showDepotLandmarks` | boolean | `false` | Show depot landmarks |
| `visibility.showEmptyStation` | boolean | `false` | Show stations with no routes |
| `visibility.showHiddenRoute` | boolean | `false` | Include routes marked as hidden in tooltips |
| `debugLog` | boolean | `false` | Log sync timing and details |
| `formalInitLog` | boolean | `false` | Use a formal startup log line |

## How it works

- Landmarks are created through the JourneyMap **client API** as `MarkerOverlay`s (map markers - not JourneyMap waypoints), with 16x16 marker textures from `assets/mtrjourneymap/textures/atlas/marker/`, anchored and tinted per station color.
- The JourneyMap plugin (`@ClientPlugin`) is reached through a reflection bridge (`MTRLandmarkManager`), so a missing JourneyMap never causes a `ClassNotFoundException`.
- Mixins:
  - `MTRSimulatorMixin` - after MTR server data syncs, schedule a landmark sync for that simulator's dimension
  - `MinecraftClientDataMixin` - on MTR client data sync, rebuild the summary from `simplifiedRouteIdMap` and sync landmarks for the current client level
  - `MTRAccessorMixin` / `MainAccessorMixin` - accessors for MTR's `Init.main` and `Main.simulators`

## Compared to the Xaero edition

| | JourneyMap edition (`journeymap` branch) | Xaero edition (`main` branch) |
|---|---|---|
| Station/depot markers | ✅ landmarks with colored icons & tooltips | ✅ waypoints |
| Platform mode (per-platform waypoints with route & destination) | - | ✅ |
| Route lines drawn on the map | - | ✅ (Xaero's World Map) |
| Client commands on any server | - | ✅ (`/mtrsurveyor ...`) |
| Manual sync command | `/mtrjourneymap syncLandmarks [dimension]` (op, level 4) | `/mtrsurveyor syncWaypoints` (client-side) |
| Config | `mtrjourneymap.json` (JSON) | `mtrsurveyor.toml` (TOML) |

Both editions are standalone client-side mods and can be installed together.

## Building

```bash
./gradlew build   # output: build/libs/mtrjourneymap-1.0.0.jar
```

Requires JDK 17+. JourneyMap is resolved via CurseMaven (`compileOnly`) and is bundled at runtime by the JourneyMap mod itself.

## License

This project is licensed under the MIT License.

## Author

**BenLi06** - based on [mtrsurveyor](https://github.com/AmberIsFrozen/mtrsurveyor) by AmberFrost
