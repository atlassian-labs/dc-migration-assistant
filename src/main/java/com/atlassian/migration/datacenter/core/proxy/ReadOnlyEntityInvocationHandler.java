package com.atlassian.migration.datacenter.core.proxy;

import net.java.ao.Entity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class ReadOnlyEntityInvocationHandler<T extends Entity> implements InvocationHandler {

    private final T entity;

    public ReadOnlyEntityInvocationHandler(T entity) {
        this.entity = entity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.startsWith("set")) {
            throw new RuntimeException("Called a setter method on a read only instance of " + this.entity.getClass().getSimpleName());
        }

        return method.invoke(this.entity, args);
    }
}
