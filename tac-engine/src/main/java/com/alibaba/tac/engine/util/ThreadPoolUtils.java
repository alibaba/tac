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

package com.alibaba.tac.engine.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinshuan.li on 2018/8/19 15:47.
 */
public class ThreadPoolUtils {

    /**
     *  create thrad pool
     * @param nThreads
     * @param threadName
     * @return
     */
    public static ExecutorService createThreadPool(int nThreads, String threadName) {

        ExecutorService executorService = new ThreadPoolExecutor(nThreads, 300, 5, TimeUnit.MINUTES, new
            LinkedBlockingQueue<>(), new ThreadFactory() {

            private AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadName + "_" + count.incrementAndGet());
            }
        });
        return executorService;
    }
}
