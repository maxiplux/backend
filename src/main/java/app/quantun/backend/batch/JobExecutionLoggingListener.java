package app.quantun.backend.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Listener for logging job execution events.
 * Logs detailed information about job start, completion, and metrics.
 */
@Component
@Slf4j
public class JobExecutionLoggingListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job '{}' starting with parameters: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long durationMillis = java.time.Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();

        String duration = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));

        log.info("Job '{}' completed with status: {} in {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                duration);

        // Log detailed metrics
        int readCount = 0;
        int writeCount = 0;
        int skipCount = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getReadSkipCount() +
                    stepExecution.getProcessSkipCount() +
                    stepExecution.getWriteSkipCount();
        }

        log.info("Job '{}' metrics - Read: {}, Written: {}, Skipped: {}",
                jobExecution.getJobInstance().getJobName(),
                readCount, writeCount, skipCount);

        // Log any exceptions that occurred
        if (jobExecution.getAllFailureExceptions().size() > 0) {
            log.error("Job '{}' encountered {} exceptions:",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getAllFailureExceptions().size());

            jobExecution.getAllFailureExceptions().forEach(ex ->
                    log.error("  Exception: {}", ex.getMessage(), ex));
        }
    }
}

