package model.order;

import model.payment.Discount;
import util.IdGenerator;

import java.util.List;
import java.util.Map;

public class Cart {
    private static final IdGenerator idGenerator = new IdGenerator();

    private Integer cartId;
    private Map<MenuItem, Integer> cartItemList;
    private Integer customerId;

    public Cart(){
        this.cartId = idGenerator.generateId();
    }
    public Cart(Map<MenuItem, Integer> cartItemList, Integer customerId){
        this.cartItemList = cartItemList;
        this.customerId = customerId;
    }
    public Integer getCartId() {
        return cartId;
    }
    public Map<MenuItem, Integer> getCartItemList() {
        return cartItemList;
    }
    public Integer getCustomerId() {
        return customerId;
    }
}
