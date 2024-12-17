package com.tp_anual.proyecto_heladeras_solidarias.security;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.Usuario;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.usuario.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomUserDetailsService customUserDetailsService;
    private final ColaboradorService colaboradorService;

    public CustomAuthenticationSuccessHandler(CustomUserDetailsService vCustomUserDetailsService, ColaboradorService vColaboradorService) {
        customUserDetailsService = vCustomUserDetailsService;
        colaboradorService = vColaboradorService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String redirectUrl = "/";

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("given_name");
        String apellido = (String) attributes.get("family_name");

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorEMail(email);

        HttpSession session = request.getSession();
        session.setAttribute("email-auth2", email);
        session.setAttribute("nombre-humano-auth2", nombre);
        session.setAttribute("apellido-humano-auth2", apellido);

        if (colaborador == null) {
            String username = customUserDetailsService.generarUsername(nombre, apellido);
            Usuario usuarioACrear = new Usuario(username, username, "ROL_GENERICO");
            customUserDetailsService.guardarUsuario(usuarioACrear);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(usuarioACrear, oAuth2User);

            Authentication newAuth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), "google");

            SecurityContextHolder.getContext().setAuthentication(newAuth);

            session.setAttribute("username", username);

            redirectUrl = "/completar-datos-seleccion-persona";

        } else {
            Usuario usuario = customUserDetailsService.obtenerUsuarioPorColaborador(colaborador);
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(usuario, oAuth2User);

            Authentication newAuth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), "google");

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        response.sendRedirect(redirectUrl);
    }
}
