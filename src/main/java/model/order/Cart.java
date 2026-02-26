package model.order;

import util.IdGenerator;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private static final IdGenerator idGenerator = new IdGenerator();

    private final Integer cartId;
    private Map<MenuItem, Integer> cartItemMap;
    private Integer customerId;

    public Cart() {
        this.cartId = idGenerator.generateId();
        this.cartItemMap = new HashMap<>();
    }

    public Cart(Map<MenuItem, Integer> cartItemMap, Integer customerId) {
        this();
        this.cartItemMap = cartItemMap;
        this.customerId = customerId;
    }

    public Integer getCartId() {
        return cartId;
    }

    public Map<MenuItem, Integer> getCartItemMap() {
        return cartItemMap;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
