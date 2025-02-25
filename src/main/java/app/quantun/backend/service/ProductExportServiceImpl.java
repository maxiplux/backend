package app.quantun.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductExportServiceImpl implements ProductExportService {

    private final JobLauncher jobLauncher;
    private final Job exportCsvJob;
    private final Job exportJsonJob;
    private final Job exportXlsxJob;

    @Override
    public Long exportProductsToCsv(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("output.csv.path", filePath)
                .addDate("date", new Date())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(exportCsvJob, jobParameters);
        return execution.getId();
    }

    @Override
    public Long exportProductsToXlsx(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("output.xlsx.path", filePath)
                .addDate("date", new Date())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(exportXlsxJob, jobParameters);
        return execution.getId();
    }

    @Override
    public Long exportProductsToJson(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("output.json.path", filePath)
                .addDate("date", new Date())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(exportJsonJob, jobParameters);
        return execution.getId();
    }
}