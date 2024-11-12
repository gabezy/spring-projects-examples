# Making Jobs tolerant to faults

Batch jobs are never executed in isolation. they consume/produce data and often interact with external components and services. This interaction exposes the job to different kinds of errors (human or system).

## State Management

Because how the domain model of Spring Batch is designed (each job execution is persisted on the job repository , it's possible to restart failed job instances.

Restartability is Spring Batch is implemented at two distinct levels: *inter-step* restartability and *inner-step* restartability.

### Inter-Step Restartability

Inter-step restartability refers to resuming a job from the last failed step, without re-executing the steps that were successfully executed in the previous run.

For instance, if a job is composed of 2 sequential steps and fails at the second step, then the first step won't be re-executed in case of a restart.

#### Inner-Step Restartability

Inner-step restartability refers to resuming a failed step where it left off, i.e. from within the step itself.

This feature isn't particular to a specific type of steps, but is typically related to chunk-oriented steps. Also, the chunk-oriented processing model is similar to the Job processing and cal restart from the last save point, meaning that successfully processed chunks aren't reprocessed again, in the case of a restart.

## Restarting Failed Jobs

To use the restartability feature in Spring Batch, it is not necessary to activate a flag or use a specific API, it's a feature that's provided automatically by the framework. To restart a failed job instance is just a matter of relaunching it with the same identifying job parameters.

##  Error handling

**By default**, if an exception occurs in a given step, the step and its enclosing job will fail. In these cases, there are several possibilities:

- If the error is *transient* (like a failed call to a flaky web service), you can decide to restart the job. However, there is no guarantee that will work or the transient error might happen again. In ths case, it is necessary to implement a retry policy around the operation that might fail.
- If the error is not *transient* (like an incorrect input data), then restarting the job won't solve the problem. in this case, it is necessary to decide if the bad input will be tolerated and skip it for late analysis or fix the problem and restart the job.


## Handling non-transient errors with skips

While transient errors can be addressed by retrying operations, some errors are "permanent". For example, if a line in a flat file is not correctly formatted and the item reader can't parse it, no matter how many times you retry the read operation, it'll always fail.

## Skipping Incorrect Items

The skip feature in Spring Batch is specific to **chunk-oriented steps**. This feature covers all the phases of the chunk-oriented processing model: when errors occur while **reading, processing, or writing items**. To activate this feature, It is needed to define "fault-tolerant" step.

```java
@Bean
public Step step(JobRepository repository, JdbcTransactionManager transactionManager,
                 ItemReader<String> itemReader, ItemWriter<String> itemWriter) {
    return new StepBuilder("myStep", repository)
            .<String, String>chunk(100, transactionManager)
            .reader(itemReader)
            .writer(itemWriter)
            .faultTolerant()
            .skip(FlatFileParseException.class) // which expection should cause the current item to be skipped
                                                // the FlatFileParseException is thrown because the current line cannot be parsed
            .skipLimit(5) // used to define the maximum number of items to skip
            .build();
}
```
This method returns a `FaultTolerantStepBuilder` that allows you to define fault-tolerance features (skip or retry). In this case is implement a skip policy.

## Handling Skipped Items

The `SkipListener` is an extension point that Spring Batch provides to give a way to handle skipped items, like **logging** or **saving** for later analysis.

```java
public interface SkipListener<T, S> extends StepListener {
    
    default void onSkipInRead(Throwable t) {}
    
    default void onSkipInWrite(S item, Throwable t) {}
    
    default void onSkipInProcess(T item, Throwable t) {}
}
```

This interface provides 3 methods to implement the logic of what to do if an item is skipped during the `read`, `process` or `write` operation. Once implemented, the `SkipListener` can be registered in the step using the `FaultTolerantStepBuilder.listener(SkipListener)` API.

## Custom Skip Policy

```java
@FunctionalInterface
public interface SkipPolicy {

	boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException;

}
```

This is a functional interface with a single method `shouldSkip` that's designed to specify whether the current item should be skipped or not. This method provides a handle to a `Throwable` object that contains all the details and context about the current item and the exception that happened.

Once the custom `SkipPolicy` is implemented, it can be registered in the step using the `FaultTolerantStepBuilder.skipPolicy(SkipPolicy)` API.


## Handling transient errors with retry

For transients errors, like call a flaky web service or hit a database lock, it is necessary to implement a retry policy. it is inefficient to fail an entire job and restart it later because a failed operation. For this reason, Spring Batch provides a **retry feature**.

## Retrying transients errors

The retry feature is based on the [Spring Retry](https://github.com/spring-projects/spring-retry) and is similar to the skip feature. It will be needed to define a "fault-tolerant" step with `faultTolerant()` method on the `StepBuilder`.

```java
@Bean
public Step step(
        JobRepository jobRepository, JdbcTransactionManager transactionManager,
        ItemReader<String> itemReader, ItemWriter<String> itemWriter) {
    return new StepBuilder("myStep", jobRepository)
            .<String, String>chunk(100, transactionManager)
            .reader(itemReader)
            .writer(itemWriter)
            .faultTolerant()
            .retry(TransientException.class)
            .retryLimit(5)
            .build();
}
```

## Handling Retry attempts

For auditing purposes, Spring Batch provides a way to register a RetryListener in the step in order to plug in custom code during retry attempts: `onError`, `onSuccess`, and so on.

```java
public interface RetryListener {

   default <T, E extends Throwable> void onSuccess(
          RetryContext context,
          RetryCallback<T, E> callback,
          T result) {
   }

   default <T, E extends Throwable> void onError(
          RetryContext context,
          RetryCallback<T, E> callback,
	   Throwable throwable) {
   }
}
```

Once the `RetryListener` is implemented, it can be registered in the step by using the `FaultTolerantStepBuilder.listener(RetryListener)` method.

## Custom Retry Polices 

```java
public interface RetryPolicy extends Serializable {
   boolean canRetry(RetryContext context);
   void registerThrowable(RetryContext context, Throwable throwable);
}
```



