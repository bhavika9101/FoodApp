package exception;

public class UserNotFoundException extends Exception{
    String message;
    public UserNotFoundException(String msg){
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return getClass().getSimpleName()+": "+message;
    }
}
