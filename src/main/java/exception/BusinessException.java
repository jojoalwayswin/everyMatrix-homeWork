package exception;

public class BusinessException extends Exception{
    @java.io.Serial
    private static final long serialVersionUID = -1L;
    public BusinessException(String message) {
        super(message);
    }
}
