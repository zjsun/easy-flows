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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A predicate interface on work report.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
@FunctionalInterface
public interface ReportPredicate {

    /**
     * Apply the predicate on the given work report.
     * 
     * @param report on which the predicate should be applied
     * @return true if the predicate applies on the given report, false otherwise
     */
    boolean apply(Report report);

    ReportPredicate ALWAYS_TRUE = report -> true;
    ReportPredicate ALWAYS_FALSE = report -> false;
    ReportPredicate COMPLETED = report -> report.getStatus().equals(Status.COMPLETED);
    ReportPredicate FAILED = report -> report.getStatus().equals(Status.FAILED);
    ReportPredicate WAITING = report -> report.getStatus().equals(Status.WAITING);

    /**
     * A predicate that returns true after a given number of times.
     *
     * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
     */
    class TimesPredicate implements ReportPredicate {

        private final int times;

        private final AtomicInteger counter = new AtomicInteger();

        public TimesPredicate(int times) {
            this.times = times;
        }

        @Override
        public boolean apply(Report report) {
            return counter.incrementAndGet() != times;
        }

        public static TimesPredicate times(int times) {
            return new TimesPredicate(times);
        }
    }


}
