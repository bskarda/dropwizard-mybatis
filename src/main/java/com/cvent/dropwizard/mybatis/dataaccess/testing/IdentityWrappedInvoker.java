package com.cvent.dropwizard.mybatis.dataaccess.testing;

import com.cvent.dropwizard.mybatis.dataaccess.WrappedInvoker;

import java.util.function.Function;

/**
 * This is a simple class for testing classes that use instances of WrappedInvoker. You can pass a mock in the
 * constructor and get the mocked result right back by calling invoke.
 * @param <T>
 */
public class IdentityWrappedInvoker<T> implements WrappedInvoker<T> {
    private final T object;

    public IdentityWrappedInvoker(T object) {
        this.object = object;
    }

    @Override
    public <TResult> TResult invoke(Function<T, TResult> function) {
        return function.apply(object);
    }
}
