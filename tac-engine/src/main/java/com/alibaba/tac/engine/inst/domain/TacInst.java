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

package com.alibaba.tac.engine.inst.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * The tac intance class
 */
@Data
public class TacInst implements Serializable {

    private static final long serialVersionUID = -7830333085387154296L;

    public static final Integer STATUS_NEW = 0;

    public static final Integer STATUS_PRE_PUBLISH = 1;

    public static final Integer STATUS_PUBLISH = 2;


    /**
     * intanceId
     */
    private long id;

    /**
     * name
     */
    private String name;

    /**
     * the service code
     */
    private String msCode;

    /**
     *  data sign
     */
    private String jarVersion;

    /**
     * status
     */
    private Integer status;

    /**
     * prePublish sign
     */
    private String prePublishJarVersion;

    /**
     *  prePublish status
     */
    private Integer prePublishStatus;

    /**
     * gitBranch
     */
    private String gitBranch;

}
