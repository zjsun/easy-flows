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
package org.jeasy.flows.flow;

import org.jeasy.flows.work.Status;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Work execution context. This can be used to pass initial parameters to the
 * workflow and share data between work units.
 *
 * <strong>Work context instances are thread-safe.</strong>
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class Context {

    private final Map<String, Object> values = new ConcurrentHashMap<>();

    private final Map<String, Status> states = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Map<String, Status> getStates() {
        return states;
    }

    public boolean isCompleted() {
        return states.size() > 0 && states.values().stream().filter(status -> status == Status.COMPLETED).count() == states.size();
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Context.class.getSimpleName() + "[", "]")
                .add("values=" + values)
                .add("states=" + states)
                .toString();
    }
}
