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

package com.alibaba.tac.engine.bootlaucher;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.jar.JarFile;

import java.io.*;
import java.net.URI;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarEntry;

/**
 * @author jinshuan.li 08/03/2018 19:12
 *
 * The Boot Laucher Helper class,  unpack jar files in the boot jar file.  the files are used to compile new java code;
 */
public class BootJarLaucherUtils {

    private static File tempUnpackFolder;

    private static final int BUFFER_SIZE = 32 * 1024;

    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    /**
     *
     * unpack jar to temp folder
     * @param jarFile
     * @return
     */
    public static Integer unpackBootLibs(JarFile jarFile) throws IOException {

        Enumeration<JarEntry> entries = jarFile.entries();
        int count = 0;
        while (entries.hasMoreElements()) {

            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().startsWith(BOOT_INF_LIB) && jarEntry.getName().endsWith(".jar")) {
                getUnpackedNestedArchive(jarFile, jarEntry);
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param jarFile
     * @param jarEntry
     * @return
     * @throws IOException
     */
    private static Archive getUnpackedNestedArchive(JarFile jarFile, JarEntry jarEntry) throws IOException {
        String name = jarEntry.getName();
        if (name.lastIndexOf("/") != -1) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        File file = new File(getTempUnpackFolder(), name);
        if (!file.exists() || file.length() != jarEntry.getSize()) {
            unpack(jarFile, jarEntry, file);
        }
        return new JarFileArchive(file, file.toURI().toURL());
    }

    public static File getTempUnpackFolder() {
        if (tempUnpackFolder == null) {
            File tempFolder = new File(System.getProperty("java.io.tmpdir"));
            tempUnpackFolder = createUnpackFolder(tempFolder);
        }
        return tempUnpackFolder;
    }

    /**
     * create the unpack folder
     * @param parent
     * @return
     */
    private static File createUnpackFolder(File parent) {
        int attempts = 0;
        while (attempts++ < 1000) {
            String fileName = "com.alibaba.tac";
            File unpackFolder = new File(parent,
                fileName + "-spring-boot-libs");

            if (unpackFolder.exists()) {
                return unpackFolder;
            }
            if (unpackFolder.mkdirs()) {
                return unpackFolder;
            }
        }
        throw new IllegalStateException(
            "Failed to create unpack folder in directory '" + parent + "'");
    }

    /**
     *
     * @param jarFile
     * @param entry
     * @param file
     * @throws IOException
     */
    private static void unpack(JarFile jarFile, JarEntry entry, File file) throws IOException {
        InputStream inputStream = jarFile.getInputStream(entry, RandomAccessData.ResourceAccess.ONCE);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } finally {
                outputStream.close();
            }
        } finally {
            inputStream.close();
        }
    }

    /**
     * get the boot jar file
     *
     * @return  the boot jar file; null is run through folder
     * @throws Exception
     */
    public final static JarFile getBootJarFile() throws Exception {
        ProtectionDomain protectionDomain = BootJarLaucherUtils.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());

        String path = (location == null ? null : location.toURL().getPath());
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }

        if (path.lastIndexOf("!/BOOT-INF") <= 0) {
            return null;
        }
        path = path.substring(0, path.lastIndexOf("!/BOOT-INF"));

        path = StringUtils.replace(path, "file:", "");

        File root = new File(path);

        if (root.isDirectory()) {
            return null;
        }
        if (!root.exists()) {
            throw new IllegalStateException(
                "Unable to determine code source archive from " + root);
        }
        return new JarFile(root);
    }
}
