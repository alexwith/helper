package me.hyfe.helper.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import me.hyfe.helper.event.functional.protocol.ProtocolSubscriptionBuilder;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class Protocol {

    public static ProtocolSubscriptionBuilder subscribe(PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(packets);
    }

    public static ProtocolSubscriptionBuilder subscribe(ListenerPriority priority, PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(priority, packets);
    }

    public static ProtocolManager manager() {
        return ProtocolLibrary.getProtocolManager();
    }

    public static void sendPacket(Player player, PacketContainer packet) {
        try {
            manager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPackets(Player player, PacketContainer... packets) {
        for (PacketContainer packet : packets) {
            sendPacket(player, packet);
        }
    }

    public static void broadcastPacket(PacketContainer packet) {
        manager().broadcastServerPacket(packet);
    }

    public static void broadcastPacket(Iterable<Player> players, PacketContainer packet) {
        for (Player player : players) {
            sendPacket(player, packet);
        }
    }

}