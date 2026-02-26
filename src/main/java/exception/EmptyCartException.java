package exception;

public class EmptyCartException extends Exception{
    String message;
    public EmptyCartException(String msg){
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return getClass().getSimpleName()+": "+message;
    }
}
