package com.cvent.dropwizard.mybatis.objectfactories;

/**
 * An immutable test class
 */
public final class ImmutableTestClass {

    private int value;

    private ImmutableTestClass() {

    }

    private ImmutableTestClass(int value) {
        this.value = value;
    }

    public static ImmutableTestClass of(int value) {
        return new ImmutableTestClass(value);
    }

    public int getValue() {
        return value;
    }

}
