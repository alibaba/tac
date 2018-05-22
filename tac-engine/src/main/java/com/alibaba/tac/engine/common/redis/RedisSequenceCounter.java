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

package com.alibaba.tac.engine.common.redis;

import com.alibaba.tac.engine.common.SequenceCounter;
import com.alibaba.tac.engine.util.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jinshuan.li 2018/3/1 07:35
 */
@Slf4j
public class RedisSequenceCounter implements SequenceCounter {

    /**
     * see counterRedisTemplate's ValueSerializer  , the value will be convert to string ,then do incr
     *
     */
    @Resource(name = "counterRedisTemplate")
    private ValueOperations<String, String> valueOperations;

    private String counterKey;

    public RedisSequenceCounter(String counterKey) {

        this.counterKey = counterKey;

    }

    @Override
    public void set(long value) {

        valueOperations.set(counterKey, String.valueOf(value));
    }

    @Override
    public Long get() {

        String s = valueOperations.get(counterKey);
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        return Long.valueOf(s);

    }

    @Override
    public long incrementAndGet() {

        Long increment = valueOperations.increment(counterKey, 1);
        if (increment != null) {
            return increment;
        }
        return 0L;

    }

    @Override
    public long increBy(long delta) {

        Long increment = valueOperations.increment(counterKey, delta);
        if (increment != null) {
            return increment;
        }
        return 0L;
    }
}
