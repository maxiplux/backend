# Enterprise Spring Boot Project with Azure Integration

## Introduction

This document serves as a comprehensive guide for developing an enterprise-level Spring Boot project integrated with
Azure services for authentication and batch processing. The project is structured into multiple branches, each focusing
on a specific feature, and follows industry best practices to ensure scalability, security, and maintainability.

### Goals of the Project:

- Implement a robust Spring Boot application with enterprise-grade features.
- Integrate Azure-based authentication mechanisms (SAML and OpenID Connect).
- Incorporate Spring Batch for automated processing with secure authentication.
- Define clear guidelines for setup, configuration, and best practices.

The project is divided into the following branches:

- **Base Branch**: Contains the core Spring Boot application setup, REST APIs, and database configurations.
- **SSO Azure SAML Branch**: Integrates Single Sign-On (SSO) using Azure SAML for user authentication.
- **API Azure OpenID API Branch**: Secures APIs using Azure OpenID Connect and OAuth 2.0.
- **Spring Batch with OpenID Azure Branch**: Implements batch processing with authentication via Azure OpenID Connect.

This guide will walk you through setting up the base project and incrementally adding each feature, ensuring a modular
and maintainable codebase.

## Project Structure

The project uses Git for version control and is organized into the following branches:

- **main**: The primary branch where all features are integrated after stabilization.
- **feature/base**: Implements the foundational Spring Boot application.
- **feature/sso-azure-saml**: Adds SSO functionality using Azure SAML.
- **feature/api-azure-openid**: Secures APIs using Azure OpenID Connect.
- **feature/spring-batch-openid**: Implements Spring Batch with Azure OpenID authentication.

Each branch focuses on a specific feature, allowing for independent development and testing. Features can be merged into
the main branch once stable and tested.

## Setting up the Base Project

The base project forms the foundation of the application, including basic Spring Boot setup, REST APIs, and database
connectivity.

### Creating a New Spring Boot Project

Use Spring Initializr to generate a new project with the following settings:

- **Project**: Maven Project
- **Language**: Java
- **Spring Boot Version**: Latest stable version
- **Group**: `com.example`
- **Artifact**: `myproject`
- **Dependencies**:
    - Spring Web
    - Spring Data JPA
    - H2 Database (for development)

Download the generated zip file, unzip it, and open the project in your preferred IDE.

### Configuring Application Properties

In `src/main/resources/application.properties`, configure the server and database settings:

```properties
# Server settings
server.port=8080
# Database settings
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

This configuration sets up an in-memory H2 database for development and testing purposes.

### Creating REST APIs

Create a package `com.example.myproject.controller` and add a simple REST controller:

```java
package com.example.myproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
```

Run the application by executing the main method in `MyprojectApplication.java`. Test the API by
visiting [http://localhost:8080/hello](http://localhost:8080/hello) in your browser.

## Adding SSO with Azure SAML

This section guides you through integrating Single Sign-On (SSO) using Azure SAML for user authentication.

### Configuring Azure for SAML

Register the application in Azure Active Directory (Azure AD):

1. Navigate to **Azure Portal > Azure Active Directory > Enterprise Applications**.
2. Add a new application and select "Non-gallery application."

#### Configure SAML settings:

- **Identifier (Entity ID)**: Unique identifier for your application (e.g., `urn:example:myproject`).
- **Reply URL (Assertion Consumer Service URL)**: Where Azure AD sends SAML responses (e.g.,
  `http://localhost:8080/login/saml2/sso`).

Download the SAML metadata XML file from Azure AD. Obtain the following details from Azure AD:

- **Login URL**: SAML SSO endpoint.
- **Azure AD Identifier**: Entity ID of Azure AD.
- **Certificate**: For signing SAML assertions.

### Integrating SAML in Spring Boot

Add SAML dependencies to `pom.xml`:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-saml2-service-provider</artifactId>
<version>${spring-security.version}</version>
</dependency>
```

#### Configure Spring Security to use SAML:

1. Create a `SecurityConfig.java` class to enable SAML authentication.
2. Use the Azure AD metadata XML for configuration.

#### Test SSO functionality:

- Access a protected endpoint (e.g. `http://localhost:8080/protected`).
- You should be redirected to Azure AD for authentication.

## Securing APIs with Azure OpenID Connect

This section covers securing REST APIs using Azure OpenID Connect and OAuth 2.0.

### Configuring Azure for OpenID Connect

Register the API in Azure AD:

1. Navigate to **Azure Portal > Azure Active Directory > App Registrations**.
2. Create a new registration for the API.
    - Note the **Client ID** and **Tenant ID**.

#### Expose API scopes:

- Under "Expose an API," add scopes (e.g., `api://<client-id>/read`).
- Authorize client applications to access these scopes.

#### Obtain the OpenID Connect metadata:

- Use the metadata endpoint: `https://login.microsoftonline.com/<tenant-id>/v2.0/.well-known/openid-configuration`.

### Implementing OAuth 2.0 in Spring Security

Add OAuth 2.0 dependencies to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

#### Configure Spring Security:

Update `application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/<tenant-id>/v2.0
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://login.microsoftonline.com/<tenant-id>/discovery/v2.0/keys
```

Create a `SecurityConfig.java` class to configure the resource server:

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .and()
            .oauth2ResourceServer().jwt();
    }
}
```

#### Secure API endpoints:

- Annotate controllers with `@PreAuthorize` to enforce scope-based access.

#### Test API security:

1. Obtain an access token from Azure AD using a client application.
2. Include the token in the Authorization header (e.g., `Bearer <token>`).
3. Access protected endpoints to verify token validation.