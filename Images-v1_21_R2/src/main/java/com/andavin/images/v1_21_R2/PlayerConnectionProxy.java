package com.andavin.images.v1_21_R2;

import com.andavin.images.PacketListener.ImageListener;
import com.andavin.reflect.FieldMatcher;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.andavin.reflect.Reflection.*;

/**
 * @since March 19, 2023
 * @author Andavin
 */
public class PlayerConnectionProxy extends ServerGamePacketListenerImpl {

    private static final Field AWAITING_TELEPORT = findField(ServerGamePacketListenerImpl.class, 3,
            new FieldMatcher(int.class).disallow(Modifier.STATIC));
    private static final Field AWAITING_POSITION_FROM_CLIENT = findField(ServerGamePacketListenerImpl.class,
            new FieldMatcher(Vec3.class));
    private final ImageListener listener;
    private final PacketListener packetListener;

    PlayerConnectionProxy(ServerGamePacketListenerImpl connection, Connection internal,
                          ImageListener listener, PacketListener packetListener,
                          CommonListenerCookie cookie) {
        super(((CraftServer) Bukkit.getServer()).getServer(), internal, connection.player, cookie);
        this.listener = listener;
        this.packetListener = packetListener;
        setFieldValue(AWAITING_TELEPORT, this, getFieldValue(AWAITING_TELEPORT, connection));
        setFieldValue(AWAITING_POSITION_FROM_CLIENT, this, getFieldValue(AWAITING_POSITION_FROM_CLIENT, connection));
    }

    @Override
    public void send(Packet<?> packet) {
        super.send(packet);
    }

    @Override
    public void handleInteract(ServerboundInteractPacket packet) {
        packetListener.handle(player.getBukkitEntity(), listener, packet);
        super.handleInteract(packet);
    }

    @Override
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        packetListener.handle(player.getBukkitEntity(), packet);
        super.handleSetCreativeModeSlot(packet);
    }
}