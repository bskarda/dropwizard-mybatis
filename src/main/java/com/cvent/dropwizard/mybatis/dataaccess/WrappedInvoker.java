package com.cvent.dropwizard.mybatis.dataaccess;

import java.util.function.Function;

/**
 * This is a higher order function that allows implementations to manufacture instances of T and execute Functions
 * using them. See the MybatisMapperInvoker for an example.
 * @param <T>
 */
public interface WrappedInvoker<T> {
    /**
     * Your implementation of this method should manufacture or reuse an instance of T, then apply function to it
     * @param function Your function that will execute given an instance of T
     * @param <TResult> The return type of your function
     * @return Usually this this just the value of function.apply(yourInstanceHere)
     */
    <TResult> TResult invoke(Function<T, TResult> function);
}
