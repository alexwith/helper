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

import com.google.common.collect.ImmutableMap;
import me.hyfe.helper.metadata.type.BlockMetadataRegistry;
import me.hyfe.helper.metadata.type.EntityMetadataRegistry;
import me.hyfe.helper.metadata.type.PlayerMetadataRegistry;
import me.hyfe.helper.metadata.type.WorldMetadataRegistry;
import me.hyfe.helper.serialize.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

final class StandardMetadataRegistries {
    public static final PlayerMetadataRegistry PLAYER = new PlayerRegistry();
    public static final EntityMetadataRegistry ENTITY = new EntityRegistry();
    public static final BlockMetadataRegistry BLOCK = new BlockRegistry();
    public static final WorldMetadataRegistry WORLD = new WorldRegistry();

    private static final MetadataRegistry<?>[] VALUES = new MetadataRegistry[]{PLAYER, ENTITY, BLOCK, WORLD};

    public static MetadataRegistry<?>[] values() {
        return VALUES;
    }

    private static final class PlayerRegistry extends AbstractMetadataRegistry<UUID> implements PlayerMetadataRegistry {

        @Override
        public MetadataMap provide(Player player) {
            Objects.requireNonNull(player, "player");
            return provide(player.getUniqueId());
        }

        @Override
        public MetadataMap get(Player player) {
            Objects.requireNonNull(player, "player");
            return get(player.getUniqueId());
        }

        @Override
        public <K> Map<Player, K> getAllWithKey(MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<Player, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> {
                K t = map.get(key);
                if (t != null) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        ret.put(player, t);
                    }
                }
            });
            return ret.build();
        }
    }

    private static final class EntityRegistry extends AbstractMetadataRegistry<UUID> implements EntityMetadataRegistry {

        @Override
        public MetadataMap provide(Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return provide(entity.getUniqueId());
        }

        @Override
        public MetadataMap get(Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return get(entity.getUniqueId());
        }

        @Override
        public <K> Map<Entity, K> getAllWithKey(MetadataKey<K> key) {
            return null;
        }
    }

    private static final class BlockRegistry extends AbstractMetadataRegistry<BlockPosition> implements BlockMetadataRegistry {

        @Override
        public MetadataMap provide(Block block) {
            Objects.requireNonNull(block, "block");
            return provide(BlockPosition.of(block));
        }

        @Override
        public MetadataMap get(Block block) {
            Objects.requireNonNull(block, "block");
            return get(BlockPosition.of(block));
        }

        @Override
        public <K> Map<BlockPosition, K> getAllWithKey(MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<BlockPosition, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((pos, map) -> {
                K t = map.get(key);
                if (t != null) {
                    ret.put(pos, t);
                }
            });
            return ret.build();
        }
    }

    private static final class WorldRegistry extends AbstractMetadataRegistry<UUID> implements WorldMetadataRegistry {

        @Override
        public MetadataMap provide(World world) {
            Objects.requireNonNull(world, "world");
            return provide(world.getUID());
        }

        @Override
        public MetadataMap get(World world) {
            Objects.requireNonNull(world, "world");
            return get(world.getUID());
        }

        @Override
        public <K> Map<World, K> getAllWithKey(MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<World, K> ret = ImmutableMap.builder();
            this.cache.asMap().forEach((uuid, map) -> {
                K t = map.get(key);
                if (t != null) {
                    World world = Bukkit.getWorld(uuid);
                    if (world != null) {
                        ret.put(world, t);
                    }
                }
            });
            return ret.build();
        }
    }
}
