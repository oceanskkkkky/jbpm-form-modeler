/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.processing;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.processing.BindingManager;
import org.jbpm.formModeler.api.processing.PropertyDefinition;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import javax.enterprise.context.ApplicationScoped;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Field;

@ApplicationScoped
public class BindingManagerImpl implements BindingManager {
    @Override
    public PropertyDefinition getPropertyDefinition(FieldType type) throws Exception {
        PropertyDefinitionImpl def = new PropertyDefinitionImpl();

        def.setPropertyClass(Class.forName(type.getFieldClass()));

        return def;
    }

    @Override
    public PropertyDefinition getPropertyDefinition(String propertyName, String className) throws Exception{
        return getPropertyDefinition(propertyName, Class.forName(className));
    }

    @Override
    public PropertyDefinition getPropertyDefinition(String propertyName, Class clazz) throws Exception{

        try {
            PropertyDefinitionImpl def = new PropertyDefinitionImpl();
            Field field = clazz.getDeclaredField(propertyName);
            def.setPropertyClass(field.getType());
            return def;
        } catch (Exception e) {

        }

        return null;
    }

    public static final BindingManagerImpl lookup() {
        return (BindingManagerImpl) CDIHelper.getBeanByType(BindingManagerImpl.class);
    }
}