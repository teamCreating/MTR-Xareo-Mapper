# MTR JourneyMap Integration

A Minecraft Forge 1.20.1 mod that displays [Minecraft Transit Railway (MTR)](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) stations, platforms and depots as markers on [JourneyMap](https://modrinth.com/mod/journeymap).

This is the **JourneyMap edition** of MTR Surveyor, maintained on the `journeymap` branch. The Xaero edition (waypoint sync) lives on the [`main`](https://github.com/teamCreating/MTR-Xareo-Mapper/tree/main) branch of the same repository. Both editions now share the same feature set.

## Features

- **Station mode** - one marker per MTR station, labelled with the formatted station name and placed at the station center
- **Platform mode** - one marker per platform, labelled with the platform number; hovering shows the station name plus every route serving that platform with its destination (`RouteName → Destination`)
- **Depot markers** - depots can be shown as markers too (off by default)
- **Colored, mode-aware icons** - each marker uses a per-transport-mode icon (train / boat / cable car / airplane), tinted with the station's own MTR color
- **Informative tooltips** - station markers show their fare zone and the routes that serve them (hidden routes optional)
- **Automatic sync with retry** - landmarks refresh automatically whenever MTR data syncs; sync requests are queued on the client tick loop and retried until the JourneyMap API is ready, and the player's dimension is tracked so markers are rebuilt on dimension change
- **Client-side commands** - work on remote servers that do not have this mod installed
- **Soft dependency** - integrates through the JourneyMap client API behind a reflection bridge, so the mod loads safely even when JourneyMap is not installed

## Requirements

| Mod | Required |
|-----|----------|
| Minecraft Forge 1.20.1 | ✅ |
| Minecraft Transit Railway 4.x | ✅ |
| JourneyMap (1.20.1) | ⚠️ Optional (enables markers) |

## Commands

All commands are **client-side** and work on any server:

| Command | Description |
|---------|-------------|
| `/mtrjourneymap syncLandmarks` | Force a landmark sync |
| `/mtrjourneymap mode` | Show the current display mode |
| `/mtrjourneymap mode station` | Switch to station mode (one marker per station) |
| `/mtrjourneymap mode platform` | Switch to platform mode (one marker per platform) |
| `/mtrjourneymap config enabled <true/false>` | Enable/disable landmark sync |
| `/mtrjourneymap config showStations <true/false>` | Show/hide station markers |
| `/mtrjourneymap config showDepots <true/false>` | Show/hide depot markers |
| `/mtrjourneymap config showEmptyStation <true/false>` | Show/hide stations with no routes |

Every config change is saved and triggers an immediate landmark re-sync.

## Configuration

The config file is located at `.minecraft/config/mtrjourneymap.json` (JSON, not TOML) and is generated on first launch.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | boolean | `true` | Master switch for landmark sync |
| `waypointMode` | string | `"station"` | Display mode: `"station"` or `"platform"` |
| `visibility.showStationLandmarks` | boolean | `true` | Show station/platform markers |
| `visibility.showDepotLandmarks` | boolean | `false` | Show depot markers |
| `visibility.showEmptyStation` | boolean | `false` | Show stations with no routes (station mode) |
| `visibility.showHiddenRoute` | boolean | `false` | Include routes marked as hidden in tooltips |
| `debugLog` | boolean | `false` | Log sync timing and details |
| `formalInitLog` | boolean | `false` | Use a formal startup log line |

## Display Modes

### Station Mode (default)
- One marker per MTR station, placed at the station center
- Tooltip shows fare zone and the routes serving the station

### Platform Mode
- One marker per platform, placed at the platform's exact position
- Marker label shows the platform name/number
- Tooltip shows the station name plus `RouteName → Destination` for every route serving that platform

## How it works

- Markers are created through the JourneyMap **client API** as `MarkerOverlay`s (map markers - not JourneyMap waypoints), with 16x16 marker textures from `assets/mtrjourneymap/textures/atlas/marker/`, anchored and tinted per station color.
- Because JourneyMap rejects re-showing a marker ID, each sync removes the previous marker set and re-adds fresh markers.
- The JourneyMap plugin (`@ClientPlugin`) is reached through a reflection bridge (`MTRLandmarkManager`), so a missing JourneyMap never causes a `ClassNotFoundException`.
- Sync requests (MTR data change, config change, dimension change, manual command) are queued in `ClientSyncHandler` and executed on the client tick loop with retry - the same model the Xaero edition uses.
- Mixins:
  - `MinecraftClientDataMixin` - on MTR client data sync, request a landmark sync
  - `MTRSimulatorMixin` - kept as a no-op: markers are client-side, so the server never touches the JourneyMap API
  - `MTRAccessorMixin` / `MainAccessorMixin` - accessors for MTR's `Init.main` and `Main.simulators`

## Compared to the Xaero edition

| | JourneyMap edition (`journeymap` branch) | Xaero edition (`main` branch) |
|---|---|---|
| Station mode + depot markers | ✅ markers with colored icons & tooltips | ✅ waypoints |
| Platform mode (per-platform markers with route & destination) | ✅ | ✅ |
| Client commands on any server | ✅ (`/mtrjourneymap ...`) | ✅ (`/mtrsurveyor ...`) |
| Route lines drawn on the map | - | - (removed in both editions) |
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
