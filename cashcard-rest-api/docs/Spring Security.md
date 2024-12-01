## Authentication

Authentication is the act of the **Principal** (user) providing its identity to the system, for example providing a username and password using **Basic Authentication**.

HTTP is a stateless protocol, meaning that each request must contain data that proves it's from a authenticated Principal. Because that, a strategy to skip this requirement is to create an **Authentication Session** (or Auth Session or just Session) when a users get authenticated using **Session Token** that is generated and place in a **Cookie**.

**note:** Cookies are automatically sent to the server with every request (no extra code needs to be written for this to happen) and can persist for a certain amount of time even if the web page is closed and later re-visited.

## Spring Security - Authentication and Authorization

**Spring Security** implements authentication in the **Filter Chain**. The Filter Chain is a component of Java web architecture which allows to define a sequence of methods that get called prior to the Controller. Each filter in the chain decides whether to allow request processing to continue, or not. Spring Security inserts a filter which checks the user’s authentication and returns with a `401 UNAUTHORIZED` response if the request is not authenticated.

After the user is authenticated, the **Authorization** is what dictates what the user can do in the system. **Spring Security** provides Authorization via **Role-Based Access Control (RBAC)**. This means that a Principal has a number of **Roles** and each resource (or operation) specifies which Roles a Principal must have in order to perform actions with proper authorization.

## Same-Origin Policy (SOP)

The **Same-Origin Policy** is a security mechanism implemented by web browsers to prevent malicious websites from interacting with resources belonging to another origin.

An **origin** is defined by:
- **Protocol** (e.g., `http`, `https`)
- **Domain** (e.g., `example.com`)
- **Port** (e.g., `80`, `443`)

*Two resources are considered of the same origin if all three elements match exactly*.

## Cross-Origin Resource Sharing (CORS)

**CORS** is an extension to SOP that allows the serve to specify a list of "allowed origins" of request coming form an origin outside the server's. By default, browsers block cross-origin AJAX unless explicitly allowed.

**Implementation in Spring Security**

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()  // Enable CORS
                .and()
                .csrf().disable() // Disable CSRF for simplicity (not recommended for production)
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin();

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://trusted-origin.com")); // Trusted origins
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
```

## Cross-Site Request Forgery (CSRF)

CSRF attacks happen when a malicious piece of code sends a request to a server where a user is authenticated. When the server receives the Authentication Cookie, it has no way of knowing if the victim sent the harmful request unintentionally.

To protect against CSRF attack, you can use a **CSRF Token** that must be generated on each request to the server, opposite to the Auth Token.

**Default CSRF example**
```java
http
    .csrf() // CSRF enabled by default
    .and()
    .authorizeHttpRequests()
    .anyRequest().authenticated();
```
**Disabling for APIs: (Recommended only the case where JWT or other stateless mechanism is used)
```java
http
    .csrf().disable() // Ensure you understand the risks before disabling CSRF
    .authorizeHttpRequests()
    .anyRequest().authenticated();
```

## Cross-Site Scripting

[//]: # (TODO: write this part)


