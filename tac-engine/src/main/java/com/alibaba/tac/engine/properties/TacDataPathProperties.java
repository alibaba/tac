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

package com.alibaba.tac.engine.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author jinshuan.li 12/02/2018 16:45
 */
@Component
@ConfigurationProperties(prefix = "tac.data.path")
public class TacDataPathProperties {

    private String sourcePathPrefix;

    @Value("${user.home}/tac/data/classes")
    private String outputPathPrefix;
    @Value("${user.home}/tac/data/ms")
    private String classLoadPathPrefix;

    private String pkgPrefix="com.alibaba.tac.biz";

    public void setSourcePathPrefix(String sourcePathPrefix) {
        this.sourcePathPrefix = sourcePathPrefix;
    }

    public String getSourcePathPrefix() {
        return sourcePathPrefix;
    }

    public String getOutputPathPrefix() {
        return outputPathPrefix;
    }

    public void setOutputPathPrefix(String outputPathPrefix) {
        this.outputPathPrefix = outputPathPrefix;
    }

    public String getClassLoadPathPrefix() {
        return classLoadPathPrefix;
    }

    public void setClassLoadPathPrefix(String classLoadPathPrefix) {
        this.classLoadPathPrefix = classLoadPathPrefix;
    }

    public String getPkgPrefix() {
        return pkgPrefix;
    }

    public void setPkgPrefix(String pkgPrefix) {
        this.pkgPrefix = pkgPrefix;
    }
}
