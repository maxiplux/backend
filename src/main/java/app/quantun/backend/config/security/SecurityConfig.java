package app.quantun.backend.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableMethodSecurity
public class SecurityConfig   {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers( "/css/**", "/js/**", "/iages/**", "/public/**","/media/**", "/custom-login", "/custom-logout",
                                "/saml2/service-provider-metadata/**", "/saml2/authenticate/**", "/saml2/logout/**",
                                "/saml2/metadata/**", "/saml2/redirect/**", "/saml2/slo/**","/saml2/**"
                                ).permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/").authenticated()
                        .anyRequest().authenticated()
                )
                .saml2Login(saml2 -> saml2
                        .loginPage("/custom-login")
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout/saml2/slo")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/custom-login")
                        .deleteCookies("JSESSIONID")
                )
                .saml2Logout(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public Saml2MetadataResolver saml2MetadataResolver() {
        return new OpenSamlMetadataResolver();
    }

//
//    @Bean
//    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
//        RelyingPartyRegistration registration = RelyingPartyRegistration
//                .withRegistrationId("pingone")
//                .assertingPartyDetails(party -> party
//                        .entityId("IDP_ENTITY_ID")
//                        .singleSignOnServiceLocation("https://auth.pingone.com/21620bf7-6daf-4fd6-a951-ae0f9b1bd7af/saml20/idp/sso")
//                        .wantAuthnRequestsSigned(false)
//                        .signingAlgorithms(sign -> sign.add("rsa-sha256"))
//                )
//                .build();
//        return new InMemoryRelyingPartyRegistrationRepository(registration);
//    }

    //    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .antMatchers("/", "/public/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .saml2Login(Customizer.withDefaults());
//
//        return http.build();
//    }
//






//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        OpenSaml4AuthenticationProvider provider =
//                new OpenSaml4AuthenticationProvider();
//
//        provider.setResponseAuthenticationConverter(token -> {
//            var auth = OpenSaml4AuthenticationProvider
//                    .createDefaultResponseAuthenticationConverter()
//                    .convert(token);
//            log.info("AUTHORITIES: {}", auth.getAuthorities());
//
//            var attrValues = token.getResponse().getAssertions().stream()
//                    .flatMap(as -> as.getAttributeStatements().stream())
//                    .flatMap(attrs -> attrs.getAttributes().stream())
//                    .filter(attrs -> attrs.getName().equals("member"))
//                    .findFirst().orElseThrow().getAttributeValues();
//
//            if (!attrValues.isEmpty()) {
//
//                var member = ((XSStringImpl) attrValues.get(0)).getValue();
//                log.info("MEMBER: {}", member);
//                List<GrantedAuthority> authoritiesList = List.of(
//                        new SimpleGrantedAuthority("ROLE_USER"),
//                        new SimpleGrantedAuthority("ROLE_" +
//                                member.toUpperCase().replaceFirst("/", ""))
//                );
//
//                log.info("NEW AUTHORITIES: {}", authoritiesList);
//                return new Saml2Authentication(
//                        (AuthenticatedPrincipal) auth.getPrincipal(),
//                        auth.getSaml2Response(),
//                        authoritiesList);
//            } else return auth;
//        });
//
//        http.csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize.anyRequest()
//                        .authenticated())
//                .saml2Login(saml2 -> saml2
//                        .authenticationManager(new ProviderManager(provider))
//                )   ;
//
//        return http.build();
//    }



}