package com.alibaba.tac.engine.service;

import com.alibaba.tac.engine.code.CodeCompileService;
import com.alibaba.tac.engine.git.GitRepoService;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.service.IMsPublisher;
import com.alibaba.tac.engine.ms.service.IMsService;
import com.alibaba.tac.engine.test.TacEnginTest;
import com.alibaba.tac.sdk.error.ServiceException;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 08/05/2018 10:32
 */
public class GitLabFeatureTest extends TacEnginTest {

    @Resource
    private IMsService msService;

    @Resource
    private IMsInstService msInstService;

    @Resource
    private GitRepoService gitRepoService;

    @Resource
    private CodeCompileService codeCompileService;

    @Resource
    private IMsPublisher msPublisher;

    private final String code = "gitlab-test";

    private final String defaultBranch = "master";

    @Test
    public void createMs() {

        TacMsDO tacMsDO = new TacMsDO();
        tacMsDO.setCode("gitlab-test");
        tacMsDO.setName("gitlab测试");
        tacMsDO.setGitSupport(true);
        tacMsDO.setGitRepo("git@127.0.0.1:tac-admin/tac-test01.git");

        TacMsDO ms = msService.createMs(tacMsDO);

        assertNotNull(ms);
    }

    @Test
    public void getMs() {

        TacMsDO ms = msService.getMs(code);

        assertNotNull(ms);
    }

    @Test
    public void createMsInst() {

        TacInst gitTacMsInst = msInstService.createGitTacMsInst(code, code, defaultBranch);

        assertNotNull(gitTacMsInst);

        List<TacInst> msInsts = msInstService.getMsInsts(code);

        assertNotNull(msInsts);
    }

    @Test
    public void prePublishTest() throws ServiceException, IOException {

        TacMsDO ms = msService.getMs(code);

        List<TacInst> allTacMsInsts = msInstService.getMsInsts(code);

        TacInst tacInst = allTacMsInsts.get(0);

        String sourcePath = gitRepoService.pullInstanceCode(ms.getGitRepo(), ms.getCode(), tacInst.getGitBranch());

        codeCompileService.compile(tacInst.getId(), sourcePath);

        byte[] jarFile = codeCompileService.getJarFile(tacInst.getId());

        msPublisher.prePublish(tacInst, jarFile);

    }
}
