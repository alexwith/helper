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

package me.hyfe.helper.cooldown;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

public interface CooldownMap<T> {

    static <T> CooldownMap<T> create(Cooldown base) {
        Objects.requireNonNull(base, "base");
        return new CooldownMapImpl<>(base);
    }

    Cooldown getBase();

    Cooldown get(T key);

    void put(T key, Cooldown cooldown);

    Map<T, Cooldown> getAll();

    default boolean test(T key) {
        return get(key).test();
    }

    default boolean testSilently(T key) {
        return get(key).testSilently();
    }

    default long elapsed(T key) {
        return get(key).elapsed();
    }

    default void reset(T key) {
        get(key).reset();
    }

    default long remainingMillis(T key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(T key, TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    default OptionalLong getLastTested(T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(T key, long time) {
        get(key).setLastTested(time);
    }

}
