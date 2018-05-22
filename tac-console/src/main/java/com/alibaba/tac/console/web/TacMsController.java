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
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.alibaba.tac.engine.ms.service.IMsService;
import com.alibaba.tac.sdk.common.TacResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jinshuan.li 05/03/2018 19:30
 */
@RestController
@RequestMapping("/api/ms")
public class TacMsController {

    @Resource
    private IMsService msService;

    @GetMapping("/list")
    public TacResult<List<TacMsDO>> list() {

        List<TacMsDO> allMs = msService.getAllMs();
        return TacResult.newResult(allMs);

    }

    @PostMapping("/create")
    public TacResult<TacMsDO> create(@RequestBody TacMsDO tacMsDO) {

        try {
            TacMsDO ms = msService.getMs(tacMsDO.getCode());
            if (ms != null) {
                throw new IllegalStateException("the service with code " + tacMsDO.getCode() + " already exist");
            }
            ms = msService.createMs(tacMsDO);
            return TacResult.newResult(ms);
        } catch (Exception e) {

            return TacResult.errorResult(ConsoleError.SYSTEM_ERROR.getCode(), e.getMessage());
        }

    }

    @PostMapping("/update")
    public TacResult<TacMsDO> update(@RequestBody TacMsDO tacMsDO) {

        try {
            msService.checkMsDO(tacMsDO);

            TacMsDO ms = msService.getMs(tacMsDO.getCode());

            if (ms == null) {
                throw new IllegalStateException("该服务不存在");
            }
            ms.setName(tacMsDO.getName());
            ms.setGitSupport(tacMsDO.getGitSupport());
            ms.setGitRepo(tacMsDO.getGitRepo());
            msService.updateMs(tacMsDO.getCode(), ms);

            return TacResult.newResult(tacMsDO);

        } catch (Exception e) {
            return TacResult.errorResult("system", e.getMessage());
        }

    }

    @PostMapping("/offline")
    public TacResult<Boolean> offline(@RequestParam("msCode") String msCode) {

        throw new UnsupportedOperationException("不支持该功能");

    }
}
