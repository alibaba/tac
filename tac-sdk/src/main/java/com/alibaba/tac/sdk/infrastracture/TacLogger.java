package com.alibaba.tac.sdk.infrastracture;


/**
 * tac log
 *
 */
public interface TacLogger {

    public void debug(String log);

    public void info(String log);
    
    public void rateInfo(String log);

    public void warn(String log);
    
    public void rateWarn(String log);

    public void error(String log, Throwable t);
    
    public void ttLog(String log);

    public String getContent();


    boolean isDebugEnabled();


    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

}
