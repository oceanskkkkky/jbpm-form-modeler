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
package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.api.config.FieldTypeManager;
import org.jbpm.formModeler.api.config.builders.FieldTypeBuilder;
import org.jbpm.formModeler.api.config.builders.DecoratorFieldTypeBuilder;
import org.jbpm.formModeler.api.config.builders.SimpleFieldTypeBuilder;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.processing.PropertyDefinition;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
@ApplicationScoped
public class FieldTypeManagerImpl implements FieldTypeManager {

    private List<FieldType> fieldTypes;
    private List<FieldType> decoratorTypes;

    @Inject
    protected Instance<SimpleFieldTypeBuilder> builders;
    @Inject
    protected Instance<DecoratorFieldTypeBuilder> decoratorBuilders;

    private Map<String, String> iconsMappings = new HashMap<String, String>();
    private String defaultIcon = "fieldTypes/button.gif";

    @PostConstruct
    protected void init() {

        fieldTypes = new ArrayList<FieldType>();
        decoratorTypes = new ArrayList<FieldType>();

        for (FieldTypeBuilder builder : builders) {
            fieldTypes.addAll(builder.buildList());
        }

        for (FieldTypeBuilder builder : decoratorBuilders) {
            decoratorTypes.addAll(builder.buildList());
        }

        iconsMappings.put("InputTextInteger", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextIBAN", "fieldTypes/box_number.png");
        iconsMappings.put("Currency", "fieldTypes/currency.gif");
        iconsMappings.put("Separator", "fieldTypes/splitter_box.png");
        iconsMappings.put("InputTextLong", "fieldTypes/box_number.png");
        iconsMappings.put("File", "fieldTypes/attachments.png");
        iconsMappings.put("multipleFiles", "fieldTypes/attachments.png");
        iconsMappings.put("I18nFile", "fieldTypes/attachments.png");
        iconsMappings.put("InputDate", "fieldTypes/date_selector.png");
        iconsMappings.put("I18nHTMLText", "fieldTypes/rich_text_box.png");
        iconsMappings.put("Link", "fieldTypes/hyperlink.png");
        iconsMappings.put("Image", "fieldTypes/image.gif");
        iconsMappings.put("multipleImages", "fieldTypes/image.gif");
        iconsMappings.put("I18nImage", "fieldTypes/image.gif");
        iconsMappings.put("HTMLEditor", "fieldTypes/rich_text_box.png");
        iconsMappings.put("InputTextCIF", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextPhone", "fieldTypes/phone_box.png");
        iconsMappings.put("InputTextArea", "fieldTypes/scroll_zone.png");
        iconsMappings.put("I18nTextArea", "fieldTypes/scroll_zone.png");
        iconsMappings.put("CheckBox", "fieldTypes/checkbox.png");
        iconsMappings.put("InputShortDate", "fieldTypes/date_selector.png");
        iconsMappings.put("InputTextNIF", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextCCC", "fieldTypes/box_number.png");
        iconsMappings.put("I18nText", "fieldTypes/textbox.png");
        iconsMappings.put("InputTextDouble", "fieldTypes/box_number.png");
        iconsMappings.put("HTMLLabel", "fieldTypes/rich_text_box.png");
        iconsMappings.put("InputText", "fieldTypes/textbox.png");
        iconsMappings.put("InputTextCP", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextEmail", "fieldTypes/mailbox.png");
        iconsMappings.put("checkboxMultiple", "fieldTypes/listbox.png");
        iconsMappings.put("radio", "fieldTypes/radiobutton.gif");
        iconsMappings.put("subform", "fieldTypes/master_details.gif");
        iconsMappings.put("select", "fieldTypes/dropdown_listbox.gif");
        iconsMappings.put("selectMultiple", "fieldTypes/listsbox.gif");
        iconsMappings.put("versionSubform", "fieldTypes/master_details.gif");
        iconsMappings.put("editorVersionSubform", "fieldTypes/master_details.gif");
        iconsMappings.put("subformMultiple", "fieldTypes/master_details.gif");
        iconsMappings.put("FreeText", "fieldTypes/textbox.png");
    }
    
    @Override
    public String getDefaultIcon() {
        return defaultIcon;
    }

    @Override
    public void setDefaultIcon(String defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    @Override
    public Map<String, String> getIconsMappings() {
        return iconsMappings;
    }

    @Override
    public void setIconsMappings(Map<String, String> iconsMappings) {
        this.iconsMappings = iconsMappings;
    }

    @Override
    public List<FieldType> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public void setFieldTypes(List<FieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    /**
     * Return FieldType's for given manager class
     *
     * @param managerClass
     * @return existing FieldType's for given manager class
     * @throws Exception
     */
    @Override
    public List<FieldType> getSuitableFieldTypes(String managerClass) throws Exception {
        final List validFieldTypes = new ArrayList();

        for (FieldType fType : fieldTypes) {
            if (fType.getManagerClass().equals(managerClass)) validFieldTypes.add(fType);
        }
        return validFieldTypes;
    }

    /**
     * Get all fieldtypes suitable to generate a value of the given class.
     *
     * @param propertyName   Property name
     * @param propDefinition Expected property definition that the field type should generate.
     * @return A list of FieldType objects suitable to generate a value of the given class.
     * @throws Exception in case of error
     */
    public List<FieldType> getSuitableFieldTypes(String propertyName, PropertyDefinition propDefinition) throws Exception {
        final List<FieldType> validFieldTypes = new ArrayList<FieldType>();
        if (propDefinition != null) {

            for (FieldType fieldType: fieldTypes) {
                if (fieldType.getFieldClass().equals(propDefinition.getPropertyClassName())) validFieldTypes.add(fieldType);
            }
        }
        return validFieldTypes;
    }

    @Override
    public List getFormDecoratorTypes() throws Exception {
        return decoratorTypes;
    }

    @Override
    public FieldType getTypeByCode(String typeCode) throws Exception {
        return getTypeByCode(typeCode, true);
    }

    @Override
    public FieldType getTypeByCode(String typeCode, boolean tryToCreateTypes) throws Exception {

        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getCode().equals(typeCode)) return fieldType;
        }

        for (FieldType decorator : decoratorTypes) {
            if (decorator.getCode().equals(typeCode)) return decorator;
        }

        return null;
    }

    @Override
    public FieldType getTypeByClass(String className){
        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getFieldClass().equals(className)) return fieldType;
        }
        return null;
    }

    @Override
    public String getIconPathForCode(String code) {
        if (code == null) {
            Logger.getLogger(FormManagerImpl.class.getName()).log(Level.SEVERE, "Retrieving icon for field type with code null. All types must have a code.");
            return defaultIcon;
        }
        String s = getIconsMappings().get(code);
        if (s == null) {
            return defaultIcon;
        }
        return s;
    }

    public static FieldTypeManagerImpl lookup() {
        return (FieldTypeManagerImpl) CDIHelper.getBeanByType(FieldTypeManagerImpl.class);
    }

}
