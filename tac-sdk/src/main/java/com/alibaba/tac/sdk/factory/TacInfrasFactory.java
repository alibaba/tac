package com.alibaba.tac.sdk.factory;

import com.alibaba.tac.sdk.infrastracture.TacLogger;
import org.springframework.stereotype.Service;

/**
 * @author jinshuan.li 12/02/2018 19:04
 */
@Service
public class TacInfrasFactory extends AbstractServiceFactory {

    /**
     * The logger Service
     *
     * @return
     */
    public static TacLogger getLogger() {

        return getServiceBean(TacLogger.class);
    }
}
