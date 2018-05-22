package com.alibaba.tac.console;

import com.alibaba.tac.console.sdk.MenuOptionHandler;
import com.alibaba.tac.console.test.TacConsoleTest;

import javax.annotation.Resource;

import java.util.concurrent.CountDownLatch;

/**
 * @author jinshuan.li 28/02/2018 15:20
 */
public class MenuOptionHandlerTest extends TacConsoleTest {

    @Resource
    private MenuOptionHandler menuOptionHandler;

    private CountDownLatch countDownLatch=new CountDownLatch(1);


}