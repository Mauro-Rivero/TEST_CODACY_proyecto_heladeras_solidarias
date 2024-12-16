package com.tp_anual.proyecto_heladeras_solidarias.config;

import com.tp_anual.proyecto_heladeras_solidarias.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfiguration(UserDetailsService vUserDetailsService, CustomAuthenticationSuccessHandler vCustomAuthenticationSuccessHandler) {
        userDetailsService = vUserDetailsService;
        customAuthenticationSuccessHandler = vCustomAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        // Permito recursos estáticos
                        .requestMatchers("/css/**", "/js/**", "/assets/**").permitAll()
                        // Endpoints "públicos"
                        .requestMatchers
                                ("/", "/quienes-somos", "/como-participar", "/mapa-heladeras",
                                "/seleccion-persona",
                                "/registro-persona-humana", "/registro-persona-humana/guardar",
                                "/registro-persona-juridica", "/registro-persona-juridica/guardar",
                                "/registro-tecnico", "/registro-tecnico/guardar",
                                "/ubicador-api", "/ubicador-api/{latitud}/{longitud}",
                                "/api/heladeras", "/login").permitAll()
                        // Endpoints para cualquier usuario
                        .requestMatchers("/api/heladeras").authenticated()
                        // Endpoints exclusivos de 'ROL_CH' (Colaborador Humano)
                        .requestMatchers
                                (new AntPathRequestMatcher("/colaborar-personas-fisicas"),
                                        new AntPathRequestMatcher("/distribuir-viandas"),
                                        new AntPathRequestMatcher("/donar-vianda"),
                                        new AntPathRequestMatcher("/registrar-persona-vulnerable"),
                                        new AntPathRequestMatcher("/suscribirse"),
                                        new AntPathRequestMatcher("/perfil-persona-humana"))
                        .hasAuthority("ROL_CH")
                        // Endpoints exclusivos de 'ROL_CJ' (Colaborador Jurídico)
                        .requestMatchers
                                (new AntPathRequestMatcher("/cargar-premio"),
                                new AntPathRequestMatcher("/colaborar-personas-juridicas"),
                                new AntPathRequestMatcher("/colocar-heladera"),
                                new AntPathRequestMatcher("/heladeras"),
                                new AntPathRequestMatcher("/perfil-persona-juridica"))
                        .hasAuthority("ROL_CJ")
                        // Endpoints accesibles por 'ROL_CJ' y 'ROL_CH' (ambos colaboradores)
                        .requestMatchers
                                (new AntPathRequestMatcher("/contribuciones"),
                                new AntPathRequestMatcher("/donar-dinero"),
                                new AntPathRequestMatcher("/reportar-falla-tecnica"),
                                new AntPathRequestMatcher("/tienda"))
                        .hasAnyAuthority("ROL_CH", "ROL_CJ")
                        // Endpoints exclusivos de 'ROL_ADMIN' (admin)
                        .requestMatchers
                                (new AntPathRequestMatcher("/admin"),
                                new AntPathRequestMatcher("/registro-tecnico"))
                        .hasAuthority("ROL_ADMIN")
                        // Endpoints exclusivos de 'ROL_T' (técnico)
                        .requestMatchers("/tecnico")
                        .hasAuthority("ROL_T")
                        .requestMatchers
                                (new AntPathRequestMatcher("/completar-datos-seleccion-persona"),
                                new AntPathRequestMatcher("/completar-datos-persona-humana"),
                                new AntPathRequestMatcher("/completar-datos-persona-juridica"))
                        .hasAuthority("ROL_GENERICO")
                        .anyRequest().authenticated())
                .formLogin(httpSecurityFormLoginConfigurer ->  httpSecurityFormLoginConfigurer
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/?loginSuccess=true")
                        .failureUrl("/login?error"))
                .oauth2Login(auth -> auth
                        .loginPage("/login")
                        .defaultSuccessUrl("/?loginSuccess=true")
                        .failureUrl("/login?error")
                        .successHandler(customAuthenticationSuccessHandler))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.permitAll()
                        .logoutRequestMatcher
                                (new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/?logoutSuccess=true"))
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
