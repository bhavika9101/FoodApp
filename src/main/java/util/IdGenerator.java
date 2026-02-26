package util;

public class IdGenerator {
    private Integer id = 0;
    public Integer generateId(){
        return ++id;
    }
}
