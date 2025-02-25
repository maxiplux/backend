package app.quantun.backend.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Listener for logging step execution events.
 * Logs detailed information about step start, completion, and metrics.
 */
@Component
@Slf4j
public class StepExecutionLoggingListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Step '{}' of job '{}' starting",
                stepExecution.getStepName(),
                stepExecution.getJobExecution().getJobInstance().getJobName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Calculate duration
        long durationMillis = java.time.Duration.between(stepExecution.getStartTime(), stepExecution.getEndTime()).toMillis();

        String duration = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));

        // Log step completion with metrics
        log.info("Step '{}' of job '{}' completed with status: {} in {}",
                stepExecution.getStepName(),
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getStatus(),
                duration);

        log.info("Step '{}' metrics - Read: {}, Written: {}, Filtered: {}, Skipped: {}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getFilterCount(),
                stepExecution.getReadSkipCount() +
                        stepExecution.getProcessSkipCount() +
                        stepExecution.getWriteSkipCount());

        // Log commit count and rollbacks
        log.info("Step '{}' transaction metrics - Commits: {}, Rollbacks: {}",
                stepExecution.getStepName(),
                stepExecution.getCommitCount(),
                stepExecution.getRollbackCount());

        // Log any exceptions that occurred specifically in this step
        if (stepExecution.getFailureExceptions().size() > 0) {
            log.error("Step '{}' encountered {} exceptions:",
                    stepExecution.getStepName(),
                    stepExecution.getFailureExceptions().size());

            stepExecution.getFailureExceptions().forEach(ex ->
                    log.error("  Exception: {}", ex.getMessage(), ex));
        }
        return ExitStatus.COMPLETED;
    }
}
