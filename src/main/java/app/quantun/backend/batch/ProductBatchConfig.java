package app.quantun.backend.batch;

import app.quantun.backend.models.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuration for Spring Batch jobs to export product data to various formats.
 * Supports exporting to CSV, JSON, and XLSX formats.
 */
@Configuration
@Slf4j
public class ProductBatchConfig {

    @Value("${batch.chunk-size:100}")
    private int chunkSize;

    /**
     * Creates an item reader for products from the database.
     *
     * @param entityManagerFactory JPA entity manager factory
     * @return A configured JPA paging item reader for products
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<Product> productItemReader(EntityManagerFactory entityManagerFactory) {
        log.info("Initializing product reader with chunk size: {}", chunkSize);
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Product p ORDER BY p.id")
                .build();
    }

    /**
     * Optional processor to filter or transform products before export.
     * Currently just logs the product being processed.
     *
     * @return An item processor for products
     */
    @Bean
    @StepScope
    public ItemProcessor<Product, Product> productItemProcessor() {
        return product -> {
            log.debug("Processing product: {}", product.getId());
            return product;
        };
    }

    /**
     * Creates a CSV file writer for products.
     *
     * @param outputPath Path to write the CSV file
     * @return A configured flat file item writer
     */
    @Bean
    @StepScope
    public FlatFileItemWriter<Product> csvFileItemWriter(@Value("${output.csv.path}") String outputPath) {
        log.info("Initializing CSV writer to path: {}", outputPath);

        // Create field extractor
        BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "description", "price", "inStock"});

        // Create line aggregator
        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Product>()
                .name("csvFileItemWriter")
                .resource(new FileSystemResource(outputPath))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("id,name,description,price,inStock"))
                .shouldDeleteIfExists(true)
                .build();
    }

    /**
     * Creates a JSON file writer for products.
     *
     * @param outputPath Path to write the JSON file
     * @return A configured JSON file item writer
     */
    @Bean
    @StepScope
    public JsonFileItemWriter<Product> jsonFileItemWriter(@Value("${output.json.path}") String outputPath) {
        log.info("Initializing JSON writer to path: {}", outputPath);
        return new JsonFileItemWriterBuilder<Product>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource(outputPath))
                .name("jsonFileItemWriter")
                .build();
    }

    /**
     * Creates an XLSX file writer for products.
     * Uses Apache POI to generate Excel files.
     *
     * @param outputPath Path to write the XLSX file
     * @return A configured abstract file item writer
     */
    @Bean
    @StepScope
    public AbstractFileItemWriter<Product> xlsxFileItemWriter(@Value("${output.xlsx.path}") String outputPath) {
        log.info("Creating XLSX writer bean for path: {}", outputPath);

        return new AbstractFileItemWriter<Product>() {
            private Workbook workbook;
            private Sheet sheet;
            private int rowNum = 0;
            private int itemCount = 0;
            private boolean workbookClosed = false;
            private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            @Override
            public void afterPropertiesSet() throws Exception {
                log.info("Initializing XLSX writer properties for: {}", outputPath);
                // Set required properties for the writer
                setName("xlsxFileItemWriter");
                setResource(new FileSystemResource(outputPath));
                setAppendAllowed(false);
                setForceSync(true);
                // CRITICAL: Disable transactional writing which can cause issues with POI
                setTransactional(false);

                // No call to super.afterPropertiesSet() as it's not available
                log.info("XLSX writer properties initialized successfully");
            }

            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {
                log.info("Opening XLSX writer for path: {}", outputPath);
                try {
                    // First call super.open() to ensure proper initialization
                    super.open(executionContext);

                    // Then create the workbook
                    workbook = new XSSFWorkbook();
                    sheet = workbook.createSheet("Products");
                    rowNum = 0;
                    workbookClosed = false;

                    // Create metadata sheet
                    Sheet metadataSheet = workbook.createSheet("Metadata");
                    var metaRow1 = metadataSheet.createRow(0);
                    metaRow1.createCell(0).setCellValue("Export Date:");
                    metaRow1.createCell(1).setCellValue(timestamp);

                    // Create header row
                    var headerRow = sheet.createRow(rowNum++);
                    headerRow.createCell(0).setCellValue("ID");
                    headerRow.createCell(1).setCellValue("Name");
                    headerRow.createCell(2).setCellValue("Description");
                    headerRow.createCell(3).setCellValue("Price");
                    headerRow.createCell(4).setCellValue("In Stock");

                    log.info("XLSX workbook initialized with headers at {}", outputPath);
                } catch (Exception e) {
                    log.error("Error opening XLSX writer: {}", e.getMessage(), e);
                    throw new ItemStreamException("Failed to open XLSX writer", e);
                }
            }

            @Override
            public void update(ExecutionContext executionContext) throws ItemStreamException {
                log.debug("Updating XLSX writer execution context, processed {} items", itemCount);
                super.update(executionContext);
            }

            @Override
            public String doWrite(Chunk<? extends Product> chunk) {
                if (workbookClosed) {
                    log.error("Attempted to write to closed workbook!");
                    throw new IllegalStateException("Workbook has already been closed");
                }

                int chunkSize = chunk.getItems().size();
                log.info("Writing chunk of {} products to XLSX (rows {}-{})",
                        chunkSize, rowNum, rowNum + chunkSize - 1);

                try {
                    for (Product product : chunk.getItems()) {
                        var row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(product.getId());
                        row.createCell(1).setCellValue(product.getName() != null ? product.getName() : "");
                        row.createCell(2).setCellValue(product.getDescription() != null ? product.getDescription() : "");
                        row.createCell(3).setCellValue(product.getPrice() != null ? product.getPrice().toString() : "");
                        row.createCell(4).setCellValue(product.isInStock());
                        itemCount++;
                    }
                    log.debug("Successfully wrote chunk to memory, total items processed: {}", itemCount);

                    // IMPORTANT: Return an empty string instead of null to avoid NullPointerException
                    return "";
                } catch (Exception e) {
                    log.error("Error writing to XLSX at row {}: {}", rowNum, e.getMessage(), e);
                    throw new RuntimeException("Failed to write products to XLSX", e);
                }
            }

            @Override
            public void close() {
                log.info("Closing XLSX writer, processed {} products", itemCount);

                // Only close and write if not already closed
                if (workbook != null && !workbookClosed) {
                    FileOutputStream fileOut = null;
                    try {
                        // Write the workbook to file before calling super.close()
                        log.info("Writing workbook to file: {}", outputPath);
                        fileOut = new FileOutputStream(outputPath);
                        workbook.write(fileOut);
                        log.info("Successfully wrote XLSX file to: {}", outputPath);

                    } catch (IOException e) {
                        log.error("Error writing XLSX file {}: {}", outputPath, e.getMessage(), e);
                    } finally {
                        // Close the output stream
                        if (fileOut != null) {
                            try {
                                fileOut.close();
                            } catch (IOException e) {
                                log.error("Error closing file output stream: {}", e.getMessage(), e);
                            }
                        }

                        // Close the workbook
                        if (workbook != null) {
                            try {
                                workbook.close();
                                workbookClosed = true;
                                log.debug("Workbook closed successfully");
                            } catch (IOException e) {
                                log.error("Error closing workbook: {}", e.getMessage(), e);
                            }
                        }

                        // Finally call super.close() after all our resources are closed
                        super.close();
                    }
                } else {
                    if (workbookClosed) {
                        log.warn("Workbook already closed, skipping physical file write");
                    } else {
                        log.warn("Workbook is null, nothing to close");
                    }
                    super.close();
                }
            }

            // Override to bypass the standard writing mechanism
            @Override
            public void write(Chunk<? extends Product> chunk) throws Exception {
                if (workbookClosed) {
                    log.error("Attempted to write to closed workbook!");
                    throw new IllegalStateException("Workbook has already been closed");
                }

                doWrite(chunk);
            }
        };
    }

    /**
     * Creates a step for exporting products to CSV.
     */
    @Bean
    public Step exportCsvStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JpaPagingItemReader<Product> reader,
                              ItemProcessor<Product, Product> processor,
                              FlatFileItemWriter<Product> csvWriter) {
        log.info("Building CSV export step with chunk size: {}", chunkSize);
        return new StepBuilder("exportCsvStep", jobRepository)
                .<Product, Product>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(csvWriter)
                .listener(new StepExecutionLoggingListener())
                .build();
    }

    /**
     * Creates a step for exporting products to JSON.
     */
    @Bean
    public Step exportJsonStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               JpaPagingItemReader<Product> reader,
                               ItemProcessor<Product, Product> processor,
                               JsonFileItemWriter<Product> jsonWriter) {
        log.info("Building JSON export step with chunk size: {}", chunkSize);
        return new StepBuilder("exportJsonStep", jobRepository)
                .<Product, Product>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(jsonWriter)
                .listener(new StepExecutionLoggingListener())
                .build();
    }

    /**
     * Creates a step for exporting products to XLSX.
     */
    @Bean
    public Step exportXlsxStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               JpaPagingItemReader<Product> reader,
                               ItemProcessor<Product, Product> processor,
                               @Qualifier("xlsxFileItemWriter") AbstractFileItemWriter<Product> xlsxWriter) {
        log.info("Building XLSX export step with chunk size: {}", chunkSize);
        return new StepBuilder("exportXlsxStep", jobRepository)
                .<Product, Product>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(xlsxWriter)
                .listener(new StepExecutionLoggingListener())
                .build();
    }

    /**
     * Creates a job for exporting products to CSV.
     */
    @Bean
    public Job exportCsvJob(JobRepository jobRepository, Step exportCsvStep) {
        log.info("Building CSV export job");
        return new JobBuilder("exportCsvJob", jobRepository)
                .start(exportCsvStep)
                .listener(new JobExecutionLoggingListener())
                .build();
    }

    /**
     * Creates a job for exporting products to JSON.
     */
    @Bean
    public Job exportJsonJob(JobRepository jobRepository, Step exportJsonStep) {
        log.info("Building JSON export job");
        return new JobBuilder("exportJsonJob", jobRepository)
                .start(exportJsonStep)
                .listener(new JobExecutionLoggingListener())
                .build();
    }

    /**
     * Creates a job for exporting products to XLSX.
     */
    @Bean
    public Job exportXlsxJob(JobRepository jobRepository, Step exportXlsxStep) {
        log.info("Building XLSX export job");
        return new JobBuilder("exportXlsxJob", jobRepository)
                .start(exportXlsxStep)
                .listener(new JobExecutionLoggingListener())
                .build();
    }
}