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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MetadataMap {

    static MetadataMap create() {
        return new MetadataMapImpl();
    }

    <T> void put(MetadataKey<T> key, T value);

    <T> void put(MetadataKey<T> key, TransientValue<T> value);

    <T> void forcePut(MetadataKey<T> key, T value);

    <T> void forcePut(MetadataKey<T> key, TransientValue<T> value);

    <T> boolean putIfAbsent(MetadataKey<T> key, T value);

    <T> boolean putIfAbsent(MetadataKey<T> key, TransientValue<T> value);

    <T> Optional<T> get(MetadataKey<T> key);

    <T> boolean ifPresent(MetadataKey<T> key, Consumer<? super T> action);

    <T> T getOrNull(MetadataKey<T> key);

    <T> T getOrDefault(MetadataKey<T> key, T def);

    <T> T getOrPut(MetadataKey<T> key, Supplier<? extends T> def);

    <T> T getOrPutExpiring(MetadataKey<T> key, Supplier<? extends TransientValue<T>> def);

    boolean has(MetadataKey<?> key);

    boolean remove(MetadataKey<?> key);

    void clear();

    ImmutableMap<MetadataKey<?>, Object> asMap();

    boolean isEmpty();

    void cleanup();
}
