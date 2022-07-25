````
  ____  ____ _____   ____                 _                                  _   
 / ___||  _ \_   _| |  _ \  _____   _____| | ___  _ __  _ __ ___   ___ _ __ | |_ 
 \___ \| |_) || |   | | | |/ _ \ \ / / _ \ |/ _ \| '_ \| '_ ` _ \ / _ \ '_ \| __|
  ___) |  __/ | |   | |_| |  __/\ V /  __/ | (_) | |_) | | | | | |  __/ | | | |_ 
 |____/|_|    |_|   |____/ \___| \_/ \___|_|\___/| .__/|_| |_| |_|\___|_| |_|\__|
                                                 |_|                                           
 cid-web-------------------------------------------------------------------------
````

[![build_status](https://github.com/spt-development/spt-development-cid-web/actions/workflows/build.yml/badge.svg)](https://github.com/spt-development/spt-development-cid-web/actions)

Library for integrating [spt-development/spt-development-cid](https://github.com/spt-development/spt-development-cid)
into a web project.

Usage
=====

Register the filter to initialise the correlation ID for each incoming request and return as a response header. For 
example, in a Spring based project register as a bean.

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        final FilterRegistrationBean<CorrelationIdFilter> filterRegBean = new FilterRegistrationBean<>(
                // Constructor overloads allow the overiding of the correlation ID header and whether to use the 
                // request correlation ID header (if it exists) to initialise the correlation ID.
                new CorrelationIdFilter()
        );

        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegBean.addUrlPatterns("/api/v1.0/*");

        return filterRegBean;
    }

Alternatively, if you are integrating the library into a Spring Boot project, add the
[spt-development/spt-development-cid-web-spring-boot](https://github.com/spt-development/spt-development-cid-web-spring-boot)
starter to your project's pom.

Building locally
================

To build the library, run the following maven command:

    $ mvn clean install

Release
=======

To build a release and upload to Maven Central push to `main`.