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

import java.io.File;
import java.io.StringWriter;

/**
 * @author jinshuan.li 12/02/2018 08:10
 */
public interface IJdkCompiler {

    /**
     *compile code , the output name is instId
     * @param codeInfo
     * @param compileInfo
     * @return
     * @throws Exception
     */
    boolean compile(InstCodeInfo codeInfo, StringWriter compileInfo) throws Exception;

    /**
     * compileWithMsCode, the output name is msCode
     *
     *
     * @param codeInfo
     * @param compileInfo
     * @return
     * @throws Exception
     */
    boolean compileWithMsCode(InstCodeInfo codeInfo, StringWriter compileInfo) throws Exception;

    /**
     * add compile class path
     *
     * @param file
     */
    void addClassPath(File file);

}
