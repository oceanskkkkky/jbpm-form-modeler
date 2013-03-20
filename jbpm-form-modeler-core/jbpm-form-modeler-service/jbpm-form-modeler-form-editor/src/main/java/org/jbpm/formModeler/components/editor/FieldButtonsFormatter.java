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
package org.jbpm.formModeler.components.editor;

import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class FieldButtonsFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FieldButtonsFormatter.class.getName());

    private String editIcon = "/formModeler/components/WysiwygFormEdit/buttons/edit.png";
    private String downIcon = "/formModeler/components/WysiwygFormEdit/buttons/down.png";
    private String firstIcon = "/formModeler/components/WysiwygFormEdit/buttons/first.png";
    private String lastIcon = "/formModeler/components/WysiwygFormEdit/buttons/last.png";
    private String moveIcon = "/formModeler/components/WysiwygFormEdit/buttons/move.png";
    private String trashIcon = "/formModeler/components/WysiwygFormEdit/buttons/trash.png";
    private String upIcon = "/formModeler/components/WysiwygFormEdit/buttons/up.png";

    private WysiwygFormEditor editor;
    private URLMarkupGenerator urlMarkupGenerator;

    public WysiwygFormEditor getEditor() {
        return editor;
    }

    public void setEditor(WysiwygFormEditor editor) {
        this.editor = editor;
    }

    public URLMarkupGenerator getUrlMarkupGenerator() {
        return urlMarkupGenerator;
    }

    public void setUrlMarkupGenerator(URLMarkupGenerator urlMarkupGenerator) {
        this.urlMarkupGenerator = urlMarkupGenerator;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        Field formField = (Field) getParameter("field");

        Boolean hideMotionButtons = (Boolean) getParameter("hideMotionButtons");

        if (formField != null) {
            try {
                if (!Boolean.TRUE.equals(hideMotionButtons)) {
                    int formSize = getEditor().getCurrentEditForm().getFormFields().size();
                    if (formField.getPosition() != 0) {
                        //Move first for non-first field
                        renderMoveButton(formField, getFirstIcon(), "moveFirst", "moveFirst", httpServletRequest);

                    }

                    if (formSize > 1) {
                        setAttribute("icon", getMoveIcon());
                        setAttribute("position", formField.getPosition());
                        setAttribute("grouped", Boolean.TRUE.equals(formField.getGroupWithPrevious()));
                        renderFragment("outputSelectField");
                    }

                    if (formField.getPosition() < formSize - 1) {
                        boolean isGroupedWithPrevious = false;
                        for (Field nextField : formField.getForm().getFormFields()) {
                            if (nextField.getPosition() == 1 + formField.getPosition()) {
                                isGroupedWithPrevious = Boolean.TRUE.equals(nextField.getGroupWithPrevious());
                                break;
                            }
                        }
                        //Move last for non-last field
                        renderMoveButton(formField, getLastIcon(), "moveLast", "moveLast", httpServletRequest);
                    }

                    if (!Boolean.TRUE.equals(hideMotionButtons)) {
                        setAttribute("position", formField.getPosition());
                        if (Boolean.TRUE.equals(formField.getGroupWithPrevious())) {
                            renderMoveButton(formField, getDownIcon(), "unGroupWithPrevious", "unGroupWithPrevious", httpServletRequest);
                        } else if (formField.getPosition() != 0) {
                            renderMoveButton(formField, getUpIcon(), "groupWithPrevious", "groupWithPrevious", httpServletRequest);
                        }
                    }
                }
                setAttribute("icon", getEditIcon());
                setAttribute("position", formField.getPosition());
                setAttribute("buttonId", "EditField_BTN_" + formField.getPosition());
                renderFragment("outputEdit");

                if (!Boolean.TRUE.equals(hideMotionButtons)) {
                    setAttribute("icon", getTrashIcon());
                    setAttribute("position", formField.getPosition());
                    renderFragment("outputDelete");
                }
            } catch (Exception e) {
                log.error("Error: ", e);
                throw new FormatterException(e);
            }
        }
    }

    protected void renderMoveButton(Field formField, String icon, String action, String msgId, HttpServletRequest request) {
        setAttribute("position", formField.getPosition());
        setAttribute("msgId", msgId);
        setAttribute("icon", icon);
        Map paramsMap = new HashMap();
        paramsMap.put("position", String.valueOf(formField.getPosition()));
        String actionUrl = urlMarkupGenerator.getMarkup(editor.getBeanName(), action, paramsMap);
        setAttribute("actionUrl", actionUrl);
        setAttribute("buttonId", action + "_BTN_" + formField.getPosition());
        renderFragment("outputMoveField");
    }

    public String getEditIcon() {
        return editIcon;
    }

    public void setEditIcon(String editIcon) {
        this.editIcon = editIcon;
    }

    public String getDownIcon() {
        return downIcon;
    }

    public void setDownIcon(String downIcon) {
        this.downIcon = downIcon;
    }

    public String getFirstIcon() {
        return firstIcon;
    }

    public void setFirstIcon(String firstIcon) {
        this.firstIcon = firstIcon;
    }

    public String getLastIcon() {
        return lastIcon;
    }

    public void setLastIcon(String lastIcon) {
        this.lastIcon = lastIcon;
    }

    public String getMoveIcon() {
        return moveIcon;
    }

    public void setMoveIcon(String moveIcon) {
        this.moveIcon = moveIcon;
    }

    public String getTrashIcon() {
        return trashIcon;
    }

    public void setTrashIcon(String trashIcon) {
        this.trashIcon = trashIcon;
    }

    public String getUpIcon() {
        return upIcon;
    }

    public void setUpIcon(String upIcon) {
        this.upIcon = upIcon;
    }
}