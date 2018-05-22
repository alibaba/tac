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

package com.alibaba.tac.engine.inst.service.redis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.tac.engine.common.redis.RedisSequenceCounter;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.properties.TacRedisConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jinshuan.li 2018/3/1 07:58
 */
@Slf4j
public class RedisMsInstService implements IMsInstService {

    /**
     * 计数器
     */
    @Resource(name = "msInstIdCounter")
    private RedisSequenceCounter sequenceCounter;

    @Resource
    private TacRedisConfigProperties tacRedisConfigProperties;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, JSONObject> redisTemplate;

    @Resource(name = "redisTemplate")
    HashOperations<String, String, TacInst> hashOperations;

    @Override
    public List<TacInst> getAllTacMsInsts() {

        return hashOperations.values(getMainKey());

    }

    @Override
    public TacInst getTacMsInst(Long msInstId) {

        TacInst data = hashOperations.get(getMainKey(), String.valueOf(msInstId));
        if (data != null) {
            return data;
        }
        return null;
    }

    @Override
    public TacInst createTacMsInst(TacInst tacInst) {

        // 校验参数
        checkInst(tacInst);

        long msInstId = sequenceCounter.incrementAndGet();
        if (msInstId <= 0) {
            throw new IllegalStateException("error on get id ");
        }

        tacInst.setId(msInstId);

        hashOperations.putIfAbsent(getMainKey(), String.valueOf(tacInst.getId()), tacInst);

        hashOperations.put(getMainKey() + tacInst.getMsCode(), String.valueOf(tacInst.getId()), tacInst);

        return tacInst;
    }

    @Override
    public Boolean updateTacMsInst(Long instId, TacInst tacInst) {

        tacInst.setId(instId);

        hashOperations.put(getMainKey(), String.valueOf(instId), tacInst);

        hashOperations.put(getMainKey() + tacInst.getMsCode(), String.valueOf(tacInst.getId()), tacInst);

        return true;
    }

    @Override
    public Boolean removeMsInst(Long instId) {
        String key = String.valueOf(instId);

        TacInst tacInst = hashOperations.get(getMainKey(), key);
        if (tacInst != null) {
            hashOperations.delete(getMainKey() + tacInst.getMsCode(), key);
        }
        hashOperations.delete(getMainKey(), key);

        return true;
    }

    @Override
    public List<TacInst> getMsInsts(String code) {

        return hashOperations.values(getMainKey() + code);
    }

    private void checkInst(TacInst tacInst) {

        if (StringUtils.isEmpty(tacInst.getJarVersion()) && StringUtils.isEmpty(tacInst.getGitBranch())) {
            throw new IllegalArgumentException("jar version or branch is empty");
        }
        String msCode = tacInst.getMsCode();
        if (StringUtils.isEmpty(msCode)) {
            throw new IllegalArgumentException("msCode");
        }
    }

    private String getMainKey() {

        return tacRedisConfigProperties.getMsInstMetaDataPath();
    }
}
