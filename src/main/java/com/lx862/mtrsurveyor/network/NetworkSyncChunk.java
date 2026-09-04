package com.lx862.mtrsurveyor.network;

import com.lx862.mtrsurveyor.mapdata.MapDataCache;
import com.lx862.mtrsurveyor.mapdata.MapRoute;
import com.lx862.mtrsurveyor.mapdata.MapTrack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * S2C: one chunk of a full-network map snapshot. The payload is a byte slice
 * of a self-describing binary dump; the last chunk triggers reassembly.
 */
public class NetworkSyncChunk {

    public int transferId;
    public short chunkIndex;
    public short totalChunks;
    public byte[] data;

    public NetworkSyncChunk() {
    }

    public NetworkSyncChunk(int transferId, short chunkIndex, short totalChunks, byte[] data) {
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.data = data;
    }

    public static void encode(NetworkSyncChunk msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.transferId);
        buf.writeShort(msg.chunkIndex);
        buf.writeShort(msg.totalChunks);
        buf.writeByteArray(msg.data);
    }

    public static NetworkSyncChunk decode(FriendlyByteBuf buf) {
        final NetworkSyncChunk msg = new NetworkSyncChunk();
        msg.transferId = buf.readVarInt();
        msg.chunkIndex = buf.readShort();
        msg.totalChunks = buf.readShort();
        msg.data = buf.readByteArray();
        return msg;
    }

    public static void handle(NetworkSyncChunk msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientNetworkSync.onChunkReceived(msg));
        ctx.get().setPacketHandled(true);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Payload codec (shared shape between writer and reader)
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Payload layout:
     * <pre>
     * varint dimensionCount
     * per dimension:
     *   UTF dimensionId
     *   varint routeCount
     *   per route: UTF name, int color, byte circular, varint stopCount,
     *              per stop: float x, float z, UTF stationName, UTF destination
     *   varint trackCount
     *   per track: varint pointCount, per point: float x, float z
     * </pre>
     */
    public static void writeDimensionList(DataOutputStream out,
            List<PendingDimension> dimensions) throws IOException {
        out.writeInt(dimensions.size());
        for (PendingDimension dimension : dimensions) {
            out.writeUTF(dimension.dimensionId);
            out.writeInt(dimension.routes.size());
            for (MapRoute route : dimension.routes) {
                out.writeUTF(route.name == null ? "" : route.name);
                out.writeInt(route.color);
                out.writeBoolean(route.circular);
                out.writeInt(route.stops.size());
                for (MapRoute.Stop stop : route.stops) {
                    out.writeFloat((float) stop.x);
                    out.writeFloat((float) stop.z);
                    out.writeUTF(stop.stationName == null ? "" : stop.stationName);
                    out.writeUTF(stop.destination == null ? "" : stop.destination);
                }
            }
            out.writeInt(dimension.tracks.size());
            for (MapTrack track : dimension.tracks) {
                out.writeInt(track.points.size());
                for (double[] point : track.points) {
                    out.writeFloat((float) point[0]);
                    out.writeFloat((float) point[1]);
                }
            }
        }
    }

    public static List<MapDataCache.DimensionData> readDimensionList(DataInputStream in) throws IOException {
        final int dimensionCount = in.readInt();
        final List<MapDataCache.DimensionData> result = new ArrayList<>(dimensionCount);
        for (int d = 0; d < dimensionCount; d++) {
            final String dimensionId = in.readUTF();

            final int routeCount = in.readInt();
            final List<MapRoute> routes = new ArrayList<>(routeCount);
            for (int r = 0; r < routeCount; r++) {
                final String name = in.readUTF();
                final int color = in.readInt();
                final boolean circular = in.readBoolean();
                final int stopCount = in.readInt();
                final List<MapRoute.Stop> stops = new ArrayList<>(stopCount);
                for (int s = 0; s < stopCount; s++) {
                    final float x = in.readFloat();
                    final float z = in.readFloat();
                    final String stationName = in.readUTF();
                    final String destination = in.readUTF();
                    stops.add(new MapRoute.Stop(x, z, stationName, destination));
                }
                routes.add(new MapRoute(name, color, circular, stops));
            }

            final int trackCount = in.readInt();
            final List<MapTrack> tracks = new ArrayList<>(trackCount);
            for (int t = 0; t < trackCount; t++) {
                final int pointCount = in.readInt();
                final List<double[]> points = new ArrayList<>(pointCount);
                for (int p = 0; p < pointCount; p++) {
                    points.add(new double[]{in.readFloat(), in.readFloat()});
                }
                tracks.add(new MapTrack(points));
            }

            result.add(new MapDataCache.DimensionData(dimensionId, routes, tracks, System.currentTimeMillis()));
        }
        return result;
    }

    /** One dimension's worth of data pending serialization. */
    public static class PendingDimension {

        public final String dimensionId;
        public final List<MapRoute> routes = new ArrayList<>();
        public final List<MapTrack> tracks = new ArrayList<>();

        public PendingDimension(String dimensionId) {
            this.dimensionId = dimensionId;
        }
    }

    static DataInputStream wrapForRead(byte[] payload) {
        return new DataInputStream(new ByteArrayInputStream(payload));
    }
}
