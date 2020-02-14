package com.example.demo.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePage extends WebPage {

    private MarkupContainer defaultModal;
    private Map<String, Object> viewModel = new HashMap<>();

    public BasePage(PageParameters params, Map<String, Object> viewModel){
        super(params);
        this.viewModel = viewModel;
        initPage();
    }

    public BasePage(){
        initPage();
    }

    private void initPage(){
        defaultModal = new EmptyPanel("defaultModal");
        defaultModal.setOutputMarkupId(true);
        add(defaultModal);
    }

    public void replaceDefaultModal(ModalWindow newModal){
        defaultModal.replaceWith(newModal);
        defaultModal = newModal;
        defaultModal.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getWicketAjaxReference()));
        String bootstrapPrefixPath = "bootstrap/4.4.1";
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference(bootstrapPrefixPath + "/js/bootstrap.js")));
        response.render(CssHeaderItem.forReference(new WebjarsJavaScriptResourceReference(bootstrapPrefixPath + "/css/bootstrap.css")));
    }

    public Map<String, Object> getViewModel() {
        return viewModel;
    }
}
