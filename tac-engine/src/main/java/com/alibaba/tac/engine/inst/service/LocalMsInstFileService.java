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
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author jinshuan.li 12/02/2018 18:14     handle the inst file in container
 */
@Slf4j
@Service
public class LocalMsInstFileService implements IMsInstFileService {

    private static Logger LOGGER = LoggerFactory.getLogger(LocalMsInstFileService.class);

    @Resource
    private TacFileService tacFileService;

    @Override
    public byte[] getInstanceFile(long instId) {

        String zipFileName = tacFileService.getLoadClassFilePath(instId);

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

        // 文件存在删除

        try {
            Long instId = tacInst.getId();

            File zipOutFile = new File(tacFileService.getLoadClassFilePath(instId));

            if (zipOutFile.exists()) {
                boolean deleteResult = zipOutFile.delete();
                LOGGER.debug("TacInstanceLoader.loadTacHandler,instId:{} exists,delete result {}", instId,
                    deleteResult);
            } else {
                final String saveFileName = tacFileService.getLoadClassFilePath(instId);
                LOGGER.debug("TacInstanceLoader.loadTacHandler,saveFileName:{} ", saveFileName);
                Files.createParentDirs(new File(saveFileName));
                LOGGER.debug("TacInstanceLoader.loadTacHandler,createParentDirs success");
            }
            zipOutFile.createNewFile();
            LOGGER.debug("TacInstanceLoader.loadTacHandler,createNewFile success " + zipOutFile.getAbsolutePath());
            FileCopyUtils.copy(data, zipOutFile);
            LOGGER.debug("TacInstanceLoader.loadTacHandler,createNewFile copy success");

            return true;
        } catch (Exception e) {

            throw new IllegalStateException(e.getMessage(), e);
        }

    }
}
