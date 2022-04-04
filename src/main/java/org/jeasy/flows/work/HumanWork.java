package org.jeasy.flows.work;

import org.jeasy.flows.flow.Context;

/**
 * @author Alex.Sun
 * @created 2022-04-04 17:50
 */
public class HumanWork extends AbstractWork {
    public HumanWork(String name) {
        super(name);
    }

    @Override
    protected Report executeInternal(Context context) {
        return new DefaultReport(Status.WAITING, context);
    }
}
