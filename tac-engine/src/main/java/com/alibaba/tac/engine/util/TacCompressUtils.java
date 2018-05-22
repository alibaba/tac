package com.alibaba.tac.engine.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TacCompressUtils {

    public static final String EXT = ".zip";
    private static final String BASE_DIR = "";

    // 符号"/"用来作为目录标识判断符
    private static final String PATH = "/";
    private static final int BUFFER = 1024;

    /**
     * compress
     *
     * @param srcFile
     * @param destFile
     * @throws Exception
     */
    public static void compress(File srcFile, File destFile) throws Exception {

        // CRC32 check
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
            destFile), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);
        zos.setComment(new String("comment"));
        compress(srcFile, zos, BASE_DIR);

        zos.flush();
        zos.close();
    }

    /**
     * compress
     *
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public static void compress(File srcFile, String destPath) throws Exception {
        compress(srcFile, new File(destPath));
    }

    /**
     * compress
     *
     * @param srcFile
     * @param zos      ZipOutputStream
     * @param basePath
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos,
                                 String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    /**
     * compress
     *
     * @param srcPath
     * @param destPath
     */
    public static void compress(String srcPath, String destPath)
        throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile, destPath);
    }

    /**
     * compressDir
     *
     * @param dir
     * @param zos      outPutStream
     * @param basePath
     * @throws Exception
     */
    private static void compressDir(File dir, ZipOutputStream zos,
                                    String basePath) throws Exception {

        File[] files = dir.listFiles();
        // create empty directory
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
            zos.putNextEntry(entry);
            zos.closeEntry();
        }
        for (File file : files) {

            compress(file, zos, basePath + dir.getName() + PATH);
        }

    }

    /**
     * compress file
     *
     * @param file
     * @param zos  ZipOutputStream
     * @param dir
     * @throws Exception
     */
    private static void compressFile(File file, ZipOutputStream zos, String dir)
        throws Exception {

        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
            file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        bis.close();

        zos.closeEntry();
    }
}