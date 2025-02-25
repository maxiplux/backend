package app.quantun.backend.service;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * Service interface for exporting products to different file formats.
 */

public interface ProductExportService {

    /**
     * Export products to a CSV file.
     *
     * @param filePath the path where the CSV file will be created
     * @return the job execution ID
     * @throws JobParametersInvalidException       if the job parameters are invalid
     * @throws JobExecutionAlreadyRunningException if the job is already running
     * @throws JobRestartException                 if there is an error restarting the job
     * @throws JobInstanceAlreadyCompleteException if the job instance is already complete
     */
    Long exportProductsToCsv(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException;

    /**
     * Export products to an XLSX file.
     *
     * @param filePath the path where the XLSX file will be created
     * @return the job execution ID
     * @throws JobParametersInvalidException       if the job parameters are invalid
     * @throws JobExecutionAlreadyRunningException if the job is already running
     * @throws JobRestartException                 if there is an error restarting the job
     * @throws JobInstanceAlreadyCompleteException if the job instance is already complete
     */
    Long exportProductsToXlsx(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException;

    /**
     * Export products to a JSON file.
     *
     * @param filePath the path where the JSON file will be created
     * @return the job execution ID
     * @throws JobParametersInvalidException       if the job parameters are invalid
     * @throws JobExecutionAlreadyRunningException if the job is already running
     * @throws JobRestartException                 if there is an error restarting the job
     * @throws JobInstanceAlreadyCompleteException if the job instance is already complete
     */
    Long exportProductsToJson(String filePath) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException;
}