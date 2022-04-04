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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jeasy.flows.flow.Flow;
import org.jeasy.flows.work.DefaultReport;
import org.jeasy.flows.work.Report;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.flow.Context;
import org.jeasy.flows.work.Status;
import org.jeasy.flows.flow.*;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.flows.engine.EngineBuilder.aNewEngine;
import static org.jeasy.flows.work.ReportPredicate.COMPLETED;
import static org.jeasy.flows.flow.ConditionalFlow.Builder.aNewConditionalFlow;
import static org.jeasy.flows.flow.ParallelFlow.Builder.aNewParallelFlow;
import static org.jeasy.flows.flow.RepeatFlow.Builder.aNewRepeatFlow;
import static org.jeasy.flows.flow.SequentialFlow.Builder.aNewSequentialFlow;

public class EngineImplTest {

    private final Engine engine = new EngineImpl();

    @Test
    public void run() {
        // given
        Flow flow = Mockito.mock(Flow.class);
        Context context = Mockito.mock(Context.class);

        // when
        engine.run(flow, context);

        // then
        Mockito.verify(flow).execute(context);
    }

    /**
     * The following tests are not really unit tests, but serve as examples of how to create a workflow and execute it
     */

    @Test
    public void composeWorkFlowFromSeparateFlowsAndExecuteIt() {

        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("done");

        RepeatFlow repeatFlow = aNewRepeatFlow()
                .named("print foo 3 times")
                .repeat(work1)
                .times(3)
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ParallelFlow parallelFlow = aNewParallelFlow()
                .named("print 'hello' and 'world' in parallel")
                .execute(work2, work3)
                .with(executorService)
                .build();

        ConditionalFlow conditionalFlow = aNewConditionalFlow()
                .execute(parallelFlow)
                .when(COMPLETED)
                .then(work4)
                .build();

        SequentialFlow sequentialFlow = aNewSequentialFlow()
                .execute(repeatFlow)
                .then(conditionalFlow)
                .build();

        Engine engine = aNewEngine().build();
        Context context = new Context();
        Report report = engine.run(sequentialFlow, context);
        executorService.shutdown();
        assertThat(report.getStatus()).isEqualTo(Status.COMPLETED);
        System.out.println("workflow report = " + report);
    }

    @Test
    public void defineWorkFlowInlineAndExecuteIt() {

        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("done");

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Flow workflow = aNewSequentialFlow()
                .execute(aNewRepeatFlow()
                            .named("print foo 3 times")
                            .repeat(work1)
                            .times(3)
                            .build())
                .then(aNewConditionalFlow()
                        .execute(aNewParallelFlow()
                                    .named("print 'hello' and 'world' in parallel")
                                    .execute(work2, work3)
                                    .with(executorService)
                                    .build())
                        .when(COMPLETED)
                        .then(work4)
                        .build())
                .build();

        Engine engine = aNewEngine().build();
        Context context = new Context();
        Report report = engine.run(workflow, context);
        executorService.shutdown();
        assertThat(report.getStatus()).isEqualTo(Status.COMPLETED);
        System.out.println("workflow report = " + report);
    }

    @Test
    public void useWorkContextToPassInitialParametersAndShareDataBetweenWorkUnits() {
        WordCountWork work1 = new WordCountWork(1);
        WordCountWork work2 = new WordCountWork(2);
        AggregateWordCountsWork work3 = new AggregateWordCountsWork();
        PrintWordCount work4 = new PrintWordCount();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Flow workflow = aNewSequentialFlow()
                .execute(aNewParallelFlow()
                            .execute(work1, work2)
                            .with(executorService)
                            .build())
                .then(work3)
                .then(work4)
                .build();

        Engine engine = aNewEngine().build();
        Context context = new Context();
        context.put("partition1", "hello foo");
        context.put("partition2", "hello bar");
        Report report = engine.run(workflow, context);
        executorService.shutdown();
        assertThat(report.getStatus()).isEqualTo(Status.COMPLETED);
    }

    static class PrintMessageWork implements Work {

        private final String message;

        public PrintMessageWork(String message) {
            this.message = message;
        }

        public String getName() {
            return "print message work";
        }

        public Report execute(Context context) {
            System.out.println(message);
            return new DefaultReport(Status.COMPLETED, context);
        }

    }
    
    static class WordCountWork implements Work {

        private final int partition;

        public WordCountWork(int partition) {
            this.partition = partition;
        }

        @Override
        public String getName() {
            return "count words in a given string";
        }

        @Override
        public Report execute(Context context) {
            String input = (String) context.get("partition" + partition);
            context.put("wordCountInPartition" + partition, input.split(" ").length);
            return new DefaultReport(Status.COMPLETED, context);
        }
    }
    
    static class AggregateWordCountsWork implements Work {

        @Override
        public String getName() {
            return "aggregate word counts from partitions";
        }

        @Override
        public Report execute(Context context) {
            Set<Map.Entry<String, Object>> entrySet = context.getValues().entrySet();
            int sum = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                if (entry.getKey().contains("InPartition")) {
                    sum += (int) entry.getValue();
                }
            }
            context.put("totalCount", sum);
            return new DefaultReport(Status.COMPLETED, context);
        }
    }

    static class PrintWordCount implements Work {

        @Override
        public String getName() {
            return "print total word count";
        }

        @Override
        public Report execute(Context context) {
            int totalCount = (int) context.get("totalCount");
            System.out.println(totalCount);
            return new DefaultReport(Status.COMPLETED, context);
        }
    }
}
