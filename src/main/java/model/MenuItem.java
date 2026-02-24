package model;

import java.util.List;

public interface MenuItem {
    Integer getItemId();
    String getItemName();
    Double getItemPrice();
    String getItemDescription();
    List getItemAddOnList();
}
