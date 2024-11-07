package br.com.gabezy.billingjob.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

public class CleanupBillingDatabaseStep implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(CleanupBillingDatabaseStep.class);
    private static final String BILLING_TABLE = "BILLING_DATA";

    public CleanupBillingDatabaseStep(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        JobParameters jobParameters = contribution.getStepExecution().getJobParameters();

        JobParameter yearParam =  jobParameters.getParameters().get("data.year");
        JobParameter monthParam =  jobParameters.getParameters().get("data.month");

        if (Objects.nonNull(yearParam) && Objects.nonNull(monthParam) && yearParam.getType().equals(Integer.class) &&
            monthParam.getType().equals(Integer.class)) {

            Integer year = (Integer) yearParam.getValue();
            Integer month = (Integer) monthParam.getValue();

            String sql = String.format("SELECT COUNT(*) FROM %s WHERE DATA_YEAR = %d AND DATA_MONTH = %d",
                    BILLING_TABLE, year, month);

            Integer recordCount = jdbcTemplate.queryForObject(sql, Integer.class);

            if (Objects.nonNull(recordCount) && recordCount > 0) {
                String deleteSqlStatement = String.format("DELETE FROM %s WHERE DATA_YEAR = %d AND DATA_MONTH = %d",
                        BILLING_TABLE, year, month);

                jdbcTemplate.update(deleteSqlStatement);
                log.info("Deleted all records from table: " + BILLING_TABLE);
            }

        }


        return RepeatStatus.FINISHED;
    }
}
