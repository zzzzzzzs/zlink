package com.zlink.common.exception;

/**
 * @author zs
 * @date 2022/11/29
 */
public class MetaDataException extends RuntimeException {
    public MetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaDataException(String message) {
        super(message);
    }
}
