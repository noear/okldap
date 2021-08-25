package org.noear.helloldap.exception;

/**
 * 无效的配置
 *
 * @author noear
 * @since 1.0
 */
public class IllegalConfigException extends RuntimeException {
    public IllegalConfigException() {
        super();
    }

    public IllegalConfigException(String message) {
        super(message);
    }
}
