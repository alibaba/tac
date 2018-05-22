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

package com.alibaba.tac.engine.ms.service.redis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.domain.TacMsStatus;
import com.alibaba.tac.engine.ms.service.IMsService;
import com.alibaba.tac.engine.properties.TacRedisConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jinshuan.li 2018/3/1 07:59
 */
@Slf4j
public class RedisMsService implements IMsService {

    @Resource
    private TacRedisConfigProperties tacRedisConfigProperties;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, JSONObject> redisTemplate;

    @Resource(name = "redisTemplate")
    HashOperations<String, String, TacMsDO> hashOperations;

    @Override
    public TacMsDO createMs(TacMsDO tacMsDO) {

        checkMsDO(tacMsDO);

        hashOperations.put(getMainKey(), tacMsDO.getCode(), tacMsDO);

        return tacMsDO;
    }

    @Override
    public Boolean removeMs(String msCode) {

        hashOperations.delete(getMainKey(), msCode);

        return true;
    }

    @Override
    public Boolean invalidMs(String msCode) {

        TacMsDO ms = this.getMs(msCode);
        if (ms == null) {
            return true;
        }

        ms.setStatus(TacMsStatus.INVALID.code());

        return this.updateMs(msCode, ms);
    }

    @Override
    public Boolean updateMs(String msCode, TacMsDO tacMsDO) {


        hashOperations.put(getMainKey(), msCode, tacMsDO);

        return true;
    }

    @Override
    public TacMsDO getMs(String msCode) {

        return hashOperations.get(getMainKey(), msCode);
    }

    @Override
    public List<TacMsDO> getAllMs() {
        return hashOperations.values(getMainKey());
    }

    private String getMainKey() {

        return tacRedisConfigProperties.getMsMetaDataPath();

    }
}
