<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jbpm.formModeler.service.bb.commons.config.LocaleManager"%>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String namespace = request.getParameter("namespace");
    String formId = request.getParameter("formId");
    String editorBgColor = "#eaeaea";
%>
<mvc:formatter name="org.jbpm.formModeler.components.editor.WysiwygFormFormatter">
<mvc:formatterParam name="formId" value="<%=formId%>"/>
<mvc:formatterParam name="namespace" value="<%=namespace%>"/>
<%-- Form preview --%>
<mvc:fragment name="outputForm">

<script defer>
    var changeEnabled = true;
    var selectedDiv = "";
    var selectedField = -1;
    var dropableAreas = [];

    function addArea (areaId) {
        dropableAreas.push("#"+areaId);
    }

    function showAreas(divName, position, grouped) {
        $.each(dropableAreas, function(index, item) {
            var previous = position - 1;
            if (!item.startsWith(divName + position) && !(grouped && item == divName + (previous) + "_right") ) $(item).show();
            else if (grouped && item == divName + position + "_bottom") $(item).show();
        });
    }

    function hideAreas() {
        $.each(dropableAreas, function(index, item) {
            $(item).hide();
        });
    }

    function selectField(position, divName, grouped) {
        var divId = divName+position;

        hideAreas();

        var unselect = divId == selectedDiv;

        if (selectedDiv) {
            var div = $(selectedDiv);
            selectedDiv = '';
            div.backgroundColor = '';
            disableMenuForItem(div);
        }

        if (unselect) {
            position = -1;
            selectedDiv = '';
            return;
        }

        selectedDiv = divId;
        selectedField = position;
        var div = $(selectedDiv);
        div.backgroundColor = '#FEE48B';

        showAreas(divName, position, grouped);
    }

    function moveField(newPosition, modifier) {
        if (newPosition == selectedField && modifier != '<%=WysiwygFormEditor.BOTTOM_FIELD_MODIFIER%>') {
            return;
        }

        var promote = newPosition < selectedField
        if (promote) {
            if (modifier == '<%=WysiwygFormEditor.RIGHT_FIELD_MODIFIER%>' || modifier == '<%=WysiwygFormEditor.BOTTOM_FIELD_MODIFIER%>') newPosition ++;
        } else {
            if (modifier == '<%=WysiwygFormEditor.LEFT_FIELD_MODIFIER%>') newPosition --;
        }
        $("#<factory:encode name="selectedField"/>").val(selectedField);
        $("#<factory:encode name="newPosition"/>").val(newPosition);
        $("#<factory:encode name="modifier"/>").val(modifier);
        $("#<factory:encode name="promote"/>").val(promote);
        submitAjaxForm($("#<factory:encode name="changeFieldPositionForm"/>").get(0));
    }

    function overDestinationArea (area) {
        area.style.background = '#DE9C93';
    }

    function outDestinationArea (area) {
        area.style.background = '';
    }

    function buttonsForField (divElement, active) {
        var itemId = "#" + divElement.id + "_child";
        if (active)  $(itemId).show();
        else  $(itemId).hide();
    }

    function enableMenuForItem(divElement, force) {
        if(!force && !changeEnabled) return true;
        buttonsForField(divElement, true);
        divElement.style.border='dotted #FCC917 2px';
        divElement.style.margin='0px';
        divElement.style.zIndex='5000';
    }

    function disableMenuForItem(divElement) {
        changeEnabled = true;
        buttonsForField(divElement, false);
        if (divElement.id == selectedDiv) return;
        divElement.style.border='none';
        divElement.style.margin='2px';
        divElement.style.zIndex='0'
        divElement.style.background = '';
    }

    function highlightLastMovedElement() {
        var movedPosition = <factory:property property="lastMovedFieldPosition" />;
        if( movedPosition >= 0 ){
            var movedId = "<factory:encode name="formMenuDiv"/>"+movedPosition;
            var movedDiv = $(movedId);
            if ( movedDiv ){
                movedDiv.style.background = '#FEE48B';
                enableMenuForItem(movedDiv, true);
                setTimeout("disableMenuForItem($('"+movedId+"'))",500);
            } else {
                //alert("I cant find div with id "+movedId);
            }
        }
    }

    setTimeout('highlightLastMovedElement()',1);

</script>
<style type="text/css">
    .horizontal_drop_area {
        cursor: pointer;
        height:10px;
        width:50px;
        border: dotted #BD3826 1px;
        margin:2px;
        display:none;
    }

    .vertical_drop_area {
        cursor: pointer;
        height:40px;
        max-height:50px;
        min-width:10px;
        width:10px;
        z-index:2000;
        border: dotted #BD3826 1px;
        margin:2px;
        display:none;
    }

