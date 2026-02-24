package model;

import util.IdGenerator;

import java.util.List;

public abstract class BaseItem implements MenuItem{
    private static final IdGenerator idGenerator = new IdGenerator();
    private Integer itemId;
    private String itemName;
    private Double itemPrice;
    private String itemDescription;
//    idk how to do this
    private List addOn;

    public BaseItem(){
        this.itemId = idGenerator.generateId();
    }

    public BaseItem(String itemName, Double itemPrice) {
        this();
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    @Override
    public Integer getItemId() {
        return itemId;
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String getItemDescription() {
        return itemDescription;
    }

    @Override
    public List getItemAddOnList() {
        return addOn;
    }
}
