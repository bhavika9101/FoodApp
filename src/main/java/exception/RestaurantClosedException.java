package exception;

public class RestaurantClosedException extends Exception{
    String message;
    public RestaurantClosedException(String msg){
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return getClass().getSimpleName()+": "+message;
    }
}
