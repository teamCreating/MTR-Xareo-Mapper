# [CRTools] MTR:Xaero Mapper

A Minecraft Forge 1.20.1 mod that automatically syncs [Minecraft Transit Railway (MTR)](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) stations and depots as waypoints to [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) / [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap).

## Features

- **Automatic Waypoint Sync** — MTR stations and depots appear as Xaero waypoints automatically
- **Station Mode** — One waypoint per station, showing full station name
- **Platform Mode** — One waypoint per platform, showing platform number with route and destination info on hover
- **Correct Altitude** — Waypoints are placed at actual platform level, not underground
- **Client-Side Commands** — Works on remote servers without server-side installation

## Requirements

| Mod | Required |
|-----|----------|
| Minecraft Forge 1.20.1 | ✅ |
| Minecraft Transit Railway 4.x | ✅ |
| Xaero's Minimap | ⚠️ Optional (enables waypoint sync) |
| Xaero's World Map | ⚠️ Optional (recommended for best experience) |

## Commands

All commands are client-side and work on any server:

| Command | Description |
|---------|-------------|
| `/mtrsurveyor syncWaypoints` | Force a waypoint sync |
| `/mtrsurveyor mode` | Show current display mode |
| `/mtrsurveyor mode station` | Switch to station mode (one waypoint per station) |
| `/mtrsurveyor mode platform` | Switch to platform mode (one waypoint per platform) |
| `/mtrsurveyor config enabled <true/false>` | Enable/disable auto-sync |
| `/mtrsurveyor config showStations <true/false>` | Show/hide station waypoints |
| `/mtrsurveyor config showDepots <true/false>` | Show/hide depot waypoints |

## Configuration

The config file is located at `.minecraft/config/mtrsurveyor.toml`.

Key options:
- `enabled` — Master switch for waypoint sync (default: `true`)
- `waypointMode` — Display mode: `"station"` or `"platform"` (default: `"station"`)
- `showStationLandmarks` — Show station waypoints (default: `true`)
- `showDepotLandmarks` — Show depot waypoints (default: `false`)
- `showEmptyStation` — Show stations with no routes (default: `false`)
- `debugLog` — Enable detailed sync logging (default: `false`)

## Display Modes

### Station Mode (default)
- One waypoint per MTR station
- Icon shows full station name
- Waypoint placed at average platform height

### Platform Mode
- One waypoint per platform in each station
- Icon shows platform name/number
- Hover text shows: `[MTR] StationName | RouteName→Destination`
- Waypoint placed at exact platform position

## License

This project is licensed under the MIT License.

## Author

**BenLi06** — Based on [mtrsurveyor](https://github.com/AmberIsFrozen/mtrsurveyor) by AmberFrost