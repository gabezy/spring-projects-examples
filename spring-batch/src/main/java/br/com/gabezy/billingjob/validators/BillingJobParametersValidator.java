package br.com.gabezy.billingjob.validators;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.util.Objects;

public class BillingJobParametersValidator implements JobParametersValidator {


    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (Objects.isNull(parameters) || parameters.isEmpty()) {
            throw new JobParametersInvalidException("Job missing parameters");
        }

        String inputFile = parameters.getString("input.file");
        if (Objects.isNull(inputFile) || inputFile.isEmpty()) {
            throw new JobParametersInvalidException("input.file parameters is missing");
        }
    }
}
