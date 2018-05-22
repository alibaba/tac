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

package com.alibaba.tac.console.sdk;

import com.alibaba.tac.console.ConsoleConstants;
import com.alibaba.tac.engine.code.CodeCompileService;
import com.alibaba.tac.engine.service.TacFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author jinshuan.li 27/02/2018 21:05
 */
@Slf4j
@Service
public class MenuOptionHandler {

    @Resource
    private ApplicationArguments arguments;

    @Resource
    private CodeCompileService codeCompileService;

    @Resource
    private TacFileService tacFileService;

    @PostConstruct
    public void init() {

    }

    /**
     * handle the menu option
     * <p>
     * the is just a package command
     *
     * @throws IOException
     */
    public void handleMenuOption() throws IOException {

        if (CollectionUtils.isEmpty(arguments.getOptionNames())) {
            printUsage();
            return;
        }

        if (arguments.containsOption(ConsoleConstants.MENU_PACKAGE)) {
            handlePackage();
        }

        if (arguments.containsOption(ConsoleConstants.MENU_PUBLISH)) {

            //handlePublish();
        }
    }

    /**
     * handle compile and package source
     */
    protected void handlePackage() {

        String msCode = "";
        List<String> msCodes = arguments.getOptionValues("msCode");

        List<String> srcDirs = arguments.getOptionValues("sourceDir");

        if (CollectionUtils.isEmpty(msCodes)) {
            printUsage();
            return;
        }
        msCode = msCodes.get(0);

        String srcDir = "";
        if (CollectionUtils.isEmpty(srcDirs)) {
            String absolutePath = new File("").getAbsolutePath();
            srcDir = absolutePath;
        } else {
            srcDir = srcDirs.get(0);
        }

        try {

            // compile

            Boolean compile = codeCompileService.compile(msCode, srcDir);

            // package
            codeCompileService.getJarFile(msCode);

            log.info("package success . file:{}", tacFileService.getClassFileOutputPath(msCode) + ".zip");

        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }

    }

    public static void printUsage() {

        System.out.println("useage:");
        System.out.println("--package --msCode=${msCode} --sourceDir=${sourceDir}");
        // System.out.println("--publish --msCode=${msCode} --zipFile=${zipFile}");
    }

}
