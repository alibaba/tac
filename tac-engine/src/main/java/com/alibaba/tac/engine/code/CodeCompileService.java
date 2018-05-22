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

package com.alibaba.tac.engine.code;

import com.alibaba.tac.engine.compile.IJdkCompiler;
import com.alibaba.tac.engine.compile.InstCodeInfo;
import com.alibaba.tac.engine.compile.JavaSourceCode;
import com.alibaba.tac.engine.service.TacFileService;
import com.alibaba.tac.engine.util.TacCompressUtils;
import com.alibaba.tac.sdk.error.ErrorCode;
import com.alibaba.tac.sdk.error.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jinshuan.li 12/02/2018 09:07
 */
@Service
public class CodeCompileService {

    private Logger LOGGER = LoggerFactory.getLogger(CodeCompileService.class);

    @Autowired
    private IJdkCompiler jdkCompiler;

    @Resource
    private TacFileService tacFileService;

    public static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*)\\s*;");

    public static final Pattern CLASS_PATTERN = Pattern.compile("(?<=\\n|\\A)(?:\\s*public\\s)?\\s*" +
        "(final\\s+class|final\\s+public\\s+class|" +
        "abstract\\s+class|abstract\\s+public\\s+class|" +
        "class|" +
        "abstract\\s+interface|abstract\\s+public\\s+interface|" +
        "interface|" + "@interface|" +
        "enum)\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)");

    /**
     * compile files
     * @param instId  the instanceId
     * @param sourceFileDicPath
     * @return
     * @throws ServiceException
     */
    public Boolean compile(Long instId, String sourceFileDicPath) throws ServiceException {

        File sourceFileDic = new File(sourceFileDicPath);
        List<File> sourceFile = TacFileService.listAllFiles(sourceFileDic);
        if (CollectionUtils.isEmpty(sourceFile)) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "there is no code");
        }
        InstCodeInfo codeInfo = getProcessCodeInfo(sourceFile);
        codeInfo.setInstId(instId);
        StringWriter compileInfo = new StringWriter();
        boolean compileResult = false;
        try {
            compileResult = jdkCompiler.compile(codeInfo, compileInfo);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "code compile fail: " + e.getMessage());
        }
        if (!compileResult) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "code compile fail: " + compileInfo.getBuffer().toString());
        }

        return compileResult;
    }

    /**
     * compile files
     *
     * @param msCode
     * @param sourceFileDicPath
     * @return
     * @throws ServiceException
     */
    public Boolean compile(String msCode, String sourceFileDicPath) throws ServiceException {

        File sourceFileDic = new File(sourceFileDicPath);
        List<File> sourceFile = TacFileService.listAllFiles(sourceFileDic);
        if (CollectionUtils.isEmpty(sourceFile)) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "there is no code");
        }
        InstCodeInfo codeInfo = getProcessCodeInfo(sourceFile);
        codeInfo.setInstId(0L);
        codeInfo.setName(msCode);
        StringWriter compileInfo = new StringWriter();
        boolean compileResult = false;
        try {
            compileResult = jdkCompiler.compileWithMsCode(codeInfo, compileInfo);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "code compile fail: " + e.getMessage());
        }
        if (!compileResult) {
            throw new ServiceException(ErrorCode.ILLEGAL_ARGUMENT, "code compile fail: " + compileInfo.getBuffer().toString());
        }

        return compileResult;
    }

    /**
     * get file info
     * @param codeFiles
     * @return
     */
    private InstCodeInfo getProcessCodeInfo(List<File> codeFiles) {
        InstCodeInfo processCodeInfo = new InstCodeInfo();
        if (CollectionUtils.isEmpty(codeFiles)) {
            return null;
        }
        for (File codeFile : codeFiles) {
            //filter the .java file
            if (codeFile == null || !StringUtils.endsWith(codeFile.getName(), ".java")) {
                continue;
            }
            FileInputStream in = null;
            try {
                in = new FileInputStream(codeFile);
                byte[] buffer = new byte[(int)codeFile.length()];
                if (in.read(buffer) > 0) {
                    String content = new String(buffer, "UTF-8");
                    JavaSourceCode unit = getCodeInfo(content);
                    processCodeInfo.getJavaSourceCodes().add(unit);
                }
            } catch (Exception e) {
                LOGGER.error("read File Error!files = " + codeFiles, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LOGGER.error("Close FileInputStream Failed! files=" + codeFiles);
                    }
                }
            }
        }
        return processCodeInfo;
    }

    /**
     * get class info through code
     * @param content
     * @return
     */
    private JavaSourceCode getCodeInfo(String content) {
        JavaSourceCode unit = new JavaSourceCode();
        // the source
        unit.setSource(content);
        // class name and file name
        Matcher matcher = CLASS_PATTERN.matcher(content);
        if (matcher.find()) {
            unit.setClassName(matcher.group(2));
            unit.setFileName(matcher.group(2) + ".java");
        }
        // package name
        matcher = PACKAGE_PATTERN.matcher(content);
        if (matcher.find()) {
            unit.setPackageName(matcher.group(1));
        }
        return unit;
    }

    public byte[] getJarFile(Long instId) throws IOException {

        return getJarFile(String.valueOf(instId));
    }

    /**
     * get the packaged jar file data
     * @param msCode
     * @return
     * @throws IOException
     */
    public byte[] getJarFile(String msCode) throws IOException {
        FileInputStream inputStream = null;
        byte[] zipFileBytes = null;
        try {
            // classse  directory
            String zipDirectory = tacFileService.getClassFileOutputPath(msCode);
            String zipOutputPathName = zipDirectory + ".zip";

            // the output zip
            File outputFile = new File(zipOutputPathName);
            if (outputFile.exists()) {
                TacFileService.deleteRecursively(outputFile);
            }

            TacCompressUtils.compress(zipDirectory + File.separator + "com", zipOutputPathName);
            final File zipFile = new File(zipOutputPathName);

            if (!zipFile.exists()) {
                return zipFileBytes;
            }
            inputStream = new FileInputStream(zipFile);
            zipFileBytes = FileCopyUtils.copyToByteArray(inputStream);

        } catch (Exception e) {
            return zipFileBytes;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        if (zipFileBytes == null) {
            return zipFileBytes;
        }
        return zipFileBytes;
    }
}
