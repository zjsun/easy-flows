package org.jeasy.flows.work;

import org.jeasy.flows.flow.Context;

/**
 * @author Alex.Sun
 * @created 2022-04-04 20:29
 */
public interface Executable {
    /**
     * Execute the unit of work and return its report. Implementations are required
     * to catch any checked or unchecked exceptions and return a {@link Report} instance
     * with a status of {@link Status#FAILED} and a reference to the exception.
     *
     * @param context context in which this unit of work is being executed
     * @return the execution report
     */
    Report execute(Context context);
}
