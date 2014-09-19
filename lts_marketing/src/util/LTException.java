package util;

public class LTException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String message;
	
    public LTException() {
        super();
    }
 
    public LTException(String message) {
        super(message);
        this.message = message;
    }
 
    public LTException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
