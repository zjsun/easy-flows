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

import org.jeasy.flows.work.Report;
import org.jeasy.flows.work.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate report of the partial reports of work units executed in a parallel flow.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class ParallelReport implements Report {

    private final List<Report> reports;
    private final ParallelPolicy policy;

    /**
     * Create a new {@link ParallelReport}.
     *
     * @param parallelPolicy
     */
    public ParallelReport(ParallelPolicy parallelPolicy) {
        this(new ArrayList<>(), parallelPolicy);

    }

    /**
     * Create a new {@link ParallelReport}.
     *
     * @param reports        of works executed in parallel
     * @param parallelPolicy
     */
    public ParallelReport(List<Report> reports, ParallelPolicy parallelPolicy) {
        this.reports = reports;
        this.policy = parallelPolicy;
    }

    /**
     * Get partial reports.
     *
     * @return partial reports
     */
    public List<Report> getReports() {
        return reports;
    }

    void add(Report report) {
        reports.add(report);
    }

    void addAll(List<Report> reports) {
        this.reports.addAll(reports);
    }

    /**
     * Return the status of the parallel flow.
     * <p>
     * The status of a parallel flow is defined as follows:
     *
     * <ul>
     *     <li>{@link Status#COMPLETED}: If all work units have successfully completed</li>
     *     <li>{@link Status#FAILED}: If one of the work units has failed</li>
     * </ul>
     *
     * @return workflow status
     */
    @Override
    public Status getStatus() {
        switch (policy) {
            case OR:
                Report waiting = null, failed = null;
                for (Report report : reports) {
                    if (report.getStatus() == Status.COMPLETED) {
                        return report.getStatus();
                    } else if (report.getStatus() == Status.WAITING) {
                        waiting = report;
                    } else if (report.getStatus() == Status.FAILED) {
                        failed = report;
                    }
                }
                return waiting != null ? waiting.getStatus() : (failed != null ? failed.getStatus() : Status.COMPLETED);
            case AND:
            default:
                Report completed = null;
                for (Report report : reports) {
                    if (report.getStatus() == Status.FAILED || report.getStatus() == Status.WAITING) {
                        return report.getStatus();
                    } else {
                        completed = report;
                    }
                }
                return completed != null ? completed.getStatus() : Status.COMPLETED;
        }
    }

    /**
     * Return the first error of partial reports.
     *
     * @return the first error of partial reports.
     */
    @Override
    public Throwable getError() {
        for (Report report : reports) {
            Throwable error = report.getError();
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    /**
     * The parallel flow context is the union of all partial contexts. In a parallel
     * flow, each work unit should have its own unique keys to avoid key overriding
     * when merging partial contexts.
     *
     * @return the union of all partial contexts
     */
    @Override
    public Context getContext() {
        Context context = new Context();
        for (Report report : reports) {
            Context partialContext = report.getContext();
            for (String key : partialContext.valueKeys()) {
                context.setValue(key, partialContext.getValue(key));
            }
            for (String name : partialContext.statusNames()) {
                context.setStatus(name, partialContext.getStatus(name));
            }
        }
        return context;
    }
}
