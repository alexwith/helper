package me.hyfe.helper.sign;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.hyfe.helper.Schedulers;
import me.hyfe.helper.protocol.Protocol;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SignPrompt {
    private final Player player;
    private final List<String> lines;
    private final ResponseHandler responseHandler;

    private final static ProtocolManager PROTOCOL = ProtocolLibrary.getProtocolManager();

    public SignPrompt(Player player, List<String> lines, ResponseHandler responseHandler) {
        this.player = player;
        this.lines = lines;
        this.responseHandler = responseHandler;
        this.lines.addAll(Collections.nCopies(4 - lines.size(), ""));
        this.open();
    }

    public static SignPrompt of(Player player, ResponseHandler responseHandler, String... lines) {
        return of(player, new ArrayList<>(Arrays.asList(lines)), responseHandler);
    }

    public static SignPrompt of(Player player, List<String> lines, ResponseHandler responseHandler) {
        return new SignPrompt(player, lines, responseHandler);
    }

    private void open() {
        Location location = this.player.getLocation();
        BlockPosition position = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());

        PacketContainer openSignPacket = PROTOCOL.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        PacketContainer setSignPacket = PROTOCOL.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        PacketContainer updateSignPacket = PROTOCOL.createPacket(PacketType.Play.Server.UPDATE_SIGN);

        openSignPacket.getBlockPositionModifier().write(0, position);

        setSignPacket.getBlockPositionModifier().write(0, position);
        setSignPacket.getBlockData().write(0, WrappedBlockData.createData(Material.valueOf("SIGN_POST")));
        updateSignPacket.getBlockPositionModifier().write(0, position);
        updateSignPacket.getChatComponentArrays().write(0, this.lines.stream()
                .map(WrappedChatComponent::fromText)
                .toArray(WrappedChatComponent[]::new));

        Protocol.sendPackets(this.player, setSignPacket, updateSignPacket, openSignPacket, setSignPacket);

        AtomicBoolean active = new AtomicBoolean(true);

        Protocol.subscribe(PacketType.Play.Client.UPDATE_SIGN)
                .filter((event) -> event.getPlayer().getUniqueId().equals(this.player.getUniqueId()))
                .biHandler((sub, event) -> {
                    if (!active.getAndSet(false)) {
                        return;
                    }
                    PacketContainer packet = event.getPacket();
                    BlockPosition signPosition = packet.getBlockPositionModifier().read(0);
                    WrappedChatComponent[] components = packet.getChatComponentArrays().read(0);
                    if (signPosition == null) {
                        return;
                    }
                    Response response = this.responseHandler.handle(Arrays.stream(components)
                            .map(WrappedChatComponent::getJson)
                            .map((json) -> json.replace("\"", ""))
                            .collect(Collectors.toList()));
                    if (response.equals(Response.TRY_AGAIN)) {
                        Schedulers.sync().runLater(() -> {
                            if (this.player.isOnline()) {
                                new SignPrompt(this.player, this.lines, this.responseHandler);
                            }
                        }, 1L);
                    }
                    sub.close();
                    this.player.sendBlockChange(location, Material.AIR, (byte) 0);
                });
    }

    public interface ResponseHandler {

        Response handle(List<String> lines);

    }

    public enum Response {

        ACCEPTED,
        TRY_AGAIN
    }
}