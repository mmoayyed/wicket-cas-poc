package com.example.demo;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import org.apache.wicket.markup.html.WebPage;
import org.wicketstuff.annotation.mount.MountPath;

@WicketHomePage
@MountPath("home")
public class HomePage extends WebPage {
}
