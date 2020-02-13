package com.example.demo;

import com.example.demo.pages.HomePage;
import com.giffing.wicket.spring.boot.starter.app.WicketBootStandardWebApplication;
import org.apache.wicket.Page;
import org.springframework.stereotype.Component;

@Component
public class WicketWebApplication extends WicketBootStandardWebApplication {
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}
