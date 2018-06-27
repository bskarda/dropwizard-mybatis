package com.cvent.dropwizard.mybatis.objectfactories;

/**
 * A mutable test class
 */
public class MutableTestClass {

    private int value;

    public MutableTestClass() {
        value = 3;
    }

    public MutableTestClass(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
