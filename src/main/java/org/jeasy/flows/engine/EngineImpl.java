/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.flows.engine;

import org.jeasy.flows.flow.Context;
import org.jeasy.flows.work.Report;
import org.jeasy.flows.flow.Flow;
import org.jeasy.flows.work.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class EngineImpl implements Engine {

    private static final Logger logger = LoggerFactory.getLogger(EngineImpl.class);

    private Map<String, Flow> flows = new ConcurrentHashMap<>();
    private Map<String, Context> contexts = new ConcurrentHashMap<>();

    @Override
    public void add(Flow flow) {
        flows.put(flow.getName(), flow);
    }

    @Override
    public Flow remove(String name) {
        return flows.remove(name);
    }

    @Override
    public Report run(Flow flow, Context context) {
        logger.info("Running workflow ''{}''", flow.getName());
        return flow.execute(context);
    }

    @Override
    public Report notify(String name, String workName, Status status) {
        return null;
    }


}
