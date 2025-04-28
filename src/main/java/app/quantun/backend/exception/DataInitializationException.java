package app.quantun.backend.exception;

public class DataInitializationException extends RuntimeException {
    public DataInitializationException(String failedToInitializeSampleData, Exception e)
    {
        super(failedToInitializeSampleData, e);
    }
}
