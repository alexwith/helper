package me.hyfe.helper.menu.scheme;

import me.hyfe.helper.menu.item.Item;

public interface SchemeMapping {

    Item get(int key);

    boolean hasMappingFor(int key);

    SchemeMapping copy();
}