package model;

import util.IdGenerator;

public class MenuItem {
    private static final IdGenerator idGenerator = new IdGenerator();
    private final Integer itemId;
    private String itemName;
    private Double itemPrice;
    private String itemDescription;

    public MenuItem(){
        this.itemId = idGenerator.generateId();
    }

    public MenuItem(String itemName, Double itemPrice) {
        this();
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    
    public Integer getItemId() {
        return itemId;
    }

    
    public String getItemName() {
        return itemName;
    }

    
    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    
    public String getItemDescription() {
        return itemDescription;
    }
}
