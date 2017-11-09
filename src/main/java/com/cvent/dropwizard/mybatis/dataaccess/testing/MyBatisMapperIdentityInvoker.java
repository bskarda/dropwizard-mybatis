package com.cvent.dropwizard.mybatis.dataaccess.testing;

import com.cvent.dropwizard.mybatis.dataaccess.MyBatisMapperInvoker;

import java.util.function.Function;

/**
 * This class has the same intent as IdentityWrappedInvoker, but for those who need concrete instances of
 * MyBatisMapperInvoker because of the invoke overload that takes the environment argument.
 * @param <TMapper>
 */
public class MyBatisMapperIdentityInvoker<TMapper> extends MyBatisMapperInvoker<TMapper> {
    private final TMapper mapper;

    public MyBatisMapperIdentityInvoker(TMapper mapper) {
        super(null, null);
        this.mapper = mapper;
    }

    @Override
    public <TResult> TResult invoke(Function<TMapper, TResult> function) {
        return invoke(function, null);
    }

    @Override
    public <TResult> TResult invoke(Function<TMapper, TResult> function, String environment) {
        return function.apply(mapper);
    }
}
