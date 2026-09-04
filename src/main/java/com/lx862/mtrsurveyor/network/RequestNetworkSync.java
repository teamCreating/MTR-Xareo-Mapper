package com.lx862.mtrsurveyor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2S: client asks the server for a full-network map snapshot.
 * The server replies with a sequence of {@link NetworkSyncChunk}s.
 */
public class RequestNetworkSync {

    public RequestNetworkSync() {
    }

    public static void encode(RequestNetworkSync msg, FriendlyByteBuf buf) {
    }

    public static RequestNetworkSync decode(FriendlyByteBuf buf) {
        return new RequestNetworkSync();
    }

    public static void handle(RequestNetworkSync msg, Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayer sender = ctx.get().getSender();
        if (sender != null) {
            // Jump to the server thread first; the collector then hops onto each
            // simulator thread for thread-safe MTR data reads.
            ctx.get().enqueueWork(() -> ServerNetworkCollector.collectAndSend(sender));
        }
        ctx.get().setPacketHandled(true);
    }
}
