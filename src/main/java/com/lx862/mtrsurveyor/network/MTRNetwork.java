package com.lx862.mtrsurveyor.network;

import com.lx862.mtrsurveyor.MTRSurveyor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * Forge network channel for the full-network map sync (Plan C).
 *
 * <p>When this mod is installed on the server, clients can request a snapshot
 * of the whole MTR network (routes + sampled track geometry) per dimension -
 * the same data model Create's train map uses. The snapshot is transferred in
 * chunks so arbitrarily large networks stay within packet size limits.</p>
 */
public class MTRNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static int messageId = 0;

    public static SimpleChannel CHANNEL;

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MTRSurveyor.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);

        CHANNEL.registerMessage(messageId++, RequestNetworkSync.class,
                RequestNetworkSync::encode, RequestNetworkSync::decode, RequestNetworkSync::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(messageId++, NetworkSyncChunk.class,
                NetworkSyncChunk::encode, NetworkSyncChunk::decode, NetworkSyncChunk::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
