package com.alibaba.tac.engine.service;

import com.alibaba.tac.engine.code.CodeCompileService;
import com.alibaba.tac.engine.test.TacEnginTest;
import com.alibaba.tac.sdk.common.TacResult;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author jinshuan.li 12/02/2018 08:49
 */

public class JdkCompilerTest extends TacEnginTest {

    @Resource
    private CodeCompileService codeCompileService;

    @Resource
    private TacInstRunService tacInstService;

    private String src = "/Users/jinshuan.li/Source/open-tac/tac-dev-source";

    private String msCode = "abcd";

    @Before
    public void beforeTest() throws Exception {

    }

    @Test
    public void testCompiler() throws Exception {


        codeCompileService.compile(msCode, src);
    }

    @Test
    public void testPackage() throws IOException {

        byte[] jarFile = codeCompileService.getJarFile(msCode);

        assertNotNull(jarFile);
    }

    @Test
    public void runCodeTest() throws Exception {

        TacResult<Object> tacResult = tacInstService.runWithLoad("msCode", 1024L, Maps.newHashMap());

        System.out.println(tacResult);

    }

    String getCurrentPath() {
        File file = new File("");
        return file.getAbsolutePath();
    }
}
