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
<%@ page import="org.jbpm.formModeler.service.bb.mvc.Framework" %>
<%@ page import="org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<div id="<%=Framework.AJAX_AREA_PREFFIX + "main_component"%>">
    <factory:useComponent bean="org.jbpm.formModeler.service.mvc.components.CurrentComponentRenderer"/>
</div>