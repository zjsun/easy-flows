package org.jeasy.flows.flow;

/**
 * @author Alex.Sun
 * @created 2022-04-04 20:50
 */
public class FlowException extends RuntimeException {
    public FlowException(String message) {
        super(message);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
