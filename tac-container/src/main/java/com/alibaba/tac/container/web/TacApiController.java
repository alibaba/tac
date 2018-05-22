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

package com.alibaba.tac.container.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.tac.engine.service.TacEngineService;
import com.alibaba.tac.sdk.common.TacParams;
import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author jinshuan.li 12/02/2018 20:27
 *
 * the online api when run tac-container
 */
@RestController
@RequestMapping("/api/tac")
@Slf4j
public class TacApiController {

    @Resource
    private TacEngineService tacEngineService;

    @GetMapping("/execute/{msCode}")
    public TacResult<Map<String, Object>> execute(@PathVariable String msCode,
                                                  @RequestParam(required = false) String paramMap) {

        try {
            TacParams tacParamsDO = new TacParams("tac", msCode);

            if (StringUtils.isEmpty(paramMap)) {

            } else {
                tacParamsDO.setParamMap(JSON.parseObject(paramMap));
            }

            return tacEngineService.execute(msCode, tacParamsDO);
        } catch (Exception e) {
            log.error("execute error msCode:{} tacParams:{} {}", msCode, paramMap, e.getMessage(), e);

            return TacResult.errorResult(msCode, String.valueOf(ErrorCode.SYS_EXCEPTION));
        }

    }

    @PostMapping("/execute/{msCode}")
    public TacResult<Map<String, Object>> executePost(@PathVariable String msCode,
                                                      @RequestBody Map<String, Object> paramMap) {

        try {
            TacParams tacParamsDO = new TacParams("tac", msCode);

            tacParamsDO.setParamMap(paramMap);

            return tacEngineService.execute(msCode, tacParamsDO);
        } catch (Exception e) {
            log.error("execute error msCode:{} tacParams:{} {}", msCode, paramMap, e.getMessage(), e);

            return TacResult.errorResult(msCode, String.valueOf(ErrorCode.SYS_EXCEPTION));
        }

    }
}
