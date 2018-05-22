package com.alibaba.tac.infrastracture.logger;

import com.alibaba.tac.sdk.common.TacContants;
import com.alibaba.tac.sdk.common.TacThreadLocals;
import com.alibaba.tac.sdk.infrastracture.TacLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * the logger service
 */
@Service
public class TacLoggerImpl implements TacLogger {

    protected static final Logger SYS_LOGGER = LoggerFactory.getLogger(TacLoggerImpl.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(TacLogConsts.TAC_USER_LOG);

    protected static final Logger TT_LOGGER = LoggerFactory.getLogger(TacLogConsts.TAC_BIZ_TT_LOG);

    private final String lineSeparator = System.getProperty("line.separator");

    private final String replacement = "##";

    private static String hostIp;

    private static String hostHost;

    /**
     * the console log lock
     */
    private ReentrantLock consoleLogLock = new ReentrantLock();

    /**
     *  default log rate
     */
    private volatile float logOutputRate = 1;

    @Override
    public void debug(String log) {
        appendToStringBuilder(log);
        log = StringUtils.replace(log, lineSeparator, replacement);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("^" + hostIp + "^" + log);
        }
    }

    @Override
    public void info(String log) {
        appendToStringBuilder(log);
        log = StringUtils.replace(log, lineSeparator, replacement);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("^" + hostIp + "^" + log);
        }
    }

    @Override
    public void rateInfo(String log) {
        if (new Random().nextInt(100) < (logOutputRate * 100)) {
            appendToStringBuilder(log);
            log = StringUtils.replace(log, lineSeparator, replacement);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("^" + hostIp + "^" + log);
            }
        }
    }

    @Override
    public void warn(String log) {
        appendToStringBuilder(log);
        log = StringUtils.replace(log, lineSeparator, replacement);
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("^" + hostIp + "^" + log);
        }
    }

    @Override
    public void rateWarn(String log) {
        if (new Random().nextInt(100) < (logOutputRate * 100)) {
            appendToStringBuilder(log);
            log = StringUtils.replace(log, lineSeparator, replacement);
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("^" + hostIp + "^" + log);
            }
        }
    }

    @Override
    public void error(String log, Throwable t) {
        log = StringUtils.replace(log, lineSeparator, replacement);
        StringWriter sw = new StringWriter();
        if (t != null) {
            t.printStackTrace(new PrintWriter(sw));
        }
        appendToStringBuilder(log, t, sw);
        String errorMsg = new StringBuilder(log).append(replacement)
            .append(StringUtils.replace(sw.toString(), lineSeparator, replacement)).toString();
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error("^" + hostIp + "^" + errorMsg);
        }

    }

    @Override
    public void ttLog(String log) {
        if (TT_LOGGER.isInfoEnabled()) {
            TT_LOGGER.info("^" + hostIp + "^" + log);
        }
    }

    @Override
    public String getContent() {
        StringBuilder content = TacThreadLocals.TAC_LOG_CONTENT.get();
        if (content != null) {
            return content.toString();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return LOGGER.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return LOGGER.isErrorEnabled();
    }

    private void appendToStringBuilder(String log) {

        if (!isDebugInvoke()) {
            return;
        }
        try {
            consoleLogLock.lock();
            StringBuilder content = TacThreadLocals.TAC_LOG_CONTENT.get();
            if (content == null) {
                content = new StringBuilder();
                TacThreadLocals.TAC_LOG_CONTENT.set(content);
            }

            content.append(log).append("\n");
        } finally {
            consoleLogLock.unlock();
        }

    }

    private Boolean isDebugInvoke() {
        Map<String, Object> params = TacThreadLocals.TAC_PARAMS.get();
        if (params == null) {
            return false;
        }
        String flag = String.valueOf(params.get(TacContants.DEBUG));
        if (StringUtils.equalsIgnoreCase("true", flag)) {
            return true;
        }
        return false;
    }

    private void appendToStringBuilder(String log, Throwable t, StringWriter sw) {

        if (!isDebugInvoke()) {
            return;
        }
        try {

            consoleLogLock.lock();
            StringBuilder content = TacThreadLocals.TAC_LOG_CONTENT.get();
            if (content == null) {
                content = new StringBuilder();
                TacThreadLocals.TAC_LOG_CONTENT.set(content);
            }

            content.append(log).append("\n");
            content.append(sw.toString()).append("\n");
        } finally {
            consoleLogLock.unlock();
        }

    }

    static {
        try {
            if (hostIp == null) {
                hostIp = Inet4Address.getLocalHost().getHostAddress();
            }

            if (hostHost == null) {
                hostHost = Inet4Address.getLocalHost().getHostName();
            }
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

    }
}