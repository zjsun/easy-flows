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

    /**
     * Create a new {@link ParallelReport}.
     */
    public ParallelReport() {
        this(new ArrayList<>());
    }

    /**
     * Create a new {@link ParallelReport}.
     *
     * @param reports of works executed in parallel
     */
    public ParallelReport(List<Report> reports) {
        this.reports = reports;
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
        for (Report report : reports) {
            if (report.getStatus() == Status.FAILED || report.getStatus() == Status.WAITING) {
                return report.getStatus();
            }
        }
        return Status.COMPLETED;
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
        reports.forEach(report -> context.getValues().putAll(report.getContext().getValues()));
        return context;
    }
}
