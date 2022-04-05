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

import org.jeasy.flows.work.Executable;
import org.jeasy.flows.work.NoOpWork;
import org.jeasy.flows.work.Report;
import org.jeasy.flows.work.ReportPredicate;
import org.jeasy.flows.work.Status;
import org.jeasy.flows.work.Work;

import java.util.UUID;

/**
 * A repeat flow executes a work repeatedly until its report satisfies a given predicate.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class RepeatFlow extends AbstractFlow {

    private final Work work;
    private final ReportPredicate predicate;

    RepeatFlow(String name, Work work, ReportPredicate predicate) {
        super(name);
        this.work = work;
        this.predicate = predicate;
    }

    @Override
    protected Report executeInternal(Context context) {
        Report report;
        do {
            report = ((Executable)work).execute(context);
            if (report != null && report.getStatus() == Status.WAITING) break;
        } while (predicate.apply(report));
        return report;
    }

    public static class Builder {

        private Builder() {
            // force usage of static method aNewRepeatFlow
        }

        public static NameStep aNewRepeatFlow() {
            return new BuildSteps();
        }

        public interface NameStep extends RepeatStep {
            RepeatStep named(String name);
        }

        public interface RepeatStep {
            UntilStep repeat(Work work);
        }

        public interface UntilStep {
            BuildStep until(ReportPredicate predicate);

            BuildStep times(int times);
        }

        public interface BuildStep {
            RepeatFlow build();
        }

        private static class BuildSteps implements NameStep, RepeatStep, UntilStep, BuildStep {

            private String name;
            private Work work;
            private ReportPredicate predicate;

            BuildSteps() {
                this.name = UUID.randomUUID().toString();
                this.work = new NoOpWork();
                this.predicate = ReportPredicate.ALWAYS_FALSE;
            }

            @Override
            public RepeatStep named(String name) {
                this.name = name;
                return this;
            }

            @Override
            public UntilStep repeat(Work work) {
                this.work = work;
                return this;
            }

            @Override
            public BuildStep until(ReportPredicate predicate) {
                this.predicate = predicate;
                return this;
            }

            @Override
            public BuildStep times(int times) {
                until(ReportPredicate.TimesPredicate.times(times));
                return this;
            }

            @Override
            public RepeatFlow build() {
                return new RepeatFlow(name, work, predicate);
            }
        }

    }
}
