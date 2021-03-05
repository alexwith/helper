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

package me.hyfe.helper.scheduler.threadlock;

import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.promise.ThreadContext;
import org.bukkit.Bukkit;

import java.util.concurrent.CountDownLatch;

final class ServerThreadLockImpl implements ServerThreadLock {
    private final CountDownLatch obtainedSignal = new CountDownLatch(1);
    private final CountDownLatch doneSignal = new CountDownLatch(1);

    ServerThreadLockImpl() {
        if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            this.obtainedSignal.countDown();
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderUtils.getPlugin(), this::signal);
        this.await();
    }

    @Override
    public void close() {
        this.doneSignal.countDown();
    }

    private void await() {
        try {
            this.obtainedSignal.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void signal() {
        this.obtainedSignal.countDown();
        try {
            this.doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
