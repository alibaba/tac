/*
 *   MIT License
 *
 *   Copyright (c) 2016 Alibaba Group
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.alibaba.tac.engine.code;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * the CustomerClassLoader
 */
public class CustomerClassLoader extends URLClassLoader {

    static {

        extClassLoader = CustomerClassLoader.getSystemClassLoader().getParent();
    }

    private static ClassLoader extClassLoader;


    public CustomerClassLoader(URL[] urls) {
        super(urls, CustomerClassLoader.class.getClassLoader());
    }

    public CustomerClassLoader(File file) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, CustomerClassLoader.class.getClassLoader());
    }

    public CustomerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public CustomerClassLoader(File file, ClassLoader parent) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
    }

    /**
     *
     * @param name
     * @param resolve
     * @return
     * @throws ClassNotFoundException
     *
     * @see ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        ClassLoader parent = this.getParent();

        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
