package com.lx862.mtrsurveyor.network;

import com.lx862.mtrsurveyor.MTRSurveyor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * C2S: client asks the server for a full-network map snapshot.
 * The server replies with a sequence of {@link NetworkSyncChunk}s.
 */
public record RequestNetworkSync() implements CustomPacketPayload {

    public static final RequestNetworkSync INSTANCE = new RequestNetworkSync();

    public static final Type<RequestNetworkSync> TYPE =
            new Type<>(MTRNetwork.id("request_network_sync"));

    public static final StreamCodec<FriendlyByteBuf, RequestNetworkSync> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestNetworkSync msg, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer sender) {
            // Jump to the server thread first; the collector then hops onto each
            // simulator thread for thread-safe MTR data reads.
            ctx.enqueueWork(() -> ServerNetworkCollector.collectAndSend(sender));
        } else {
            MTRSurveyor.LOGGER.warn("[MTRSurveyor] Received network sync request from a non-player");
        }
    }
}
