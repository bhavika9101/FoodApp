package model.order;

import java.util.Set;

public interface MenuComponent {
    Integer getId();
    String getName();
    Double getPrice();
    Boolean isComponent();
    Set<MenuComponent> getComponentSet();
    void print();
    void add(MenuComponent menuComponent);
}
