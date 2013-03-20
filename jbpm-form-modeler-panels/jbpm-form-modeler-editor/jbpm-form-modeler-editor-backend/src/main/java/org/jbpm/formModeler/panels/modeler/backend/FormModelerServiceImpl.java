package org.jbpm.formModeler.panels.modeler.backend;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.util.helpers.EditorHelper;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.editor.model.FormTO;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.commons.io.IOService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.menu.Menus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Service
@ApplicationScoped
public class FormModelerServiceImpl implements FormModelerService {


    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    private Menus menus;

    @Override
    public List<FormTO> getAllForms() {
        List<FormTO> result = new ArrayList<FormTO>();
        for (Form form : formManager.getAllForms()) {
            if (form.isVisibleStatus()) result.add(new FormTO(form.getId(), form.getName()));
        }
        return result;
    }

    @Override
    public Long setFormId(Long formId) {
        EditorHelper helper = getHelper();

        if (helper != null) {
            helper.setOriginalForm(formId);
            helper.setFormToEdit(formManager.cloneForm(formId));
            return formId;
        }

        return null;
    }

    @Override
    public Long loadForm(Path context) {
        org.kie.commons.java.nio.file.Path kiePath = paths.convert( context );

        String xml = ioService.readAllString(kiePath).trim();
        Form form = formSerializationManager.loadFormFromXML(xml);

        EditorHelper helper = getHelper();
        helper.setFormToEdit(form);

        return form.getId();
    }

    @Override
    public FormTO getCurrentForm() {
        EditorHelper helper = getHelper();
        formManager.replaceForm(helper.getOriginalForm(), helper.getFormToEdit());
        clearHelper();
        return new FormTO(helper.getFormToEdit().getId(), helper.getFormToEdit().getName());
    }

    private void clearHelper() {
        RpcContext.getHttpSession().removeAttribute("EditorHelper");
    }

    protected EditorHelper getHelper() {
        EditorHelper helper = (EditorHelper) RpcContext.getHttpSession().getAttribute("EditorHelper");

        if (helper == null) helper = new EditorHelper();

        RpcContext.getHttpSession().setAttribute("EditorHelper", helper);

        return helper;
    }

    @Override
    public void saveForm(Path context) {
        EditorHelper helper = getHelper();

        formManager.replaceForm(helper.getOriginalForm(), helper.getFormToEdit());

        org.kie.commons.java.nio.file.Path kiePath = paths.convert(context);
        ioService.write(kiePath, formSerializationManager.generateFormXML(helper.getFormToEdit()));
    }

    @Override
    public Path createForm(Path context, String formName) {
        org.kie.commons.java.nio.file.Path kiePath = paths.convert(context ).resolve(formName);

        ioService.createFile(kiePath);

        Form form = formManager.createForm(formName);

        ioService.write(kiePath, formSerializationManager.generateFormXML(form));

        if(getHelper()!=null){
            getHelper().setFormToEdit(form);
            getHelper().setOriginalForm(form.getId());
        }

        //setFormId(form.getId());

        final Path path = paths.convert(kiePath, false);

        return path;
    }
}