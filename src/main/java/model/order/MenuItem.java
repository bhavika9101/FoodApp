package model.order;

import util.IdGenerator;

import java.util.Set;

public class MenuItem implements MenuComponent{
    private static final IdGenerator idGenerator = new IdGenerator();
    private final Integer itemId;
    private String itemName;
    private Double itemPrice;
    public MenuItem(){
        this.itemId = idGenerator.generateId();
    }
    public MenuItem(String itemName, Double itemPrice) {
        this();
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    @Override
    public Integer getId() {
        return itemId;
    }

    @Override
    public String getName() {
        return itemName;
    }

    @Override
    public Double getPrice() {
        return itemPrice;
    }

    @Override
    public Boolean isComponent() {
        return Boolean.FALSE;
    }

    @Override
    public Set<MenuComponent> getComponentSet() {
        return null;
    }

    @Override
    public void print() {
        System.out.printf("%-5s %-30s %-10s\n", getId(), getName(), getPrice());
    }

    @Override
    public void add(MenuComponent menuComponent) {
        System.out.println("Unsupported operation.");
    }
}
