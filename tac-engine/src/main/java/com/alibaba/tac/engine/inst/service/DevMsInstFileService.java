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

package com.alibaba.tac.engine.inst.service;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.code.TacFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author jinshuan.li 27/02/2018 20:24  handle the inst file in dev client
 */
@Slf4j
@Service
public class DevMsInstFileService implements IMsInstFileService {
    @Override
    public byte[] getInstanceFile(long instId) {

        return getInstanceFile(String.valueOf(instId));
    }

    @Resource
    private TacFileService tacFileService;

    public byte[] getInstanceFile(String msCode){

        String zipFileName = tacFileService.getOutPutFilePath(msCode);

        final File zipFile = new File(zipFileName);

        byte[] zipFileBytes = null;
        if (!zipFile.exists()) {
            return zipFileBytes;
        }

        try {
            zipFileBytes = TacFileService.getFileBytes(zipFile);
        } catch (IOException e) {

            log.error(e.getMessage(), e);
            return null;
        }
        return zipFileBytes;
    }

    @Override
    public Boolean saveInstanceFile(TacInst tacInst, byte[] data) {
        throw new UnsupportedOperationException();
    }

    /**
     * get file data in filePath
     *
     * @param filePath
     * @return
     */
    public byte[] getInstanceFileData(String filePath) {

        byte[] zipFileBytes = null;
        try {
            final File zipFile = new File(filePath);
            zipFileBytes = TacFileService.getFileBytes(zipFile);
        } catch (IOException e) {

            log.error(e.getMessage(), e);
            return null;
        }
        return zipFileBytes;
    }

}
