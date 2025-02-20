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
import org.jeasy.flows.work.Work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A parallel flow executes a set of work units in parallel. A {@link ParallelFlow}
 * requires a {@link ExecutorService} to execute work units in parallel using multiple
 * threads.
 *
 * <strong>It is the responsibility of the caller to manage the lifecycle of the
 * executor service.</strong>
 * <p>
 * The status of a parallel flow execution is defined as:
 *
 * <ul>
 *     <li>{@link Status#COMPLETED}: If all work units have successfully completed</li>
 *     <li>{@link Status#FAILED}: If one of the work units has failed</li>
 * </ul>
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class ParallelFlow extends AbstractFlow {

    private final List<Work> workUnits = new ArrayList<>();
    private final ParallelExecutor workExecutor;
    private final ParallelPolicy parallelPolicy;

    ParallelFlow(String name, List<Work> workUnits, ParallelExecutor parallelExecutor, ParallelPolicy parallelPolicy) {
        super(name);
        this.workUnits.addAll(workUnits);
        this.workExecutor = parallelExecutor;
        this.parallelPolicy = parallelPolicy;
    }

    @Override
    protected Report executeInternal(Context context) {
        ParallelReport parallelReport = new ParallelReport(parallelPolicy);
        List<Report> reports = workExecutor.executeInParallel(workUnits, context);
        parallelReport.addAll(reports);
        return parallelReport;
    }

    public static class Builder {

        private Builder() {
            // force usage of method aNewParallelFlow
        }

        public static NameStep aNewParallelFlow() {
            return new BuildSteps();
        }

        public interface NameStep extends ExecuteStep {
            ExecuteStep named(String name);
        }

        public interface ExecuteStep {
            WithStep execute(Work... workUnits);
        }

        public interface WithStep {
            /**
             * A {@link ParallelFlow} requires an {@link ExecutorService} to
             * execute work units in parallel using multiple threads.
             *
             * <strong>It is the responsibility of the caller to manage the lifecycle
             * of the executor service.</strong>
             *
             * @param executorService to use to execute work units in parallel
             * @return the builder instance
             */
            PolicyStep with(ExecutorService executorService);
        }

        public interface PolicyStep {
            BuildStep policy(ParallelPolicy policy);
        }

        public interface BuildStep {
            ParallelFlow build();
        }

        private static class BuildSteps implements NameStep, ExecuteStep, WithStep, BuildStep, PolicyStep {

            static final ExecutorService DEFAULT = Executors.newCachedThreadPool();
            static {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> DEFAULT.shutdown()));
            }

            private String name;
            private final List<Work> works;
            private ExecutorService executorService = DEFAULT;
            private ParallelPolicy policy = ParallelPolicy.AND;

            public BuildSteps() {
                this.name = UUID.randomUUID().toString();
                this.works = new ArrayList<>();
            }

            @Override
            public ExecuteStep named(String name) {
                this.name = name;
                return this;
            }

            @Override
            public WithStep execute(Work... workUnits) {
                this.works.addAll(Arrays.asList(workUnits));
                return this;
            }

            @Override
            public PolicyStep with(ExecutorService executorService) {
                this.executorService = executorService;
                return this;
            }

            @Override
            public BuildStep policy(ParallelPolicy policy) {
                this.policy = policy;
                return this;
            }

            @Override
            public ParallelFlow build() {
                return new ParallelFlow(
                        this.name, this.works,
                        new ParallelExecutor(this.executorService), this.policy);
            }
        }

    }
}
