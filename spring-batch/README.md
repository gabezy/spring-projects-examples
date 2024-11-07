# Spring Cellular Billing Batch Application

This project is a batch processing application designed to generate billing reports for an imaginary cell phone company, **Spring Cellular**. The application reads billing data from a PostgreSQL database, processes it, and outputs billing reports in a specified format. Built using Java 17 and Spring Boot, the application leverages **Spring Batch** for efficient batch processing.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Batch**
- **PostgreSQL**

## Features

- **Billing Data Processing**: Reads billing information from a PostgreSQL database, processes data for each customer, and aggregates it by billing period.
- **Report Generation**: Generates a detailed billing report for each customer, including call duration, SMS usage, and total charges.
- **Batch Processing**: Utilizes Spring Batch to handle large volumes of billing data efficiently, with job restartability and transaction management.

## Prerequisites

- **Java 17**: Ensure you have Java 17 installed on your machine.
- **Docker**: Required to run the PostgreSQL container if you don't have PostgreSQL installed locally.
- **Maven** or **Gradle**: To manage dependencies and build the application.

## Definition 

### Key concepts 
![concepts img](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-domain-model.svg)

- **JobLaucher**: It is the entity that launch the **Job** and can receive **JobParameters**.
- **Job**: It is the entity that encapsulates an entire batch process, that runs from start to finish without interruption. The **Job** has one or more **Steps**.
- **Step**: A Step is a unit that can be a simple task (such as copying a file or creating an archive), or an item-oriented task (such as exporting records from a relational database table to a file). A Step has
  - ItemReader (Mandatory)
  - ItemProcessor (Optional)
  - ItemWriter (Mandatory)

### Batch Domain Model
![Batch Domain Model](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-relational-model.svg)

- **Job_Instance**: This table contains all information relevant to a job definition, such as the job name and its identification key.
- **Job_Execution**: This table holds all information relevant to the execution of a job, like the start time, end time, and status. Every time a job is run, a new row is inserted in this table.
- **Job_Execution_Context**: This table holds the execution context of a job. An execution context is a set of key/value pairs of runtime information that typically represents the state that must be retrieved after a failure.
- **Step_Execution**: This table holds all information relevant to the execution of a step, such as the start time, end time, item read count, and item write count. Every time a step is run, a new row is inserted in this table.
- **Step_Execution_Context**: This table holds the execution context of a step. This is similar to the table that holds the execution context of a job, but instead it stores the execution context of a step.
- **Job_Execution_Params**: This table contains the runtime parameters of a job execution.

***Obs***: No, you don't have to implement this domain model yourself. Spring Batch automatically creates and manages these tables for you. The domain model is provided out of the box when you use Spring Batch. Here's what you need to know:

1. **Automatic Schema Creation**: Spring Batch provides SQL scripts to create these tables automatically. You can find them in the `org.springframework.batch.core` package.
2. Configuration Options:
   - Let Spring Batch create the schema automatically using `spring.batch.jdbc.initialize-schema=always`. 
   - Create it manually using the provided scripts
   - Use an existing schema if you already have one

#### Relation Job x JobInstance x  JobParameters x JobExecution
![relation](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-class-relations.svg)

JobParameters are typically used to distinguish one JobInstance from another. In other words, they are used to identify a specific JobInstance.


#### Life cycle of a JobInstance
![life cycle jobinstance](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/lifecycle-example.svg)


## Testing
`@SpringBatchTest` annotation registers the `JobLauncherTestUtils` and `JobRepositoryTestUtils` as Spring beans and can be used int the Test, so just needed to be autowired them in the test class.

Can use `JobRepositoryTestUtils` methods like `removeJobExecutions` to clean up the metadata from the database for each test.

## Step
A **Step** in Spring Batch is a domain object that basically serve as a independent and sequential phase of a batch job, can be a step that reads some file or create a report. It contains all the information necessary to define a unit of work in a batch `Job`.

```java
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StepExecution;

public interface Step {

    String getName();

    void execute(StepExecution stepExecution) throws JobInterruptedException;

}
```

The `StepExecution` object is similar to `JobExecution` representing the actual execution os the step at runtime. It contains various runtime details, such as the start time, execution status. his runtime information is stored by Spring Batch in the metadata repository, similar to the `JobExecution`,

#### Step's types

![types](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/bcbed634eed342346d30d61c0cfd8a30a55036a3/courses/course-spring-batch-essentials/step-and-tasklet.svg)

- `TasketStep`: Designed for simple tasks (like copying a file or creating an archive), or item-oriented tasks (like reading a file or a database table).
- `PartitionedStep`: Designed to process the input data set in partitions.
- `FlowStep`: Useful for logically grouping steps into flows.
- `JobStep`: Similar to a FlowStep but actually creates and launches a separate job execution for the steps in the specified flow. This is useful for creating a complex flow of jobs and sub-jobs.

```shell
# disables the automatic execution of the job by Spring Boot
./mvnw clean test -Dspring.batch.job.enabled=false 
# disables the test
./mvnw package -Dmaven.skip.test=true
```

## Getting Started

### Running PostgreSQL with Docker

If you don't have PostgreSQL installed locally, you can use Docker to run a PostgreSQL container.

1. Create a `docker-compose.yml` file with the following content:

   ```yaml
   version: '3.8'
   services:
     postgres:
       image: postgres:15
       environment:
         POSTGRES_DB: spring_cellular
         POSTGRES_USER: postgres
         POSTGRES_PASSWORD: password
       ports:
         - "5432:5432"
       volumes:
         - postgres-data:/var/lib/postgresql/data
   volumes:
     postgres-data:
   ```
2. In application.yml, set the database connection details to match the Docker configuration:
    
    ```yaml
    spring:
    datasource:
    url: jdbc:postgresql://localhost:5432/spring_cellular
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
   ```
3. Run the following SQL script to create the necessary tables for Spring Batch:

    ```postgresql
    CREATE TABLE BATCH_JOB_INSTANCE (
    JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT,
    JOB_NAME VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32) NOT NULL,
    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
    );

    CREATE TABLE BATCH_JOB_EXECUTION (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    START_TIME TIMESTAMP DEFAULT NULL,
    END_TIME TIMESTAMP DEFAULT NULL,
    STATUS VARCHAR(10),
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
    REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
    );

    CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
    JOB_EXECUTION_ID BIGINT NOT NULL,
    PARAMETER_NAME VARCHAR(100) NOT NULL,
    PARAMETER_TYPE VARCHAR(100) NOT NULL,
    PARAMETER_VALUE VARCHAR(2500),
    IDENTIFYING CHAR(1) NOT NULL,
    CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );

    CREATE TABLE BATCH_STEP_EXECUTION (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT NOT NULL,
    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    START_TIME TIMESTAMP DEFAULT NULL,
    END_TIME TIMESTAMP DEFAULT NULL,
    STATUS VARCHAR(10),
    COMMIT_COUNT BIGINT,
    READ_COUNT BIGINT,
    FILTER_COUNT BIGINT,
    WRITE_COUNT BIGINT,
    READ_SKIP_COUNT BIGINT,
    WRITE_SKIP_COUNT BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT BIGINT,
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );

    CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
    REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
    );

    CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );

    CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
    CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
    CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
    ```