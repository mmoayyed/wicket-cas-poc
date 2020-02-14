# Embedded CAS Server with Apache Wicket - POC

# Overview

This is a modest POC to demonstrate how a given CAS server can be *embedded* inside a Spring Boot application
to act as an identity provider while still presenting functionality and protocol support as a standalone CAS server would. The view layer and framework is, *experimentally*, chosen to be Apache Wicket.

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
you may start with something like `http://localhost:8080/login?service=https://app.example.org`, log in with the credentials
provided and be issued a service ticket request. 

Additional endpoints are also available depending on the availability of modules included in the build. For example, 
the OIDC discovery endpoint is provided at `http://localhost:8080/oidc/.well-known`.
Spring Actuator endpoints some of which are provided by CAS are also found at `http://localhost:8080/actuator`.

![image](https://user-images.githubusercontent.com/1205228/74520785-b2cd1d00-4f31-11ea-9e7d-6b4b9ab2c622.png)

# Design

## Baseline

The CAS server itself is composed of many individual modules each of which provides a specific type of functionality
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

Typically, adding additional functionality is as simple as including the module in the build. Once the module is included,
its behavior can be altered using Spring Cloud and `@RefreshScope`. Components in CAS that can be refreshed are marked with `@RefreshScope`. Not everything is refreshable, and improvements can be made incrementally to mark and tag components for reloadability.

## Differences

### View Resolution

CAS by default uses the Thymeleaf Template Engine for view resolution in a Spring Boot/Spring MVC world. Just like anything else,
the thymeleaf support is an optional add-on that can be excluded, and for this demo, we are investigating the ability to exclude
Thymeleaf support in favor of Apache Wicket. Since Apache Wicket does not seem to provide Spring-MVC support and a view resolution engine
(i.e. `ViewReolver`) that can be auto-configured by Spring Boot, we are providing our own `ViewResolver` implementation
that is based on Apache Wicket. This is handled via `WicketViewResolver` and `WicketView` components that are tasked to render Apache Wicket pages and present them to the MVC framework. 

As an example, we have a `CasLoginPage` wicket page that responds to the `/login` endpoint expected by CAS and its Spring Webflow machinery.

### Protocol Views

Several other views/templates that are specific to aspects of *a given protocol*, mainly CAS itself, (not the software but the protocol itself) that are typically backed by Thymeleaf are also extracted out and are expected to be designed and rendered using Apache Wicket. For this purpose, we are injecting a `WicketCasProtocolViewFactory` into the runtime that is tasked to create `View` objects to render protocol-specific templates such as the validation/failure payloads rendered by the CAS protocol itself.

# Lessons Learned

## Apache Wicket for the View Layer

Apereo CAS primarily and very heavily operates and orchestrates the authentication flows using a combination of Spring Webflow and Thymeleaf. Thymeleaf support and auto-configuration is provided by Spring Boot automatically, which is used by CAS to handle he user interface, branding
and themes. While Thymeleaf itself can be extracted in favor of another templating engine such FreeMarker, Apache Velocity (also deprecated and removed from Spring), Groovy, Mustache, etc, using Apache Wicket as the view engine with CAS will, in the long-term makes things *significantly and unnecessarily complicated*:

- Apache Wicket integration with Spring Boot is not a native module and is provided via a 3rd-party addon. 
- Apache Wicket is not a templating engine, as CAS would expect auto-configured via Spring Boot. No built-in `ViewResolver` is available.
- The current `ViewResolver` machinery requires one to render pages out of band, forcefully, before presenting content back. This is unnecessary, prone to error, and long-term maintenance headaches.
- Custom code is required to translate model data produced by CAS from Spring Webflow over to Apache Wicket pages and page-parameters. This item makes the solution very unattractive.
- Most if not all CAS views need to be re-implemented in Apache Wicket form, which makes maintenance very difficult, security risk and upgrades prone to error. Also, duplicating views in Wicket increases the risk breaking changes as CAS views are updated, added or removed, since such things are not considered part of a public API. 

The following alternatives are far better choices:

- **Use Thymeleaf as is provided by CAS**; remove custom, duplicate code and only customize what is necessary.
Or
- Use an *actual templating engine* instead of Thymeleaf whose auto-configuration, support and maintenance is backed by an active community.

## Refreshability

The CAS server has baked in strategies to auto-configure and bootstrap itself via REST APIs. The bootstrapping process feeds values to Spring Beans at initialization time to auto-configure components and registers them in the application context. However, when it comes to managing dynamic states and values using an external configuration store, such beans need to be refreshed and the application context must be renewed. This is handled automatically by Spring Cloud and beans that declare their support for refreshability using `@RefreshScope`. Not everything, of course, can be refreshable, especially components that require serializability since refresh-scope creates proxies around components via cglib. So while most components are marked as refreshable, additional testing effort and modest improvements may be required to adjust support for the dynamic state.

This is generally not a big deal; just worth pointing out.

# Other Alternatives

It is possible to use CAS server components in a way to exclude Thymeleaf, Spring Webflow and purely use the server's components that provide protocol support while building the authentication flow and the rest of the logic separately. This approach, while plausible in theory, has several serious drawbacks:

- While technically possible on paper, it is evidently unnecessary. There is really no good answer to the "But Why?" question.
- It, quite likely, requires a HIOOOGE refactoring effort on part of the CAS server to completely reorganize components to detach themselves from Spring Webflow and family.
- In doing so, the CAS server purely turns into a framework/library and loses much of its advantage. There are plenty of other libraries available, on top of which protocol and authentication support can be built.