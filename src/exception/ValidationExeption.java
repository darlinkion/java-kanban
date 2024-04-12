package exception;

public class ValidationExeption extends RuntimeException {
    public ValidationExeption(String message) {
        super(message);
    }
}