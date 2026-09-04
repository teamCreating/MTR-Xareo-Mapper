# v1.2.0 Release Notes (2026-09-05)

## 平台迁移: Forge 1.20.1 → NeoForge 1.21.1

- 目标平台变更为 **NeoForge 1.21.1**(NeoForge 21.1.249),依赖 **MTR 4.1.0-beta.2**(NEOFORGE-4.1.0-beta.2+1.21.1)、Xaero's World Map 1.45.0+、Xaero's Minimap 26.4.2+。
- 构建系统由 ForgeGradle 6 迁移为 **ModDevGradle 2.0.146**;NeoForge 运行时即 Mojang 官方映射,mixin 不再需要 refmap/SRG 名。
- MTR 4.1 的包名迁移已适配:`org.mtr.mod.client.MinecraftClientData` → `org.mtr.client.MinecraftClientData`、`org.mtr.mod.Init` → `org.mtr.MTR`;mtr-core 数据层(Station/Platform/SimplifiedRoute/Rail/RailMath/Simulator)保持兼容。
- 网络层从 Forge `SimpleChannel` 重写为 NeoForge `CustomPacketPayload` + `PayloadRegistrar`,两侧均以 `optional()` 注册,并对非 NeoForge 连接做了类型探测,在不支持的服务器上自动回退纯客户端模式。
- 1.21.1 渲染 API 适配:`VertexConsumer.addVertex()/setColor()`、`ResourceLocation.fromNamespaceAndPath`、pack_format 34。
- 新增 `/mtrsurveyor syncRoutes` 命令与 `networkSync.enabled` / `networkSync.refreshIntervalSeconds` 配置(全网快照同步)。

## 实机自测记录 (runClient + MTR/Xaero 实装环境)

- mtrsurveyor 1.2.0 加载成功;Xaero 双图检测、`XaeroWorldMapMixin`/`XaeroWorldMapAccessor` 成功应用到 `GuiMap`。
- 打开世界地图后渲染钩子日志 `Path layer render hook into Xaero's World Map is active` 出现,ROUTES/TRACKS 开关在地图左上角正常渲染。
- 单机进入世界后全网同步全链路自动完成:`Requested → Sent full-network snapshot for minecraft/overworld|the_nether|the_end → Full-network snapshot applied`,分块传输与客户端缓存工作正常。
- 会话期间无 mod 相关报错。

---

# v1.0.1 Release Notes(2026-09-05)

## 下载说明

本 Release 附带两个独立构建产物(均为客户端 mod,可单独安装,也可同时安装):

| 文件 | 版本 | 说明 |
|------|------|------|
| `CRTools-MTR-Xaero-Mapper-1.0.1.jar` | Xaero 版(main 分支) | 将 MTR 车站 / 站台 / 车辆段同步为 Xaero's Minimap 航点 |
| `mtrjourneymap-1.0.1.jar` | JourneyMap 版(journeymap 分支) | 将 MTR 车站 / 站台 / 车辆段显示为 JourneyMap marker |

## 本次更新内容

### 移除:Xaero 世界地图路线视图(main 分支)

- 按计划移除了将 MTR 路线网络直接呈现至 Xaero's World Map 的功能(`XaeroRouteRenderer` 及配套的 `XaeroWorldMapMixin` / `XaeroWorldMapAccessor` / `XaeroMixinPlugin` 已一并删除)。
- Xaero 版本自此不再依赖 Xaero's World Map——航点同步仅需要 Xaero's Minimap。
- 两个版本的功能特性自此完全对齐,路线视图在两个版本中均不再提供。

### 统一:JourneyMap 版本(journeymap 分支)功能对齐 Xaero 版本

- **站台(Platform)模式**:新增 `waypointMode` 配置(`station` / `platform`)。
  - platform 模式下,每个站台一个 marker,标签为站台编号;
  - 悬停显示车站名,以及每条经过该站台路线的「路线名 → 终点站」信息(支持自定义终点站、按路线名去重);
  - marker 坐标取站台精确位置。
- **Depot 显示**:车辆段 marker 在两种模式下均可通过 `showDepotLandmarks` 开关显示,并按交通方式(火车 / 船 / 缆车 / 飞机)使用对应图标,按站点颜色染色。
- **配置调整命令**(客户端命令,任何服务器可用,与 Xaero 版命令结构一致):
  - `/mtrjourneymap syncLandmarks` — 手动触发同步;
  - `/mtrjourneymap mode [station|platform]` — 查看 / 切换显示模式;
  - `/mtrjourneymap config enabled|showStations|showDepots|showEmptyStation <true/false>` — 调整配置;
  - 每次修改自动保存并立即触发重新同步。
- **同步机制对齐**:新增 `ClientSyncHandler`,同步请求进入客户端 tick 队列并自动重试(JourneyMap API 未就绪、MTR 数据未同步时不会丢失请求);玩家切换维度时自动重建 marker。
- **服务端命令移除**:原需权限等级 4 的服务端 `/mtrjourneymap syncLandmarks [dimension]` 已由上述客户端命令取代;`MTRSimulatorMixin` 改为空操作(marker 属客户端内容,专用服务器上不存在 JourneyMap API,原写法在服务器端有崩溃风险)。

### 保留:Xaero 版本既有特性(纯客户端运行)

- 全面解耦服务端数据依赖:调取本地 Dashboard 与活动 Client 实例节点直接映射渲染,可顺畅连接至未安装本 Mod 的外部或第三方多人服务器。

## 修复与底层优化

- **JourneyMap marker 重复 ID 修复**:JourneyMap API 拒绝对已显示的 ID 再次调用 `api.show()`,原逻辑每次同步会持续报错且无法更新。现改为每次同步先移除旧 marker 集再整体重建。
- **marker ID 生成修复**:Station / Depot / Platform 的共同基类是 `NameColorDataBase` 而非 `AreaBase`(Platform 属于 SavedRailBase 体系),原实现对站台生成 marker 时会失败。
- **构建环境修复**:两个分支的 `gradle.properties` 改为指向本机 JDK 21;journeymap 分支 gradle wrapper 由 8.12 调整为 8.8。

## 部署及兼容说明

- **目标环境架构**: Forge 1.20.1
- **依赖运行基准版本**:
  - `MTR v4.0.2-hotfix-1` 或以上版本
  - Xaero 版本(main 分支):`Xaero's Minimap v25.3.10` 或以上版本(不再需要 Xaero's World Map)
  - JourneyMap 版本(journeymap 分支):`JourneyMap 1.20.1`(可选,提供 marker 显示)
