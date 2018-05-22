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
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.IMsInstFileService;
import com.alibaba.tac.engine.properties.TacRedisConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jinshuan.li 2018/3/1 07:57
 */
@Slf4j
public class RedisMsInstFileService implements IMsInstFileService {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, byte[]> redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, JSONObject> fileDataMetaOperations;

    @Resource
    private TacRedisConfigProperties redisConfig;

    private RedisSerializer keySerializer;

    private Boolean prePublish = false;

    @PostConstruct
    public void init() {

        keySerializer = redisTemplate.getKeySerializer();

    }

    public RedisMsInstFileService(Boolean prePublish) {
        this.prePublish = prePublish;
    }

    @Override
    public byte[] getInstanceFile(long instId) {

        String dataPath = getDataPath(instId, null);

        JSONObject jsonObject = fileDataMetaOperations.get(dataPath);
        if (jsonObject == null) {
            return null;
        }
        String jarVersion = jsonObject.getString("jarVersion");

        dataPath = getDataPath(instId, jarVersion);

        String finalDataPath = dataPath;

        // the bytes type should get the raw data, don't serializ it
        byte[] bytes = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(keySerializer.serialize(finalDataPath));
            }
        });

        return bytes;
    }

    @Override
    public Boolean saveInstanceFile(TacInst tacInst, byte[] data) {

        String jarVersion;
        if (this.prePublish) {
            jarVersion = tacInst.getPrePublishJarVersion();
        } else {
            jarVersion = tacInst.getJarVersion();
        }

        if (StringUtils.isEmpty(jarVersion)) {
            throw new IllegalArgumentException("jarVersion");
        }
        long instId = tacInst.getId();
        if (instId <= 0) {
            throw new IllegalArgumentException("instId");
        }

        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        String dataPath = getDataPath(instId, null);
        JSONObject metaData = new JSONObject();
        metaData.put("jarVersion", jarVersion);
        fileDataMetaOperations.set(dataPath, metaData);

        dataPath = getDataPath(instId, jarVersion);

        String finalDataPath = dataPath;
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.set(keySerializer.serialize(finalDataPath), data);

                return null;
            }
        });

        return true;

    }

    /**
     * get data path
     *
     * @param instId
     * @param jarVersion
     * @return
     */
    private String getDataPath(Long instId, String jarVersion) {

        String dataPathPrefix = redisConfig.getDataPathPrefix();
        if (this.prePublish) {
            dataPathPrefix += ".prepublish.";
        }
        if (StringUtils.isEmpty(jarVersion)) {
            return String.format("%s.%d", dataPathPrefix, instId);
        }
        return String.format("%s.%d.%s", dataPathPrefix, instId,
            jarVersion);

    }
}
