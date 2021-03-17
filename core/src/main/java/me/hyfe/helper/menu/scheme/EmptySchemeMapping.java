package me.hyfe.helper.menu.scheme;

import me.hyfe.helper.menu.item.Item;

public class EmptySchemeMapping implements SchemeMapping {

    public Item get(int key) {
        return null;
    }

    @Override
    public boolean hasMappingFor(int key) {
        return false;
    }

    @Override
    public SchemeMapping copy() {
        return this;
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }
}