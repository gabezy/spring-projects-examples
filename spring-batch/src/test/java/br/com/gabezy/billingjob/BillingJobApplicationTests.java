package br.com.gabezy.billingjob;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@SpringBatchTest
//@ExtendWith(OutputCaptureExtension.class) // to capture the output message CapturedOutput output
class BillingJobApplicationTests {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String BILLING_TABLE = "BILLING_DATA";

    @BeforeEach
    void setUpEach() {
        this.jobRepositoryTestUtils.removeJobExecutions();
        JdbcTestUtils.deleteFromTables(jdbcTemplate, BILLING_TABLE);
    }

    @Test
    void testJobExecution() throws Exception {
        // Given
        JobParameters jobParameters = this.jobLauncherTestUtils.getUniqueJobParametersBuilder()
            .addString("input.file", "input/billing-2023-01.csv")
            .addString("output.file", "staging/report-billing-2023-01.csv")
            .addJobParameter("data.year", 2023, Integer.class)
            .addJobParameter("data.month", 1, Integer.class)
            .toJobParameters();

        // When
        JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);

        // Then

        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        Assertions.assertTrue(Files.exists(Paths.get("staging", "billing-2023-01.csv")));

        Assertions.assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, BILLING_TABLE));

        Path billingReport = Paths.get("staging","billing-report-2023-01.csv");
        Assertions.assertTrue(Files.exists(billingReport));
        Assertions.assertEquals(781, Files.lines(billingReport).count());
    }

}
