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
import com.alibaba.tac.engine.service.TacFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author jinshuan.li 12/02/2018 14:02
 *
 * the code loader service class, load  biz class through tac instance and jar file;
 */
@Slf4j
@Service
public class CodeLoadService {

    private CustomerClassLoader extendClassLoader = null;

    @PostConstruct
    public void init() {
        loadCustomerDirectory();
    }

    @Value("${tac.extend.lib:extendlibs}")
    private String extendlibs;

    @Resource
    private IJdkCompiler jdkCompiler;

    @Resource
    private TacFileService tacFileService;

    static {

    }

    /**
     *
     * @param environment
     */
    public static void changeClassLoader(Environment environment) {

        String extendlibs = environment.getProperty("tac.extend.lib", "extendlibs");
        try {
            // change class loader ,this allow us load class in extend jars
            ClassLoader classLoader = CodeLoadService.changeClassLoader(extendlibs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     * change class loader ,this allow us load class in extend jars
     * @param path
     * @return
     */
    public static ClassLoader changeClassLoader(String path) throws IllegalAccessException, NoSuchFieldException {

        try {

            File file = new File(path);
            if (!file.exists() || !file.isDirectory()) {
                return CodeLoadService.class.getClassLoader();
            }
            ClassLoader classLoader = getAppClassLoader();

            String[] list = file.list();
            if (list == null || list.length == 0) {
                return classLoader;
            }
            URL[] urls = new URL[list.length];
            int index = 0;
            String absolutePath = file.getAbsolutePath();
            for (String jarFile : list) {
                File item = new File(absolutePath + "/" + jarFile);
                URL url = item.toURI().toURL();
                urls[index] = url;
                index++;
            }

            // change classloader's parent
            log.info("find extendlibs . changing classloader....");

            Field parent = ClassLoader.class.getDeclaredField("parent");
            parent.setAccessible(true);

            SpringClassLoader extendClassLoader = new SpringClassLoader(urls, classLoader.getParent());
            parent.set(classLoader, extendClassLoader);

            return classLoader;
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return CodeLoadService.class.getClassLoader();
    }

    /**
     * get the AppClassLoader
     * @return
     */
    private static ClassLoader getAppClassLoader() {
        ClassLoader classLoader = CodeLoadService.class.getClassLoader();
        while (classLoader != null) {
            if (!StringUtils.containsIgnoreCase(classLoader.getClass().getName(), "AppClassLoader")) {
                classLoader = classLoader.getParent();
            } else {
                return classLoader;
            }
        }
        return null;
    }

    /**
     * change classloader
     *
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static ClassLoader changeClassLoader() throws NoSuchFieldException, IllegalAccessException {

        String extendlibs = "extendlibs";
        return changeClassLoader(extendlibs);
    }

    /**
     * loadCustomerDirectory  this allow the console compile java file which import extend classes;
     */
    private void loadCustomerDirectory() {

        try {

            File file = new File(extendlibs);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return StringUtils.endsWith(pathname.getName(), ".jar");
                }
            });
            if (files == null || files.length == 0) {
                return;
            }
            URL[] urls = new URL[files.length];
            int index = 0;

            for (File jarFile : files) {
                URL url = jarFile.toURI().toURL();
                urls[index] = url;
                jdkCompiler.addClassPath(jarFile);
                index++;
            }
            extendClassLoader = new CustomerClassLoader(urls, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * load class in intance file
     *
     * @param instId
     * @return
     * @throws Exception
     */
    public <T> Class<T> loadHandlerClass(Long instId, Class<T> interfaceClass) throws Exception {

        ZipFile zipFile;
        String zipFileName = tacFileService.getLoadClassFilePath(instId);
        File file = new File(zipFileName);

        ClassLoader parent = null;
        if (this.extendClassLoader == null) {
            parent = this.getClass().getClassLoader();
        } else {
            parent = this.extendClassLoader;
        }
        ClassLoader clazzLoader = new CustomerClassLoader(file, parent);
        zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entName = entry.getName();

            if (entry.isDirectory() || !entName.endsWith(".class")) {
                continue;
            }

            entName = entName.substring(0, entName.lastIndexOf('.'));

            String className = StringUtils.replaceChars(entName, '/', '.');

            Class<?> clazz = clazzLoader.loadClass(className);


            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            if (interfaceClass.isAssignableFrom(clazz)) {
                return (Class<T>)clazz;
            }
        }

        return null;
    }

}
