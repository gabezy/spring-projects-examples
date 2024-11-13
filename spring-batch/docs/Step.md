# Step
A `Step` in Spring Batch is a domain object that basically serve as an independent and sequential phase of a batch job, can be a step that reads some file or create a report. It contains all the information necessary to define a unit of work in a batch `Job`.

```java
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StepExecution;

public interface Step {

    String getName();

    void execute(StepExecution stepExecution) throws JobInterruptedException;

}
```

The `StepExecution` object is similar to `JobExecution` representing the actual execution os the step at runtime. It contains various runtime details, such as the start time, execution status. his runtime information is stored by Spring Batch in the metadata repository, similar to the `JobExecution`,

## Types of Step
![types](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/bcbed634eed342346d30d61c0cfd8a30a55036a3/courses/course-spring-batch-essentials/step-and-tasklet.svg)

- `TasketStep`: Designed for simple tasks (like copying a file or creating an archive), or item-oriented tasks (like reading a file or a database table).
- `PartitionedStep`: Designed to process the input data set in partitions.
- `FlowStep`: Useful for logically grouping steps into flows.
- `JobStep`: Similar to a FlowStep but actually creates and launches a separate job execution for the steps in the specified flow. This is useful for creating a complex flow of jobs and sub-jobs.

### TaskletStep

The TaskletStep is an implementation of the Step interface based on the concept of a Tasklet. A Tasklet represents a unit of work that the Step should do when invoked. The Tasklet interface is defined as follows:

```java
@FunctionalInterface
public interface Tasklet {

    @Nullable
    RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
}
```
- The `RepeatStatus` is an enumeration that tells to the framework that work has been completed (RepeatStatus.COMPLETED) or not yet (RepeatStatus.CONTINUABLE). In the latter case, the TaskletStep re-invokes that Tasklet again.
- Each iteration of the `Tasklet` is executed in the scope of a database transaction. This is needed because Spring batch saves the work that has been done during the iteration in the persistent job repository and the `Step` can resume where it left off in case of failure.
    - It requires a `PlatformTransactionManager` to manage the transactions of the `Tasklet`.
- `StepContribution`: It represents the contribution of this `Tasklet` to the step (for example how many items were read, written, or otherwise processed).
- `ChunkContext`: It is a bag of key/value pairs that provide detail about the execution context of the `Tasklet`.
- The exception thrown by the `execute` method occurs when any error occurs, meaning that the `Step` failed (marking as well).

## Using Steps
It'll rarely have to implement a `Job` interface manually. In fact, Spring provides the `AbstractJob` classes that lets define the job as a flow of steps, which has **two variations**:

- `SimpleJob`: For sequential execution of steps.
- `FlowJob`: For complex step flows, including conditional branching and parallel execution

### SimpleJob
The `SimpleJob` **class** is designed to compose a job as a sequence of steps. A step should be completed successfully in order for the next step in the sequence start. If the **step fails**, the job is immediately terminated and subsequent steps are not executed.

```java
@Bean
public Job myJob(JobRepository jobRepository, Step step1, Step step2) {
  return new JobBuilder("job", jobRepository)
          .start(step1)
          .next(step2)
          .build();
}
```

### Define Steps
Similar to `JobBuilder` API, Spring Batch has `StepBuilder` API for create different type of Step. Spring Batch has the specific builder for each step type like: `TaskletStepBuilder`, `PartitionStepBuilder` and others.

```java
@Bean
public Step taskletStep(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager transactionManager) {
  return new StepBuilder("step1", jobRepository)
          .tasklet(tasklet, transactionManager)
          .build();
}
```
The `StepBuilder` accepts the step name and the job repository to report metadata to at construction time, as those are common to all step types.<br/>
The `StepBuilder.tasklet` method, which will use a `TaskletStepBuilder` to further define specific properties of the `TaskletStep`, mainly the `Tasklet` to execute as part of the step and the transaction manager to use for transactions. 

## Reading and Writing Data

To read and write data in Spring Batch, we can use the processing model chunk-oriented model. The idea of this model is to process the datasource in chunks of a configurable size.  

A chunk is a collection of "items" from a datasource. An item can be a line in a flat file, a record in a database table, etc. A chunk of items is represented by the `Chunk<T>` API.

