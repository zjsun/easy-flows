package org.jeasy.flows.flow;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author Alex.Sun
 * @created 2022-04-04 20:54
 */
public class Instance {
    private final String id;
    private final String flow;
    private final Context context = new Context();

    public Instance(String flow) {
        this(UUID.randomUUID().toString(), flow);
    }

    public Instance(String id, String flow) {
        this.id = StringUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
        this.flow = flow;
    }

    public String getId() {
        return id;
    }

    public String getFlow() {
        return flow;
    }

    public Context getContext() {
        return context;
    }
}
