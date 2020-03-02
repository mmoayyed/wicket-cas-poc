package com.example.demo;

import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.util.AsciiArtUtils;
import org.apereo.cas.util.DateTimeUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;

@Configuration
public class CasWicketConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @EventListener
    public void handleApplicationReadyEvent(final ApplicationReadyEvent event) {
        AsciiArtUtils.printAsciiArtReady(log, StringUtils.EMPTY);
        log.info("Ready to process requests @ [{}]", DateTimeUtils.zonedDateTimeOf(event.getTimestamp()));
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
}