```java
public class Chunk<T> implements Iterable<T> {
    private List<T> items;
}
```

### Transactions
Each chunk of items is read and written within the scope of a transaction. This way, chunks are either committed together or rolled back together, meaning the transaction will be commited if a chunk can be processed correctly, if not will fail together, like an "all-or-nothing" approach, but for the specific chunk that is being processed.  

If the transaction is committed, Spring Batch records, as part of the transaction, the execution progress (read count, write count, etc.) in its metadata repository and uses that information to restart where it left off in case of a failure.

If an error occurs while processing a chunk of items, then the transaction will be rolled back and restart from the last successful save-point.

*Obs.: the number of items to include in a chunk is called the commit-interval* and is the configurable size of a chunk that should be processed in a single transaction.

### Reading Data

```java
@FunctionalInterface
public interface ItemReader<T> {
  @Nullable
  T read() throws Exception;
}
```
The `read` method is used to **read one piece of data or an item**. Each call to this method is expected to return a **single item, one at time**. Spring Batch will call read as needed to create chunks of items, preventing loading the entire datasource in memory, only chunks of data.  

When the `read` method return `null`, this means that the datasource is exhausted, in other words, there is no more data to read.

### ItemReader Library

Spring Batch comes with a large library of `ItemReader` implementations to read data from a variety of datasources, like files, databases, message brokers, etc.

To read data from a flat file is used the `FlatFileItemReader`. Here is an example of how to configure such a reader:

```java
@Bean
public FlatFileItemReader<BillingData> billingDataFileReader() {
  return new FlatFileItemReaderBuilder<BiilingData>()
          .name("billingDataFileReader")
          .resource(new FileSystemResource("staging/billing-data.csv"))
          .delimited()
          .names("dataYear", "dataMonth", "accountId", "phoneNumber", "dataUsage", "callDuration", "smsCount")
          .targetType(BillingData.class)
          .build();
}
```
To build a `FlatFileItemReader`, is used the `FlatFileItemReaderBuilder` API to specific the **path** of the input file, the **expected fields** in the file and the **target type** to map data to.

It's recommended to one of the item readers provided by Spring Batch out-of-the-box, unless in a some situations a very specific requirement implementation is needed. To read data from a database is used the JDBC API `JdbcCursorItemReader`.

#### Writing Data

Similar to the `ItemReader` interface, writing data in Spring Batch is done through the `ItemWriter` interface.

```java
@FunctionalInterface
public interface ItemWriter<T> {
    
    void writer(Chunk<? extends T> chunk) throws Exception;
}
```

Because bulk writes operations are typically more efficient than single writes, the `write` method expects a chunk of items, unlike reading items which is done one at a time.

Note: if an error occurs while writing items, implementations of this interface are expected to throw an exception to signal the problem to the framework.

To write data to a table in a database is the `JdbcBatchItemWriter`, as the example. For flat file is used `FlatFileItemWriter`.

```java
@Bean
public JdbcBatchItemWriter<BillingData> billingDataTableWriter(DataSource dataSource) {
    String sql = "insert into BILLING_DATA values (:dataYear, :dataMonth, :accountId, :phoneNumber, :dataUsage, :callDuration, :smsCount)";
    return new JdbcBatchItemWriterBuilder<BillingData>()
            .dataSource(dataSource)
            .sql(sql)
            .beanMapped()
            .build();
}
```

## Configuring Chunk-Oriented Tasklet Steps

A chunk-oriented step in Spring Batch is a `TaskletStep` configured with a specific `Tasklet` type, the `ChunkOrientedTasklet`. This `Tasklet` is what implements the chunk-oriented processing model using an item reader and an item writer.

The chunk-oriented Tasklet configuration is similar to the `TaskletStep`, so is also configured using `StepBuilder`.

```java

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.transaction.PlatformTransactionManager;

@Bean
public Step ingestFile(JobRepository repository,
                       PlatformTransactionManager transactionManager,
                       FlatFileItemReader<BillingData> billingDataDataFileReader,
                       JdbcBatchItemWriter<BillingData> billingDataTableWriter) 
{
  return new StepBuilder("ingestFile", repository)
          .<BillingData, BillingData>chunk(100, transactionManager)
          .reader(billingDataDataFileReader)
          .writer(billingDataTableWriter)
          .build();
}
```
Attention points:

