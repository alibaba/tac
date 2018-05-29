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
import com.alibaba.tac.engine.code.CodeCompileService;
import com.alibaba.tac.engine.git.GitRepoService;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.DevMsInstFileService;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.inst.service.redis.RedisMsInstFileService;
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.domain.TacMsPublishMeta;
import com.alibaba.tac.engine.ms.service.AbstractDefaultMsPublisher;
import com.alibaba.tac.engine.ms.service.IMsService;
import com.alibaba.tac.engine.properties.TacMsConstants;
import com.alibaba.tac.engine.properties.TacRedisConfigProperties;
import com.alibaba.tac.engine.code.TacFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author jinshuan.li 2018/3/1 07:58 the redis ms publisher
 */
@Slf4j
public class RedisMsPublisher extends AbstractDefaultMsPublisher {

    @Resource
    private DevMsInstFileService devMsInstFileService;

    @Resource(name = "remoteMsInstFileService")
    private RedisMsInstFileService redisMsInstFileService;

    @Resource(name = "prePublishMsInstFileService")
    private RedisMsInstFileService prePublishMsInstFileService;

    @Resource
    private TacRedisConfigProperties tacRedisConfigProperties;

    @Resource(name = "redisTemplate")
    HashOperations<String, String, TacMsPublishMeta> hashOperations;

    @Resource
    private GitRepoService gitRepoService;

    @Resource
    private IMsInstService msInstService;

    @Resource
    private CodeCompileService codeCompileService;

    @Resource
    private IMsService msService;

    /**
     * the subsciber is serializ with string ,then the publisher should serialize with string too;
     */
    @Resource(name = "counterRedisTemplate")
    private RedisTemplate<String, JSONObject> redisTopicTemplate;

    @Override
    public Boolean publish(TacInst tacInst, byte[] data) {

        byte[] instanceFile = data;

        tacInst.setJarVersion(TacFileService.getMd5(data));
        tacInst.setStatus(TacInst.STATUS_PUBLISH);
        // 1 save data
        redisMsInstFileService.saveInstanceFile(tacInst, instanceFile);

        // 2. save meta data

        String msCode = tacInst.getMsCode();

        TacMsPublishMeta publishMeta = new TacMsPublishMeta(tacInst);

        hashOperations.put(getMainKey(), msCode, publishMeta);

        // 3. send message
        redisTopicTemplate.convertAndSend(getPublishChannel(), JSONObject.toJSONString(publishMeta));

        TacMsDO ms = msService.getMs(tacInst.getMsCode());

        // 4 update ms

        Long oldPublishId = ms.getPublishedInstId();

        ms.setPublishedInstId(tacInst.getId());
        msService.updateMs(tacInst.getMsCode(), ms);

        // 5 update instance

        msInstService.updateTacMsInst(tacInst.getId(), tacInst);

        if (oldPublishId != null && !oldPublishId.equals(tacInst.getId())) {
            TacInst tacMsInst = msInstService.getTacMsInst(oldPublishId);

            if (tacMsInst != null) {
                tacMsInst.setStatus(TacInst.STATUS_PRE_PUBLISH);
                msInstService.updateTacMsInst(oldPublishId, tacMsInst);
            }
        }

        return true;
    }

    @Override
    public Boolean offline(TacInst tacInst) {

        String msCode = tacInst.getMsCode();

        // 1. update data
        TacMsPublishMeta publishMeta = new TacMsPublishMeta(tacInst, TacMsConstants.INST_STATUS_OFFLINE);

        hashOperations.put(getMainKey(), msCode, publishMeta);

        // 2. send message
        redisTopicTemplate.convertAndSend(getPublishChannel(), JSONObject.toJSONString(publishMeta));

        return true;
    }

    private String getMainKey() {

        return tacRedisConfigProperties.getMsListPath();
    }

    private String getPublishChannel() {

        return tacRedisConfigProperties.getPublishEventChannel();
    }

    @Override
    public Boolean prePublish(TacInst tacInst, byte[] data) {

        byte[] instanceFile = data;
        tacInst.setStatus(TacInst.STATUS_PRE_PUBLISH);
        tacInst.setPrePublishJarVersion(TacFileService.getMd5(data));

        // 1 save data
        prePublishMsInstFileService.saveInstanceFile(tacInst, instanceFile);

        // 2 update meta data

        msInstService.updateTacMsInst(tacInst.getId(), tacInst);

        return true;

    }

    @Override
    public TacInst gitPrePublish(TacMsDO tacMsDO, TacInst tacInst) {

        if (StringUtils.isEmpty(tacMsDO.getGitRepo())) {
            throw new IllegalArgumentException("no git repo address");
        }

        String gitBranch = tacInst.getGitBranch();

        if (StringUtils.isEmpty(gitBranch)) {
            throw new IllegalArgumentException("git branch is emplty");
        }
        TacMsDO ms = tacMsDO;

        String sourcePath = gitRepoService.pullInstanceCode(ms.getGitRepo(), ms.getCode(), tacInst.getGitBranch());

        try {
            codeCompileService.compile(tacInst.getId(), sourcePath);

            byte[] jarFile = codeCompileService.getJarFile(tacInst.getId());

            this.prePublish(tacInst, jarFile);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return msInstService.getTacMsInst(tacInst.getId());
    }

}
