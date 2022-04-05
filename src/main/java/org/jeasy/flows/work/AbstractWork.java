package org.jeasy.flows.work;

import org.jeasy.flows.flow.Context;

/**
 * @author Alex.Sun
 * @created 2022-04-04 16:50
 */
public abstract class AbstractWork implements ExecutableWork {

    private final String name;

    public AbstractWork(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public final Report execute(Context context) {
        if (context.getStatus(getName()) == Status.COMPLETED) {
            return new DefaultReport(Status.COMPLETED, context);
        }

        Report report = executeInternal(context);
        if (report != null) {
            context.setStatus(getName(), report.getStatus());
        }
        return report;
    }

    protected abstract Report executeInternal(Context context);
}
