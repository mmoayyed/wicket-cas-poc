# CAS POC

# Overview

This is a modest POC to demonstrate how a given CAS server can be *embedded* inside a Spring Boot application
to act as an identity provider while still presenting functionality and protocol support as a standalone CAS server would. 

# Versions

- CAS `6.2.x`
- JDK `11`

# Run

*For this demo to be a success, the very latest build of Apereo CAS `6.2.x` is required.*

Execute:

```bash
./run.sh
```

The application should be available under `http://localhost:8080/login`. To simulate a CAS protocol login request,
you may start with something like `http://localhost:8080/login?service=https://app.example.org`, login with the credentials
provided and be issued a service ticket request. 

Additional endpoints are also available depending on the availability of modules included in the build. For example, 
the OIDC discovery endpoint is provided at `http://localhost:8080/oidc/.well-known`.
Spring Actuator endpoints some of which are provided by CAS are also found at `http://localhost:8080/actuator`.

![image](https://user-images.githubusercontent.com/1205228/74520785-b2cd1d00-4f31-11ea-9e7d-6b4b9ab2c622.png)

# Design

## Baseline

The CAS server itself is composed to many individual modules each of which provide a specific type of functionality
that is then activated using Spring Boot's `@AutoConfiguration` strategy. Some modules are required (typically in the `core`)
category while others are optional and provide extra functionality (typically in the `support` category). By assembling bits and pieces
of the CAS ecosystem, one could design an identity provider with what is required for an access management deployment, much
like making a pizza!

For this demo, we have included several configuration modules
and extensions into the build the most important of which are:

- CAS protocol support
- SAML1 protocol support
- OIDC protocol support
- OAUTH protocol support
- Delegated AuthN
- Google Authenticator MFA
- LDAP Authentication
- Replying Party (i.e. Client Application) Management
- [Configuration management via REST](https://github.com/mmoayyed/cas-poc-restful-config)
- Many others...

Typically, adding additional functinality is as simple as including the module in the build. Once the module is included,
its behavior can be altered using Spring Cloud and `@RefreshScope`. Components in CAS that can be refreshed are marked 
with `@RefreshScope`. Not everything is refreshable, and improvements can be made incrementally 
to mark and tag components for reloadability.

## Differences

### View Resolution

CAS by default uses the Thymeleaf Template Engine for view resolution in a Spring Boot/Spring MVC world. Just like anything else,
the thymeleaf support is an optional add-on that can be excluded, and for this demo, we are investigating the ability to exclude
Thymeleaf support in favor of Apache Wicket. Since Apache Wicket does not seem to provide Spring-MVC support and a view resolution engine
(i.e. `ViewReolver`) that can be auto-configured by Spring Boot, we are providing our own `ViewResolver` implementation
that is based on Apache Wicket. This is handled via `WicketViewResolver` and `WicketView` components that are tasked to render Apache Wicket pages and present them to the MVC framework. 

As an example, we have a `CasLoginPage` wicket page that responds to the `/login` endpoint expected by CAS and its Spring Webflow machinery.

### Protocol Views

A number of other views/templates that are specific to aspects of *a given protocol*, mainly CAS itself, (not the software but the protocol itself) that are typically backed by Thymeleaf are also extracted out and are expected to be designed and rendered using Apache Wicket. For this purpose, we are injecting a `WicketCasProtocolViewFactory` into the runtime that is tasked to create `View` objects to render protocol-specific templates such as the validation/failure payloads rendered by the CAS protocol itself.

