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
package org.jeasy.flows.work;

import org.jeasy.flows.flow.Context;

/**
 * Default implementation of {@link Report}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class DefaultReport implements Report {

    private final Status status;
    private final Context context;
    private Throwable error;

    /**
     * Create a new {@link DefaultReport}.
     *
     * @param status of work
     */
    public DefaultReport(Status status, Context context) {
        this.status = status;
        this.context = context;
    }

    /**
     * Create a new {@link DefaultReport}.
     *
     * @param status of work
     * @param error if any
     */
    public DefaultReport(Status status, Context context, Throwable error) {
        this(status, context);
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "DefaultWorkReport {" +
                "status=" + status +
                ", context=" + context +
                ", error=" + (error == null ? "''" : error) +
                '}';
    }
}
