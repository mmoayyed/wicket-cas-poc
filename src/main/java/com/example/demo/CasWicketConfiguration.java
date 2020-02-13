package com.example.demo;

import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.util.AsciiArtUtils;
import org.apereo.cas.util.DateTimeUtils;
import org.apereo.cas.validation.CasProtocolViewFactory;

import com.example.demo.pages.CasLoginPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class CasWicketConfiguration {
    @Bean
    public CasProtocolViewFactory casProtocolViewFactory() {
        return new WicketCasProtocolViewFactory();
    }

    @EventListener
    public void handleApplicationReadyEvent(final ApplicationReadyEvent event) {
        AsciiArtUtils.printAsciiArtReady(log, StringUtils.EMPTY);
        log.info("Ready to process requests @ [{}]", DateTimeUtils.zonedDateTimeOf(event.getTimestamp()));
    }

    @Bean
    public ViewResolver wicketViewResolver() {
        return new WicketViewResolver();
    }

    @Bean
    public List inMemoryRegisteredServices() {
        var service = new RegexRegisteredService();
        service.setId(1);
        service.setServiceId(".*");
        service.setName("Everything");
        service.setDescription("Every URL is allowed by this definition");
        return List.of(service);
    }

    public static class WicketViewResolver extends AbstractTemplateViewResolver {
        public WicketViewResolver() {
            this.setViewClass(this.requiredViewClass());
        }

        public WicketViewResolver(String prefix, String suffix) {
            this();
            this.setPrefix(prefix);
            this.setSuffix(suffix);
        }

        protected Class<?> requiredViewClass() {
            return WicketView.class;
        }
    }

    public static class WicketView extends AbstractTemplateView {
        @Override
        protected void renderMergedTemplateModel(final Map<String, Object> map,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) throws Exception {
            ServletWebRequest webRequest = new ServletWebRequest(request, "");
            ServletWebResponse webResponse = new ServletWebResponse(webRequest, response);
            RequestCycleContext context = new RequestCycleContext(webRequest, webResponse,
                new BookmarkableMapper(),
                new DefaultExceptionMapper());
            ThreadContext.setRequestCycle(new RequestCycle(context));
            PageParameters parameters = new PageParameters();
            parameters.set("flowExecutionKey", map.get("flowExecutionKey"));
            CasLoginPage page = new CasLoginPage(parameters);
            page.setViewModule(map);
            PageProvider pageProvider = new PageProvider(page);
            CharSequence result = ComponentRenderer.renderPage(pageProvider);
            try (var writer = new BufferedWriter(response.getWriter())) {
                String html = result.toString();
                writer.write(html);
                writer.flush();
            }

        }
    }

    private static class WicketCasProtocolViewFactory implements CasProtocolViewFactory {
        @Override
        public View create(final ConfigurableApplicationContext applicationContext,
                           final String viewName,
                           final String contentType) {
            return null;
        }
    }
}
