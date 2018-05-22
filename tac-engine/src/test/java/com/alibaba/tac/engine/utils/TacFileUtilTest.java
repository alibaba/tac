package com.alibaba.tac.engine.utils;

import com.alibaba.tac.engine.test.TacEnginTest;
import com.alibaba.tac.engine.service.TacFileService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author jinshuan.li 27/02/2018 20:29
 */
public class TacFileUtilTest extends TacEnginTest {

    @Resource
    private TacFileService tacFileService;

    @Test
    public void getClassFileOutputPath() {

        String classFileOutputPath = tacFileService.getClassFileOutputPath(1L);

        System.out.println(classFileOutputPath);
    }

}
