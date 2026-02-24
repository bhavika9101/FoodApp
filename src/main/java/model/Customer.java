package model;
public class Customer extends BaseUser{
    public Customer(){
        super();
    }
    public Customer(String customerName, String password){
        super(customerName, password);
    }
}