</style>

<div style='overflow: hidden; height:100%; border:solid 1px #CCCCCC; background-color: <%=editorBgColor%>;'>
    <form id="<factory:encode name="changeFieldPositionForm"/>" action="<factory:formUrl/>" method="post">
        <factory:handler action="moveField"/>
        <input type="hidden" id="<factory:encode name="selectedField"/>" name="selectedField" value="">
        <input type="hidden" id="<factory:encode name="newPosition"/>" name="newPosition" value="">
        <input type="hidden" id="<factory:encode name="modifier"/>" name="modifier" value="">
        <input type="hidden" id="<factory:encode name="promote"/>" name="promote" value="">
    </form>
    <script type="text/javascript" defer="defer">
        setAjax("<factory:encode name="changeFieldPositionForm"/>");
    </script>
    <div style="width:100%; height:450px; overflow:scroll; position: relative;">
        <mvc:fragmentValue name="form" id="form">
            <mvc:fragmentValue name="namespace" id="formNamespace">
                <mvc:fragmentValue name="displayMode" id="displayMode">
                    <mvc:fragmentValue name="renderMode" id="renderMode">
                        <div style='width:90%; height:100%; margin: 5px;' id="<factory:encode name="wysiwygFormTable"/>">
                            <form action="#" onsubmit="return false;" style="margin:0px;" >
                                <mvc:formatter name="org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter">
                                    <mvc:formatterParam name="form" value="<%=form%>"/>
                                    <mvc:formatterParam name="namespace" value="<%=formNamespace%>"/>
                                    <mvc:formatterParam name="displayMode" value="<%=displayMode%>"/>
                                    <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                    <mvc:fragment name="outputStart">
                                        <table border="0" cellspacing="0" cellpadding="0"  width="<mvc:fragmentValue name="width"/>">
                                    </mvc:fragment>
                                    <mvc:fragment name="groupStart">
                                        <mvc:fragmentValue name="isFirst" id="isFirst">
                                            <%
                                                if (Boolean.TRUE.equals(isFirst)) {
                                            %>
                                            <tr style="width:100%" height="100%">
                                                <td>
                                                    <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_top"
                                                         colspan="<mvc:fragmentValue name="colspan"/>" width="<mvc:fragmentValue name="width"/>" class="horizontal_drop_area"
                                                         onclick="moveField(<mvc:fragmentValue name="field/position"/>, '<%=WysiwygFormEditor.TOP_FIELD_MODIFIER%>');"
                                                         onmouseover="overDestinationArea(this);"
                                                         onmouseout="outDestinationArea(this);">
                                                        &nbsp;
                                                    </div>
                                                    <script defer="defer">
                                                        addArea("<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_top");
                                                    </script>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                            <tr style="width:100%" height="100%">
                                        </mvc:fragmentValue>
                                    </mvc:fragment>
                                    <mvc:fragment name="beforeInputElement">
                                        <mvc:fragmentValue name="index" id="index">
                                            <td colspan="<mvc:fragmentValue name="colspan"/>" width="<mvc:fragmentValue name="width"/>" nowrap="nowrap" align="left" style="height:100%;  width:100%;" valign="top">
                                            <table border="0" cellspacing="0" cellpadding="0" style="display:block; width:100%; height:100%;">
                                            <tr>
                                            <%
                                                if (index != null && Integer.decode(index.toString()).intValue() == 0) {
                                            %>
                                            <td valign="top" style="width: 0px; overflow: visible;">
                                                <div class="vertical_drop_area"
                                                     id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_left"
                                                     onclick="moveField(<mvc:fragmentValue name="field/position"/>, '<%=WysiwygFormEditor.LEFT_FIELD_MODIFIER%>');"
                                                     onmouseover="overDestinationArea(this);"
                                                     onmouseout="outDestinationArea(this);">
                                                    &nbsp;
                                                </div>
                                                <script defer="defer">
                                                    addArea("<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_left");
                                                </script>&nbsp;
                                            </td>
                                            <%
                                            } else {
                                            %>
                                            <td style="width: 0px;"></td>
                                            <%
                                                }
                                            %>
                                            <td width="*" valign="top">
                                            <%-- Div with dotted border --%>
                                            <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>"
                                            style="position:relative; height:100%; width:100%;z-index:2000; overflow:visible; display:block; margin: 2px;"
                                            onmouseover="enableMenuForItem(this,false)"
                                            onmouseout="disableMenuForItem(this)">
                                            <%-- Div with field buttons --%>
                                            <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_child"
                                                 style="position:absolute; top:-10px; left:0px; z-index:5000; text-align:left; padding:5px; display:none" align="left">
                                                <mvc:fragmentValue name="field" id="field">
                                                    <% Boolean hideMotionButtons = Boolean.FALSE; %>
                                                    <%@ include file="buttonActions.jsp" %>
                                                </mvc:fragmentValue>
                                            </div>
                                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                            <tr>
                                        </mvc:fragmentValue>
                                    </mvc:fragment>

                                    <mvc:fragment name="beforeFieldInTemplateMode">
                                        <%-- Div with dotted border --%>
                                        <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>"
                                        style="position:relative; height:100%; width:100%; z-index:2000; overflow:visible;display:block; margin: 2px;"
                                        onmouseover="enableMenuForItem(this, false)"
                                        onmouseout="disableMenuForItem(this)">
                                        <%-- Div with field buttons --%>
                                        <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_child"
                                             style="position:absolute; top:-10px; left:0px; z-index:5000; text-align:left; padding:5px; display:none" align="left">
                                            <mvc:fragmentValue name="field" id="field">
                                                <% Boolean hideMotionButtons = Boolean.TRUE; %>
                                                <%@ include file="buttonActions.jsp" %>
                                            </mvc:fragmentValue>
                                        </div>
                                    </mvc:fragment>

                                    <mvc:fragment name="afterFieldInTemplateMode"><div style="height:2px;"></div></div></mvc:fragment>
                                    <mvc:fragment name="beforeLabel"><td valign="top" nowrap width="1%"></mvc:fragment>
                                    <mvc:fragment name="afterLabel"></td></mvc:fragment>
                                    <mvc:fragment name="lineBetweenLabelAndField"></tr><tr></mvc:fragment>
                                    <mvc:fragment name="beforeField"><td valign="top" style="height: 2px; overflow:visible;"></mvc:fragment>
                                    <mvc:fragment name="afterField"></td></mvc:fragment>
                                    <mvc:fragment name="afterInputElement">
                                        </tr>
                                        </table>
                                        </div>
                                        </td>
                                        <td valign="top" style="width: 0px; overflow: visible;">
                                            <div class="vertical_drop_area"
                                                 id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_right"
                                                 onclick="moveField(<mvc:fragmentValue name="field/position"/>, '<%=WysiwygFormEditor.RIGHT_FIELD_MODIFIER%>');"
                                                 onmouseover="overDestinationArea(this);"
                                                 onmouseout="outDestinationArea(this);">
                                                &nbsp;
                                            </div>
                                            <script defer="defer">
                                                addArea("<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_right");
                                            </script>
                                        </td>
                                        </tr>
                                        </table>
                                        </td>
                                    </mvc:fragment>
                                    <mvc:fragment name="groupEnd">
                                        </tr>
                                        <tr style="width:100%" height="100%">
                                            <td >
                                                <div id="<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_bottom"
                                                     colspan="<mvc:fragmentValue name="colspan"/>" class="horizontal_drop_area"
                                                     onclick="moveField(<mvc:fragmentValue name="field/position"/>, '<%=WysiwygFormEditor.BOTTOM_FIELD_MODIFIER%>');"
                                                     onmouseover="overDestinationArea(this);"
                                                     onmouseout="outDestinationArea(this);">

                                                </div>
                                                <script defer="defer">
                                                    addArea("<factory:encode name="formMenuDiv"/><mvc:fragmentValue name="field/position"/>_bottom");
                                                </script>
                                            </td>
                                        </tr>
                                    </mvc:fragment>
                                    <mvc:fragment name="outputEnd"></table></mvc:fragment>
                                </mvc:formatter>
                            </form>
                        </div>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </div>
</div>
<mvc:fragmentValue name="showDisplayWarningMessage" id="showDisplayWarningMessage">
    <%if(Boolean.TRUE.equals(showDisplayWarningMessage)){%>
    <mvc:fragmentValue name="message" id="message">
             <span class="skn-error">
                 <i18n:message key="<%=(String)message%>"></i18n:message>
             </span>
    </mvc:fragmentValue>
    <%}%>
</mvc:fragmentValue>

</mvc:fragment>
</mvc:formatter>


<form action="<factory:formUrl/>" id="refreshForm" method="POST">
    <factory:handler  action="void"/>
</form>
<script defer="true">
    setAjax("refreshForm");

    var formMustBeRefreshed = false;
    refreshDDMForm = function(){
        if(FX){
            setTimeout('refreshDDMFormTimer()',100);
            formMustBeRefreshed = true;
        }
        else {
            submitAjaxForm(document.getElementById('refreshForm'));
        }
    }
    function refreshDDMFormTimer() {
        if(formMustBeRefreshed) {
            submitAjaxForm(document.getElementById('refreshForm'));
            formMustBeRefreshed = false;
        }
    }
</script>