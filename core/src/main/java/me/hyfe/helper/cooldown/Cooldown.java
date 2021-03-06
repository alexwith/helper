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

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hyfe.helper.gson.GsonSerializable;
import me.hyfe.helper.scheduler.Ticks;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

public interface Cooldown extends GsonSerializable {
    static Cooldown deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("lastTested"));
        Preconditions.checkArgument(object.has("timeout"));

        long lastTested = object.get("lastTested").getAsLong();
        long timeout = object.get("timeout").getAsLong();

        Cooldown c = of(timeout, TimeUnit.MILLISECONDS);
        c.setLastTested(lastTested);
        return c;
    }

    static Cooldown ofTicks(long ticks) {
        return new CooldownImpl(Ticks.to(ticks, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    static Cooldown of(long amount, TimeUnit unit) {
        return new CooldownImpl(amount, unit);
    }

    default boolean test() {
        if (!this.testSilently()) {
            return false;
        }

        this.reset();
        return true;
    }

    default boolean testSilently() {
        return elapsed() > getTimeout();
    }

    default long elapsed() {
        return System.currentTimeMillis() - getLastTested().orElse(0);
    }

    default void reset() {
        this.setLastTested(System.currentTimeMillis());
    }

    default long remainingMillis() {
        long diff = this.elapsed();
        return diff > this.getTimeout() ? 0L : this.getTimeout() - diff;
    }

    default long remainingTime(TimeUnit unit) {
        return Math.max(0L, unit.convert(this.remainingMillis(), TimeUnit.MILLISECONDS));
    }

    OptionalLong getLastTested();

    void setLastTested(long time);

    long getTimeout();

    Cooldown copy();
}
