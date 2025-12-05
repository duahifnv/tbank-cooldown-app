package org.svids.tbankcooldownapi.entity;

public enum ItemCategoryType {
    ELECTRONICS("Электроника"),
    CLOTHING("Одежда"),
    FOOD("Еда"),
    HOME("Хозтовары"),
    OTHER("Другое");

    final String name;

    ItemCategoryType(String name) {
        this.name = name;
    }
}
