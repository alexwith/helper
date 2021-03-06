/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.hyfe.helper.metadata;

import me.hyfe.helper.Events;
import me.hyfe.helper.Schedulers;
import me.hyfe.helper.metadata.type.BlockMetadataRegistry;
import me.hyfe.helper.metadata.type.EntityMetadataRegistry;
import me.hyfe.helper.metadata.type.PlayerMetadataRegistry;
import me.hyfe.helper.metadata.type.WorldMetadataRegistry;
import me.hyfe.helper.serialize.BlockPosition;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Metadata {
    private static final AtomicBoolean SETUP = new AtomicBoolean(false);

    private static void ensureSetup() {
        if (SETUP.get()) {
            return;
        }
        if (!SETUP.getAndSet(true)) {
            Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR)
                    .handler(e -> StandardMetadataRegistries.PLAYER.remove(e.getPlayer().getUniqueId()));
            Schedulers.builder()
                    .async()
                    .afterAndEvery(1, TimeUnit.MINUTES)
                    .run(() -> {
                        for (MetadataRegistry<?> registry : StandardMetadataRegistries.values()) {
                            registry.cleanup();
                        }
                    });
        }
    }

    public static PlayerMetadataRegistry players() {
        ensureSetup();
        return StandardMetadataRegistries.PLAYER;
    }

    public static EntityMetadataRegistry entities() {
        ensureSetup();
        return StandardMetadataRegistries.ENTITY;
    }

    public static BlockMetadataRegistry blocks() {
        ensureSetup();
        return StandardMetadataRegistries.BLOCK;
    }

    public static WorldMetadataRegistry worlds() {
        ensureSetup();
        return StandardMetadataRegistries.WORLD;
    }

    public static MetadataMap provide(Object obj) {
        Objects.requireNonNull(obj, "obj");
        if (obj instanceof Player) {
            return provideForPlayer(((Player) obj));
        } else if (obj instanceof UUID) {
            return provideForPlayer(((UUID) obj));
        } else if (obj instanceof Entity) {
            return provideForEntity(((Entity) obj));
        } else if (obj instanceof Block) {
            return provideForBlock(((Block) obj));
        } else if (obj instanceof World) {
            return provideForWorld(((World) obj));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
        }
    }

    public static Optional<MetadataMap> get(Object obj) {
        Objects.requireNonNull(obj, "obj");
        if (obj instanceof Player) {
            return getForPlayer(((Player) obj));
        } else if (obj instanceof UUID) {
            return getForPlayer(((UUID) obj));
        } else if (obj instanceof Entity) {
            return getForEntity(((Entity) obj));
        } else if (obj instanceof Block) {
            return getForBlock(((Block) obj));
        } else if (obj instanceof World) {
            return getForWorld(((World) obj));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
        }
    }

    public static MetadataMap provideForPlayer(UUID uuid) {
        return players().provide(uuid);
    }

    public static MetadataMap provideForPlayer(Player player) {
        return players().provide(player);
    }

    public static Optional<MetadataMap> getForPlayer(UUID uuid) {
        return players().get(uuid);
    }

    public static Optional<MetadataMap> getForPlayer(Player player) {
        return players().get(player);
    }

    public static <T> Map<Player, T> lookupPlayersWithKey(MetadataKey<T> key) {
        return players().getAllWithKey(key);
    }

    public static MetadataMap provideForEntity(UUID uuid) {
        return entities().provide(uuid);
    }

    public static MetadataMap provideForEntity(Entity entity) {
        return entities().provide(entity);
    }

    public static Optional<MetadataMap> getForEntity(UUID uuid) {
        return entities().get(uuid);
    }

    public static Optional<MetadataMap> getForEntity(Entity entity) {
        return entities().get(entity);
    }

    public static <T> Map<Entity, T> lookupEntitiesWithKey(MetadataKey<T> key) {
        return entities().getAllWithKey(key);
    }

    public static MetadataMap provideForBlock(BlockPosition block) {
        return blocks().provide(block);
    }

    public static MetadataMap provideForBlock(Block block) {
        return blocks().provide(block);
    }

    public static Optional<MetadataMap> getForBlock(BlockPosition block) {
        return blocks().get(block);
    }

    public static Optional<MetadataMap> getForBlock(Block block) {
        return blocks().get(block);
    }

    public static <T> Map<BlockPosition, T> lookupBlocksWithKey(MetadataKey<T> key) {
        return blocks().getAllWithKey(key);
    }

    public static MetadataMap provideForWorld(UUID uid) {
        return worlds().provide(uid);
    }

    public static MetadataMap provideForWorld(World world) {
        return worlds().provide(world);
    }

    public static Optional<MetadataMap> getForWorld(UUID uid) {
        return worlds().get(uid);
    }

    public static Optional<MetadataMap> getForWorld(World world) {
        return worlds().get(world);
    }

    public static <T> Map<World, T> lookupWorldsWithKey(MetadataKey<T> key) {
        return worlds().getAllWithKey(key);
    }
}
