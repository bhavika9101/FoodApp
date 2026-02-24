package model.order;

import util.IdGenerator;
import java.util.HashSet;
import java.util.Set;

public class MenuCategory implements MenuComponent {
    private static final IdGenerator idGenerator = new IdGenerator();
    private final Integer categoryId;
    private String categoryName;
    private final Set<MenuComponent> componentSet = new HashSet<>();
    public MenuCategory(){
        this.categoryId = idGenerator.generateId();
    }
    public MenuCategory(String categoryName) {
        this();
        this.categoryName = categoryName;
    }
    @Override
    public Integer getId() {
        return categoryId;
    }

    @Override
    public String getName() {
        return categoryName;
    }

    @Override
    public Double getPrice() {
        return componentSet
                .stream()
                .mapToDouble(MenuComponent::getPrice)
                .sum();
    }

    @Override
    public Boolean isComponent() {
        return Boolean.TRUE;
    }

    @Override
    public Set<MenuComponent> getComponentSet() {
        return componentSet;
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println(categoryName);
        componentSet.forEach(MenuComponent::print);
    }

    public void add(MenuComponent menuComponent){
        componentSet.add(menuComponent);
    }
}
