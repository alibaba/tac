package com.alibaba.tac.engine.ms.service.redis;

import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.domain.TacMsStatus;
import com.alibaba.tac.engine.test.TacEnginTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 01/03/2018 14:53
 */
public class RedisMsServiceTest extends TacEnginTest {

    @Resource
    private RedisMsService redisMsService;

    TacMsDO tacMsDO = new TacMsDO();

    @Before
    public void setUp() throws Exception {

        tacMsDO.setCode("test");
        tacMsDO.setName("ljinshuan");

    }

    @Test
    public void createMs() {

        redisMsService.createMs(tacMsDO);

        TacMsDO ms = redisMsService.getMs(tacMsDO.getCode());

        assertNotNull(ms);
    }

    @Test
    public void removeMs() {

        redisMsService.removeMs(tacMsDO.getCode());

        TacMsDO ms = redisMsService.getMs(tacMsDO.getCode());

        assertNull(ms);
    }

    @Test
    public void invalidMs() {

        redisMsService.invalidMs(tacMsDO.getCode());

        TacMsDO ms = redisMsService.getMs(tacMsDO.getCode());

        assertTrue(ms == null || ms.getStatus().equals(TacMsStatus.INVALID.code()));
    }

    @Test
    public void updateMs() {
        tacMsDO.setName("updated");

        redisMsService.updateMs(tacMsDO.getCode(), tacMsDO);

        TacMsDO ms = redisMsService.getMs(tacMsDO.getCode());

        assertEquals(ms.getName(), "updated");
    }

    @Test
    public void getMs() {

        TacMsDO ms = redisMsService.getMs(tacMsDO.getCode());

        assertNull(ms);
    }

    @Test
    public void getAllMs() {

        List<TacMsDO> allMs = redisMsService.getAllMs();

        assertTrue(CollectionUtils.isNotEmpty(allMs));
    }

}