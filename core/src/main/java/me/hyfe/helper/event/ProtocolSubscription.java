package me.hyfe.helper.event;

import com.comphenix.protocol.PacketType;

import java.util.Set;

public interface ProtocolSubscription extends Subscription {

    Set<PacketType> getPackets();
}