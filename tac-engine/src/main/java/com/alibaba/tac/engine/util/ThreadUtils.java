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

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author jinshuan.li
 */
public class ThreadUtils {

    public static ExecutorService executorService = ThreadPoolUtils.createThreadPool(30, "tac_batch_process");

    /**
     * execute tasks and wait all complete
     *
     * @param tasks
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <T> List<T> runWaitCompleteTask(List<Callable<T>> tasks, Long timeout, TimeUnit timeUnit)
        throws Exception {

        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<T>();
        }

        CompletionService<T> completionService = new ExecutorCompletionService<T>(executorService);

        for (Callable<T> runnable : tasks) {
            completionService.submit(runnable);

        }

        Future<T> resultFuture = null;

        List<T> result = new ArrayList<T>();

        for (int i = 0; i < tasks.size(); i++) {
            resultFuture = completionService.take();
            result.add(resultFuture.get(timeout, timeUnit));
        }
        return result;

    }

    public static <T> Future<T> runAsync(Callable<T> task) {
        return executorService.submit(task);
    }

    public static <T> Future<T> runAsync(ExecutorService executor, Callable<T> task) {
        return executor.submit(task);
    }

    public static void sleep(long time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }
}
