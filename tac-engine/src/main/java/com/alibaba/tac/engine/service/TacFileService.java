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

package com.alibaba.tac.engine.service;

import com.alibaba.tac.engine.properties.TacDataPathProperties;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class TacFileService {

    @Resource
    private TacDataPathProperties tacDataPathProperties;

    public String getLoadClassFilePath(Long processID) {
        String path;
        String classloadPathPrefix = tacDataPathProperties.getClassLoadPathPrefix();

        if (!classloadPathPrefix.endsWith(File.separator)) {
            path = classloadPathPrefix + File.separator;
        } else {
            path = classloadPathPrefix;
        }
        path += processID + File.separator + processID + ".zip";
        return path;
    }

    public String getClassFileOutputPath(Long processID) {

        return getClassFileOutputPath(String.valueOf(processID));
    }

    public String getClassFileOutputPath(String suffix) {
        String path = "";

        String outputPathPrefix = tacDataPathProperties.getOutputPathPrefix();

        if (!outputPathPrefix.endsWith(File.separator)) {
            path = outputPathPrefix + File.separator;
        } else {
            path = outputPathPrefix;
        }
        path += suffix;
        return path;
    }

    public String getOutPutFilePath(Long processID) {

        return getClassFileOutputPath(processID) + ".zip";
    }

    public String getOutPutFilePath(String suffix) {

        return getClassFileOutputPath(suffix) + ".zip";
    }

    /**
     *listAllFiles
     *
     * @param directory
     * @return List
     */
    public static List<File> listAllFiles(File directory) {
        Collection<File> files = FileUtils.listFiles(directory, FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
        return Lists.newArrayList(files);
    }


    /**
     *
     *
     * @param file the file to delete
     * @throws IOException if an I/O error occurs
     * @see
     */
    public static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
            return ;
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

    /**
     * @param zipFile
     * @return
     * @throws IOException
     */
    public static byte[] getFileBytes(File zipFile) throws IOException {
        FileInputStream inputStream = null;
        inputStream = new FileInputStream(zipFile);
        byte[] zipFileBytes = FileCopyUtils.copyToByteArray(inputStream);

        return zipFileBytes;
    }

    /**
     * @param data
     * @return
     */
    public static String getMd5(byte[] data) {

        String md5 = Hashing.md5().hashBytes(data).toString();

        return md5;
    }
}
