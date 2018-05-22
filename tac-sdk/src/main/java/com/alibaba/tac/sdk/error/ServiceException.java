package com.alibaba.tac.sdk.error;

/**
 * the service exception class
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1701785539191674857L;
    /**
     * errorCode
     */
    private int errorCode;
    /**
     * errorMessage
     */
    private String message;

    public ServiceException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * the helper method which throw an exception
     *
     * @param error
     * @return
     * @throws ServiceException
     */
    public static ServiceException throwException(IError error) throws ServiceException {

        throw new ServiceException(error.getCode(), error.getMessage());
    }

    /**
     *
     *
     * @param errorCode
     * @param errorMessage
     * @return
     * @throws ServiceException
     */
    public static ServiceException throwException(Integer errorCode, String errorMessage) throws ServiceException {

        throw new ServiceException(errorCode, errorMessage);
    }

    /**
     *
     *
     * @param error
     * @param errorMessage
     * @return
     * @throws ServiceException
     */
    public static ServiceException throwException(IError error, String errorMessage) throws ServiceException {

        throw new ServiceException(error.getCode(), errorMessage);
    }

    /**
     *
     *
     * @param errorCode
     * @param errorMessage
     * @return
     */
    public static ServiceException newException(Integer errorCode, String errorMessage) {

        return new ServiceException(errorCode, errorMessage);
    }
}
