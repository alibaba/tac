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

import java.util.List;

/**
 * @author jinshuan.li 12/02/2018 17:14
 */
public interface IMsInstService {

    /**
     * get all tac instance
     *
     * @return
     */
    List<TacInst> getAllTacMsInsts();

    /**
     * get single instance
     *
     * @param msInstId
     * @return
     */
    TacInst getTacMsInst(Long msInstId);

    /**
     * create instance
     *
     * @param tacInst
     * @return
     */
    TacInst createTacMsInst(TacInst tacInst);

    /**
     * update instance
     *
     * @param instId
     * @param tacInst
     * @return
     */
    Boolean updateTacMsInst(Long instId, TacInst tacInst);

    /**
     * remove instance
     *
     * @param instId
     * @return
     */
    Boolean removeMsInst(Long instId);

    /**
     * getMsInsts
     * @param code
     * @return
     */
    List<TacInst> getMsInsts(String code);

    /**
     * create default instance
     *
     * @param msCode
     * @param name
     * @param jarVersion
     * @return
     */
    default TacInst createTacMsInst(String msCode, String name, String jarVersion) {
        TacInst tacInst = new TacInst();
        tacInst.setJarVersion(jarVersion);
        tacInst.setMsCode(msCode);
        tacInst.setName(msCode);
        return this.createTacMsInst(tacInst);
    }

    /**
     * create git instance
     *
     * @param msCode
     * @param name
     * @param branch
     * @return
     */
    default TacInst createGitTacMsInst(String msCode, String name, String branch) {

        TacInst tacInst = new TacInst();
        tacInst.setGitBranch(branch);
        tacInst.setMsCode(msCode);
        tacInst.setName(name);
        return this.createTacMsInst(tacInst);
    }

}
