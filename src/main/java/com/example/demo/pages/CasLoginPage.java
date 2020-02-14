package com.example.demo.pages;

import org.apereo.cas.authentication.principal.Service;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Map;

public class CasLoginPage extends BasePage {

    public CasLoginPage(PageParameters parameters, Map<String, Object> viewModel) {
        super(parameters, viewModel);

        add(new RequiredTextField<String>("username"));
        add(new PasswordTextField("password"));

        var service = getViewModel().get("service");
        if (service != null) {
            var svc = Service.class.cast(service).getId();
            add(new Label("service", "The target application specified is " + svc));
        } else {
            add(new Label("service", "This request is not from a client application!"));
        }
        var flowExecutionKey = CasLoginPage.this.getPageParameters().get("flowExecutionKey").toOptionalString();
        add(new HiddenField<String>("execution", Model.of(flowExecutionKey)));
    }
}

