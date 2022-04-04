package org.jeasy.flows.work;

import org.jeasy.flows.flow.Context;

/**
 * @author Alex.Sun
 * @created 2022-04-04 16:50
 */
public abstract class AbstractWork implements Work {

    private final String name;

    public AbstractWork(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public final Report execute(Context context) {
        if (context.getStates().get(getName()) == Status.COMPLETED) {
            return new DefaultReport(Status.COMPLETED, context);
        }

        Report report = executeInternal(context);
        context.getStates().put(getName(), report.getStatus());
        return report;
    }

    protected abstract Report executeInternal(Context context);
}
