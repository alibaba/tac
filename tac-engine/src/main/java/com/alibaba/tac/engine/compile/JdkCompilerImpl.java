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

package com.alibaba.tac.engine.compile;

import com.alibaba.tac.engine.bootlaucher.BootJarLaucherUtils;
import com.alibaba.tac.engine.properties.TacDataPathProperties;
import com.alibaba.tac.engine.code.TacFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JdkCompilerImpl implements IJdkCompiler, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(JdkCompilerImpl.class);

    private static final List<String> OPTIONS = new ArrayList<String>();

    private static final List<File> CLASSPATH = new ArrayList<File>();

    static {
        OPTIONS.add("-target");
        OPTIONS.add("1.8");
    }

    @Resource
    private TacDataPathProperties tacDataPathProperties;

    @Resource
    private TacFileService tacFileService;

    public String sourcePathPrefix;
    public String outputPathPrefix;
    public String classLoadPathPrefix;
    public String pkgPrefix;

    @PostConstruct
    public void init() {

        this.sourcePathPrefix = tacDataPathProperties.getSourcePathPrefix();
        this.outputPathPrefix = tacDataPathProperties.getOutputPathPrefix();
        this.classLoadPathPrefix = tacDataPathProperties.getClassLoadPathPrefix();
        this.pkgPrefix = tacDataPathProperties.getPkgPrefix();
    }

    @Override
    public synchronized boolean compile(InstCodeInfo codeInfo, StringWriter compileInfo) throws Exception {

        return this.compile(codeInfo, compileInfo, String.valueOf(codeInfo.getInstId()));
    }

    @Override
    public synchronized boolean compileWithMsCode(InstCodeInfo codeInfo, StringWriter compileInfo) throws Exception {

        return this.compile(codeInfo, compileInfo, codeInfo.getName());
    }

    @Override
    public void addClassPath(File file) {
        CLASSPATH.add(file);
        LOG.debug("add compile class path:{}", file.getAbsolutePath());
    }

    private boolean compile(InstCodeInfo codeInfo, StringWriter compileInfo, String outputName) throws Exception {
        long start = 0;
        if (LOG.isInfoEnabled()) {
            start = System.currentTimeMillis();
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        File[] outputs = new File[] {new File(tacFileService.getClassFileOutputPath(outputName))};
        for (File file : outputs) {
            if (!file.exists()) {
                file.mkdirs();
            } else {
                TacFileService.deleteRecursively(file);
                file.mkdirs();
            }
        }

        LOG.debug("compile classpath.  size:{}  CLASS-PATH:{}", CLASSPATH.size(), CLASSPATH);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputs));
        fileManager.setLocation(StandardLocation.CLASS_PATH, CLASSPATH);
        List<TacJavaFileObject> fileObjects = new ArrayList<TacJavaFileObject>();
        for (JavaSourceCode unit : codeInfo.getJavaSourceCodes()) {
            TacJavaFileObject sourceObject = new TacJavaFileObject(unit.getFullClassName(), unit.getSource());
            fileObjects.add(sourceObject);
        }
        CompilationTask task = compiler.getTask(compileInfo, fileManager, null, OPTIONS, null, fileObjects);
        Boolean resultSuccess = task.call();
        if (resultSuccess == null || !resultSuccess) {
            return false;
        }
        fileManager.close();
        if (LOG.isInfoEnabled()) {
            LOG.info("compile complete . name :{}  instClassName:{} cost: {} resultSucess:{} ", codeInfo.getName(),
                codeInfo.getInstClassName(), (System.currentTimeMillis() - start), resultSuccess);
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initTacDict();

        LOG.debug("init class path:\n");
        ClassLoader classLoader = this.getClass().getClassLoader();
        while (classLoader != null) {
            this.addClassLoaderClassPath(classLoader);
            classLoader = classLoader.getParent();
        }

        addBootLibJars();
    }

    /**
     * add boot lib jars to the compile classpath
     */
    private void addBootLibJars() {

        File tempUnpackFolder = BootJarLaucherUtils.getTempUnpackFolder();
        if (tempUnpackFolder != null && tempUnpackFolder.isDirectory() && tempUnpackFolder.exists()) {

            File[] files = tempUnpackFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return StringUtils.endsWith(pathname.getName(), ".jar");
                }
            });
            for (File file : files) {
                CLASSPATH.add(file);
                LOG.debug("add compile class path:{} ", file.getPath());
            }
        }
    }

    /**
     * @param classLoader
     */
    private void addClassLoaderClassPath(ClassLoader classLoader) {
        ClassLoader loader = classLoader;

        if (loader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader)loader;
            URL[] urls = urlClassLoader.getURLs();
            for (URL url : urls) {
                File file = new File(url.getFile());
                if (file.exists()) {
                    CLASSPATH.add(file);
                    LOG.debug("add compile class path:{} ", file.getPath());
                }

            }
        } else {
            LOG.error("need URLClassLoader!!");
        }
    }

    private void initTacDict() {


        // class compile output directory


        File outputPathDic = new File(this.outputPathPrefix);

        if (!outputPathDic.exists()) {
            outputPathDic.mkdirs();
        }
        /**
         * class load direcotry
         */
        File classLoadDic = new File(this.classLoadPathPrefix);
        if (!classLoadDic.exists()) {
            classLoadDic.mkdirs();
        }
    }
}
