package org.svids.tbankcooldownapi.entity;

public enum PurchaseCategory {
    ELECTRONICS("Электроника"),
    CLOTHING("Одежда"),
    FOOD("Еда"),
    HOME("Хозтовары"),
    OTHER("Другое");

    final String name;

    PurchaseCategory(String name) {
        this.name = name;
    }
}
