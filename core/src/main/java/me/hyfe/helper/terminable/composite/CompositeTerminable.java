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

package me.hyfe.helper.terminable.composite;

import me.hyfe.helper.terminable.Terminable;
import me.hyfe.helper.terminable.TerminableConsumer;

public interface CompositeTerminable extends Terminable, TerminableConsumer {

    static CompositeTerminable create() {
        return new AbstractCompositeTerminable();
    }

    static CompositeTerminable createWeak() {
        return new AbstractWeakCompositeTerminable();
    }

    @Override
    void close() throws CompositeClosingException;


    @Override
    default CompositeClosingException closeSilently() {
        try {
            close();
            return null;
        } catch (CompositeClosingException e) {
            return e;
        }
    }

    @Override
    default void closeAndReportException() {
        try {
            close();
        } catch (CompositeClosingException e) {
            e.printAllStackTraces();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompositeTerminable with(AutoCloseable autoCloseable);

    default CompositeTerminable withAll(AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }

    default CompositeTerminable withAll(Iterable<? extends AutoCloseable> autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }
    
    @Override
    default <T extends AutoCloseable> T bind(T terminable) {
        with(terminable);
        return terminable;
    }

    void cleanup();
}
