package app.quantun.backend.config.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@RequiredArgsConstructor
public class SecurityConfig   {


    private final CustomAuthenticationSuccessHandler successHandler;


    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String loginPage = "/custom-login";

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers( "/css/**", "/js/**", "/iages/**", "/public/**","/media/**", loginPage, "/custom-logout",
                                "/saml2/service-provider-metadata/**", "/saml2/authenticate/**", "/saml2/logout/**",
                                "/saml2/metadata/**", "/saml2/redirect/**", "/saml2/slo/**","/saml2/**"
                                ).permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/").authenticated()
                        .anyRequest().authenticated()
                )
                .saml2Login(saml2 -> saml2
                        .loginPage(loginPage)
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout/saml2/slo")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl(loginPage)
                        .deleteCookies("JSESSIONID")
                )
                .saml2Logout(Customizer.withDefaults());

        return http.build();
    }








}