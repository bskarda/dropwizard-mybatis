package com.cvent.dropwizard.mybatis.objectfactories;

import com.cvent.dropwizard.mybatis.objectFactories.ImmutablesFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by jeremya on 11/8/2016.
 */
public class ImmutablesFactoryTest {

    private ImmutablesFactory factory;

    @BeforeEach
    public void initialize() {
        this.factory = new ImmutablesFactory();
    }

    @Test
    public void testDefaultConstructionShouldThrowForImmutables() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ImmutableTestClass.class));
    }

    @Test
    public void testDefaultConstructionShouldReturnInstanceForNonImmutables() {
        MutableTestClass created = factory.create(MutableTestClass.class);

        assertNotNull(created);
    }

    @Test
    public void testParameterConstructionShouldReturnInstanceForNonImmutables() {
        List<Class<?>> constructorArgTypes = Arrays.asList(
                int.class
        );

        List<Object> constructorArgs = Arrays.asList(
                2
        );

        MutableTestClass created = factory.create(MutableTestClass.class, constructorArgTypes, constructorArgs);

        assertEquals(created.getValue(), 2);
    }

    @Test
    public void testParameterConstructionShouldReturnInstanceForImmutables() {
        List<Class<?>> constructorArgTypes = Arrays.asList(
                int.class
        );

        List<Object> constructorArgs = Arrays.asList(
                2
        );

        ImmutableTestClass created = factory.create(ImmutableTestClass.class, constructorArgTypes, constructorArgs);

        assertEquals(created.getValue(), 2);
    }

    @Test
    public void testParameterConstructionShouldThrowWithInvalidParameterTypeForNonImmutables() {
        List<Class<?>> constructorArgTypes = Arrays.asList(
                String.class
        );

        List<Object> constructorArgs = Arrays.asList(
                "Blue"
        );

        assertThrows(Exception.class,
                () -> factory.create(MutableTestClass.class, constructorArgTypes, constructorArgs));
    }

    @Test
    public void testParameterConstructionShouldThrowWithInvalidParameterTypeForImmutables() {
        List<Class<?>> constructorArgTypes = Arrays.asList(
                String.class
        );

        List<Object> constructorArgs = Arrays.asList(
                "Blue"
        );

        assertThrows(Exception.class,
                () -> factory.create(ImmutableTestClass.class, constructorArgTypes, constructorArgs));
    }


}

