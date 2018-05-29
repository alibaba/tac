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

package com.alibaba.tac.console.web;

import com.alibaba.tac.console.error.ConsoleError;
import com.alibaba.tac.console.web.ro.InstTestRO;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.IMsInstFileService;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.service.IMsPublisher;
import com.alibaba.tac.engine.ms.service.IMsService;
import com.alibaba.tac.engine.service.TacPublishTestService;
import com.alibaba.tac.engine.code.TacFileService;
import com.alibaba.tac.sdk.common.TacResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jinshuan.li 05/03/2018 20:14
 */

@Slf4j
@RestController
@RequestMapping("/api/inst")
public class TacInstController {

    @Resource
    private IMsService msService;

    @Resource
    private IMsInstService msInstService;

    @Resource
    private IMsPublisher msPublisher;

    @Resource
    private TacPublishTestService tacPublishTestService;

    @Resource(name = "prePublishMsInstFileService")
    private IMsInstFileService prePublishMsInstFileService;

    @PostMapping(value = "/uploadFile")
    public TacResult<List<TacMsDO>> uploadFile(@RequestParam("file") MultipartFile instFileRO,
                                               @RequestParam("msCode") String msCode) {

        return TacResult.newResult(null);

    }

    @GetMapping(value = "/info/{msCode}")
    public TacResult<TacInst> getMsInst(@PathVariable("msCode") String msCode) {

        try {
            TacMsDO ms = msService.getMs(msCode);
            if (ms == null) {
                throw new IllegalArgumentException("the service is not exist");
            }

            TacInst tacInst = this.getExistTacInst(ms, "");

            return TacResult.newResult(tacInst);

        } catch (Exception e) {
            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    @PostMapping("/create")
    public TacResult<TacMsDO> create(@RequestBody TacInst tacInst) {

        String msCode = tacInst.getMsCode();
        try {
            if (StringUtils.isEmpty(msCode)) {
                throw new IllegalArgumentException("invalid params");
            }
            TacMsDO ms = msService.getMs(msCode);
            if (ms == null) {
                throw new IllegalStateException("the service with code " + msCode + " not exist");
            }

            String name = tacInst.getName();
            String gitBranch = tacInst.getGitBranch();

            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(gitBranch)) {
                throw new IllegalArgumentException("invalid params");
            }
            msInstService.createGitTacMsInst(msCode, name, gitBranch);

            return TacResult.newResult(ms);
        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    @PostMapping("/update")
    public TacResult<TacMsDO> update(@RequestBody TacInst tacInst) {

        String msCode = tacInst.getMsCode();

        long instId = tacInst.getId();

        try {
            if (StringUtils.isEmpty(msCode)) {
                throw new IllegalArgumentException("invalid params");
            }
            TacMsDO ms = msService.getMs(msCode);
            if (ms == null) {
                throw new IllegalStateException("the service with code " + msCode + " not exist");
            }

            TacInst tacMsInst = msInstService.getTacMsInst(instId);

            if (tacMsInst == null) {
                throw new IllegalStateException("inst not exist");
            }

            String name = tacInst.getName();
            String gitBranch = tacInst.getGitBranch();

            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(gitBranch)) {
                throw new IllegalArgumentException("invalid params");
            }

            tacMsInst.setGitBranch(gitBranch);
            tacMsInst.setName(name);

            msInstService.updateTacMsInst(instId, tacMsInst);

            return TacResult.newResult(ms);
        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    @GetMapping(value = "/list/{msCode}")
    public TacResult<List<TacInst>> getMsInstList(@PathVariable("msCode") String msCode) {

        try {
            TacMsDO ms = msService.getMs(msCode);
            if (ms == null) {
                throw new IllegalArgumentException("the service is not exist");
            }

            List<TacInst> msInsts = msInstService.getMsInsts(msCode);
            Optional.ofNullable(msInsts).ifPresent(items -> {

                items.stream().forEach(d -> {
                    if (d.getStatus() == null) {
                        d.setStatus(TacInst.STATUS_NEW);
                    }
                });
            });
            return TacResult.newResult(msInsts);

        } catch (Exception e) {
            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    private TacInst getExistTacInst(TacMsDO ms, String jarVersion) {

        String msCode = ms.getCode();

        Long publishedInstId = ms.getPublishedInstId();
        TacInst tacMsInst = null;
        if (publishedInstId == null || publishedInstId.equals(0)) {
            tacMsInst = msInstService.createTacMsInst(msCode, ms.getName(), jarVersion);
            // update service data
            ms.setPublishedInstId(tacMsInst.getId());

            msService.updateMs(msCode, ms);

            publishedInstId = ms.getPublishedInstId();
        }
        tacMsInst = msInstService.getTacMsInst(publishedInstId);

        if (tacMsInst == null) {
            throw new IllegalStateException("can't find the instance " + publishedInstId);
        }
        return tacMsInst;
    }

    @PostMapping(value = "/prePublish")
    public TacResult<TacInst> prePublish(@RequestParam("file") MultipartFile instFileRO,
                                         @RequestParam("msCode") String msCode,
                                         @RequestParam(value = "instId", required = false) Long instId) {

        try {
            byte[] bytes = instFileRO.getBytes();
            String md5 = TacFileService.getMd5(bytes);

            TacMsDO ms = msService.getMs(msCode);
            if (ms == null) {
                throw new IllegalArgumentException("the service is not exist");
            }

            TacInst tacMsInst = this.getExistTacInst(ms, md5);

            // prepublish
            msPublisher.prePublish(tacMsInst, bytes);

            return TacResult.newResult(msInstService.getTacMsInst(tacMsInst.getId()));

        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    @PostMapping(value = "/publish")
    public TacResult<TacInst> publish(@RequestParam("msCode") String msCode,
                                      @RequestParam(value = "instId") Long instId) {

        try {

            TacInst tacMsInst = msInstService.getTacMsInst(instId);
            if (tacMsInst == null) {
                throw new IllegalArgumentException("the instance is not exist " + instId);
            }

            if (!StringUtils.equalsIgnoreCase(tacMsInst.getMsCode(), msCode)) {
                throw new IllegalArgumentException("the msCode is not match");
            }

            // 取预发布的数据
            byte[] instanceFile = prePublishMsInstFileService.getInstanceFile(instId);
            if (instanceFile == null) {
                throw new IllegalStateException("can't find prePublish data");
            }
            // 正式发布
            msPublisher.publish(tacMsInst, instanceFile);

        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        return TacResult.newResult(null);

    }

    @PostMapping(value = "/preTest")
    public TacResult<Object> preTest(@RequestBody InstTestRO instTestRO) {

        Long instId = instTestRO.getInstId();

        Map<String, Object> params = instTestRO.getParams();

        try {
            TacResult<Object> result = tacPublishTestService.prePublishTest(instId, instTestRO.getMsCode(),
                params);
            TacResult<Object> response = new TacResult<>(result);
            return response;

        } catch (Exception e) {

            log.info("preTest error.  {} {}", instTestRO, e.getMessage(), e);

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());

        }

    }

    @PostMapping(value = "/onlineTest")
    public TacResult<?> onlineTest(@RequestBody InstTestRO instTestRO) {

        Long instId = instTestRO.getInstId();

        Map<String, Object> params = instTestRO.getParams();

        try {
            TacResult<?> result = tacPublishTestService.onlinePublishTest(instId, instTestRO.getMsCode(), params);
            return result;

        } catch (Exception e) {

            log.info("preTest error.  {} {}", instTestRO, e.getMessage(), e);

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());

        }

    }

    @GetMapping(value = "/gitPrePublish")
    public TacResult<TacInst> prePublish(@RequestParam(value = "instId") Long instId) {

        try {

            TacInst tacMsInst = msInstService.getTacMsInst(instId);

            if (tacMsInst == null) {
                throw new IllegalArgumentException("inst not exist");
            }

            TacMsDO ms = msService.getMs(tacMsInst.getMsCode());

            if (ms == null) {
                throw new IllegalArgumentException("ms not eist");
            }

            tacMsInst = msPublisher.gitPrePublish(ms, tacMsInst);

            return TacResult.newResult(tacMsInst);

        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

}
