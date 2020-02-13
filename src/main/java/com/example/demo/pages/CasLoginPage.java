package com.example.demo.pages;

import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class CasLoginPage extends BasePage {

    public CasLoginPage(PageParameters parameters) {
        super(parameters);

        add(new RequiredTextField<String>("username"));
        add(new PasswordTextField("password"));

        var flowExecutionKey = CasLoginPage.this.getPageParameters().get("flowExecutionKey").toOptionalString();
        add(new HiddenField<String>("execution", Model.of(flowExecutionKey)));
    }
}

