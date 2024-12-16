package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.EMail;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.Telefono;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaJuridica;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.NoUsuario;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.Usuario;
import com.tp_anual.proyecto_heladeras_solidarias.security.CustomOAuth2User;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.EMailService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoServiceSelector;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.usuario.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class OAuth2Controller {

    private final CustomUserDetailsService customUserDetailsService;
    private final ColaboradorService colaboradorService;
    private final MedioDeContactoServiceSelector medioDeContactoServiceSelector;
    private final EMailService eMailService;

    private final I18nService i18nService;

    public OAuth2Controller(CustomUserDetailsService vCustomUserDetailsService, ColaboradorService vColaboradorService, MedioDeContactoServiceSelector vMedioDeContactoServiceSelector, EMailService vEMailService, I18nService vI18nService) {
        customUserDetailsService = vCustomUserDetailsService;
        colaboradorService = vColaboradorService;
        medioDeContactoServiceSelector = vMedioDeContactoServiceSelector;
        eMailService = vEMailService;
        i18nService = vI18nService;
    }

    @GetMapping("/completar-datos-seleccion-persona")
    public String mostrarSeleccionPersonaRellenoDatos() {
        return "seleccion-persona-auth2";
    }

    @GetMapping("/completar-datos-persona-humana")
    public String mostrarRegistroPersonaHumanaRellenoDatos(Model model) {
        model.addAttribute("emails", eMailService.obtenerEMails());
        model.addAttribute("message", i18nService.getMessage("controller.Authcontroller.mostrarRegistroPersonaHumana"));
        return "registro-persona-humana-auth2";
    }

    @PostMapping("/completar-datos-persona-humana/guardar")
    public String guardarPersonaHumana(
        HttpSession session,
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("fecha-nacimiento") LocalDate fechaNacimiento,
        @RequestParam("tipo-documento") Documento.TipoDocumento tipoDocumento,
        @RequestParam("numero-documento") String numeroDocumento,
        @RequestParam("sexo-documento") Documento.Sexo sexoDocumento,
        @RequestParam("pais") String pais,
        @RequestParam("ciudad") String ciudad,
        @RequestParam("calle") String calle,
        @RequestParam("altura") String altura,
        @RequestParam("codigo-postal") String codigoPostal,
        @RequestParam("prefijo") String prefijo,
        @RequestParam("codigo-area") String codigoArea,
        @RequestParam("numero-telefono") String numeroTelefono,
        @RequestParam("correo") String correo,
        @RequestParam("password") String password)
    {
        Documento documento = new Documento(tipoDocumento, numeroDocumento, sexoDocumento);
        PersonaFisica personaFisica = new PersonaFisica(nombre, apellido, documento, fechaNacimiento);
        Ubicacion domicilio = new Ubicacion(null, null, (calle + " " + altura), codigoPostal, ciudad, pais);
        Telefono telefono = new Telefono(prefijo, codigoArea, numeroTelefono);
        EMail eMail = new EMail(correo);

        String username = (String) session.getAttribute("username");
        Usuario usuario = customUserDetailsService.obtenerUsuarioPorUsername(username);

        usuario.setPassword(password);
        usuario.setRole("ROL_CH");

        customUserDetailsService.guardarUsuario(usuario);

        ColaboradorHumano colaborador = new ColaboradorHumano(usuario, personaFisica, domicilio, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);

        colaborador.agregarMedioDeContacto(telefono);
        colaborador.agregarMedioDeContacto(eMail);

        Long colaboradorId = colaboradorService.guardarColaborador(colaborador).getId();

        colaboradorService.asignarUsuario(colaboradorId, usuario);

        MedioDeContacto medioDeContacto = colaborador.getMedioDeContacto(EMail.class);
        MedioDeContactoService medioDeContactoService = medioDeContactoServiceSelector.obtenerMedioDeContactoService(medioDeContacto.getClass());
        medioDeContactoService.contactar(medioDeContacto.getId(),  i18nService.getMessage("controller.AuthController.guardarPersonaHumana_title"), i18nService.getMessage("controller.AuthController.guardarPersonaHumana_body", username));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(usuario, oAuth2User);
        Authentication newAuth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), "google");
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/";
    }

    @GetMapping("/completar-datos-persona-juridica")
    public String mostrarRegistroPersonaJuridicaRellenoDatos(Model model) {
        model.addAttribute("emails", eMailService.obtenerEMails());
        model.addAttribute("message", i18nService.getMessage("controller.Authcontroller.mostrarRegistroPersonaHumana"));
        return "registro-persona-juridica-auth2";
    }

    @PostMapping("/completar-datos-persona-juridica/guardar")
    public String guardarPersonaJuridica(
        HttpSession session,
        @RequestParam("razon-social") String razonSocial,
        @RequestParam("tipo-persona-juridica") PersonaJuridica.TipoPersonaJuridica tipoPersonaJuridica,
        @RequestParam("rubro") String rubro,
        @RequestParam("pais") String pais,
        @RequestParam("ciudad") String ciudad,
        @RequestParam("calle") String calle,
        @RequestParam("altura") String altura,
        @RequestParam("codigo-postal") String codigoPostal,
        @RequestParam("prefijo") String prefijo,
        @RequestParam("codigo-area") String codigoArea,
        @RequestParam("numero-telefono") String numeroTelefono,
        @RequestParam("correo") String correo,
        @RequestParam("password") String password)
    {
        PersonaJuridica personaJuridica = new PersonaJuridica(razonSocial, tipoPersonaJuridica, rubro);
        Ubicacion domicilio = new Ubicacion(null, null, (calle + " " + altura), codigoPostal, ciudad, pais);
        Telefono telefono = new Telefono(prefijo, codigoArea, numeroTelefono);
        EMail eMail = new EMail(correo);

        String username = (String) session.getAttribute("username");
        Usuario usuario = customUserDetailsService.obtenerUsuarioPorUsername(username);

        usuario.setPassword(password);
        usuario.setRole("ROL_CJ");

        customUserDetailsService.guardarUsuario(usuario);

        ColaboradorJuridico colaborador = new ColaboradorJuridico(new NoUsuario(), personaJuridica, domicilio, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);

        colaborador.agregarMedioDeContacto(telefono);
        colaborador.agregarMedioDeContacto(eMail);

        Long colaboradorId = colaboradorService.guardarColaborador(colaborador).getId();

        colaboradorService.asignarUsuario(colaboradorId, usuario);

        MedioDeContacto medioDeContacto = colaborador.getMedioDeContacto(EMail.class);
        MedioDeContactoService medioDeContactoService = medioDeContactoServiceSelector.obtenerMedioDeContactoService(medioDeContacto.getClass());
        medioDeContactoService.contactar(medioDeContacto.getId(),  i18nService.getMessage("controller.AuthController.guardarPersonaHumana_title"), i18nService.getMessage("controller.AuthController.guardarPersonaHumana_body", username));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(usuario, oAuth2User);
        Authentication newAuth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), "google");
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/";
    }
}
