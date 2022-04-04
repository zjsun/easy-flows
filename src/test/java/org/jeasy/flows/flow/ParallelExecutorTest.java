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

import org.assertj.core.api.Assertions;
import org.jeasy.flows.work.DefaultReport;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.Report;
import org.jeasy.flows.work.Status;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelExecutorTest {

    @Test
    public void testExecute() {

        // given
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        HelloWorldWork work1 = new HelloWorldWork("work1", Status.COMPLETED);
        HelloWorldWork work2 = new HelloWorldWork("work2", Status.FAILED);
        Context context = Mockito.mock(Context.class);
        ParallelExecutor parallelExecutor = new ParallelExecutor(executorService);

        // when
        List<Report> reports = parallelExecutor.executeInParallel(Arrays.asList(work1, work2), context);
        executorService.shutdown();

        // then
        Assertions.assertThat(reports).hasSize(2);
        Assertions.assertThat(work1.isExecuted()).isTrue();
        Assertions.assertThat(work2.isExecuted()).isTrue();
    }

    static class HelloWorldWork implements Work {

        private final String name;
        private final Status status;
        private boolean executed;

        HelloWorldWork(String name, Status status) {
            this.name = name;
            this.status = status;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Report execute(Context context) {
            executed = true;
            return new DefaultReport(status, context);
        }

        public boolean isExecuted() {
            return executed;
        }
    }

}