- Specify the step name and job repository (common for all steps).
- Define chunk size, or the commit-interval (First parameter). In this case, it is set to 100, which means Spring Batch will read and write 100 items as a unit in each transaction.
- Reference the `PlatformTransactionMangar` to manager the transactions of the tasklet. This is necessary because each call the `Tasklet.execute()` is done within the scope of a transaction.

## Processing Data

### ItemProcessor API

Processing items in a chunk-oriented step happens between reading and writing data. It's an optional phase and occurs after the reading process, then handed over to the writer (works as intermediate stage).

```java
@FunctionalInterface
public interface ItemProcessor<I, O> {
    @Nullable
    O process(@NonNull I item) throws Exception;
}
```

The `process` method receives an item of type `I` and returns an item of type `O` as output. The method can be used in multiple case, like **transforming data**, **enriching it**, **validating it**, or **filtering the data**.

### Transform Data

In this use case, the `process` takes an `I` item and return an `O` type, transforming the item in the process.

```java
public class BillingDataProcessor implements ItemProcessor<BillingData, ReportingData> {
    
    public ReportingData process(BillingData item) {
        return new ReportingData(item);
    }
    
}
```

### Enriching Data

In this use case, the type `I` and `O` is the same. The focus here is to enrich the data by adding information to it, like in the example below, an item processor is requesting external information.

```java
public class EnrichItemProcessor implements ItemProcessor<Person, Person> {
    
    private AddressService addressService;
    
    public EnrichItemProcessor(AddressService addressService) {
        this.addressService = addressService;
    }
    
    public BillingData(Person person) {
        Address address = addressService.getAddress(person);
        person.setAddress(address);
        return person;
    }
    
}
```

#### Validating Data

The `process` method is designed to throw an exception in case of a processing error, which could be technical errors (like a failure to call an external service) or functional errors (like invalid items). In this case, it can be used an `ItemProcessor` validate if the input data is valid or not before saving it to the target system.

```java
public class ValidatingItemProcessor implements ItemProcessor<Person, Person> {
    
    private EmailService emailService;
    
    public ValidatingItemProcessor(EmailService emailService) {
        this.emailService = emailService;
    }
    
    public Person process(Person person) {
        if (!emailService.isValid(person.getEmail())) {
            throw new InvalidEmailException();
        }
        return person;
    }
    
}
```

### Filtering Data

The last use case for an `ItemProcessor` is to filter an item but telling Spring Batch how to filter out the current item. Filtering the current item simply means to not let it continue in the processing pipeline. Therefore, it'll be excluded from being written as part of the current chunk.

```java
public class FilteringItemProcessor implements ItemProcessor<BillingData, BillingData> {

    public BillingData process(BillingData item) {
        if (item.getMonthlySpending() < 150) {
            return null;
        }
        return item;
    }

}
```

## Batch Scopes

Spring provides two custom Spring bean scopes: **job scope** and **step scope**. Batch-scoped beans are not created at application startup like **singleton** beans, but rather created at **runtime** when the **job or step is executed**. A **job-scoped** bean will only be instantiated when the job is stared. Similarly, a step-scoped bean won't be instantiated until the step is started. Since this type of bean is lazily instantiated at runtime, any runtime job parameter or execution context attribute can be resolved.

Note: The scopes in Spring Batch are configured with Spring Expression Language (SpEL).

### Spring Expression Language (SpEL)

**Job parameters** and **execution context values** can be resolved by using the SpEL, but specifying the job parameter or context values should be bound in the bean definition method "late" at runtime, like a "Late binding" job parameter or step attributes.

```java
@Bean
@StepScope
public FlatFileItemReader<BillingData> reader(@Value("#{jobParameters['input.file']}") String inputFile) {

  return new FlatFileItemReaderBuilder<BillingData>()
          .resource(new FileSystemResource(inputFile))
          // other properties 
          .build();

}
```

This snippet creates a step-scoped bean of `FlatFileItemReader`, thanks to the `@StepdScope` annotation. This item reader is configured with an input file from a job parameter `input.file` specified by using the `@Value("#{jobParameters['input.file']}")` notation. Basically, this notation instructs Spring Framework to resolve the value **lazily** from the `jobParameters` object. With that, this item reader will be configured dynamically with the value from the job parameter.

