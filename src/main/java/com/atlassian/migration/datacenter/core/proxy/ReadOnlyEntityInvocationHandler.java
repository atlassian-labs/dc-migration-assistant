/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
