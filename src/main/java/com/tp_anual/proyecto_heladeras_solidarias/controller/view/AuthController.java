package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.model.area.Area;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.EMail;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.Telefono;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaJuridica;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.NoUsuario;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.Usuario;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.EMailService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoServiceSelector;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.TecnicoService;
import com.tp_anual.proyecto_heladeras_solidarias.service.usuario.CustomUserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class AuthController {

    private final ColaboradorService colaboradorService;
    private final TecnicoService tecnicoService;
    private final CustomUserDetailsService customUserDetailsService;
    private final MedioDeContactoServiceSelector medioDeContactoServiceSelector;
    private final EMailService eMailService;

    private final I18nService i18nService;

    public AuthController(ColaboradorService vColaboradorService, CustomUserDetailsService vUsuarioService, TecnicoService vTecnicoService, MedioDeContactoServiceSelector vMedioDeContactoServiceSelector, EMailService vEMailService, I18nService vI18nService) {
        colaboradorService = vColaboradorService;
        tecnicoService = vTecnicoService;
        customUserDetailsService = vUsuarioService;
        medioDeContactoServiceSelector = vMedioDeContactoServiceSelector;
        eMailService = vEMailService;
        i18nService = vI18nService;
    }

    @GetMapping("/seleccion-persona")
    public String mostrarSeleccionPersona() {
        return "seleccion-persona";
    }

    @GetMapping("/registro-persona-humana")
    public String mostrarRegistroPersonaHumana(Model model) {
        model.addAttribute("emails", eMailService.obtenerEMails());
        model.addAttribute("message", i18nService.getMessage("controller.Authcontroller.mostrarRegistroPersonaHumana"));
        return "registro-persona-humana";
    }

    @PostMapping("/registro-persona-humana/guardar")
    public String guardarPersonaHumana(
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

        String username = customUserDetailsService.generarUsername(nombre, apellido);
        Usuario usuarioACrear = new Usuario(username, password, "ROL_CH");

        customUserDetailsService.guardarUsuario(usuarioACrear);

        ColaboradorHumano colaborador = new ColaboradorHumano(usuarioACrear, personaFisica, domicilio, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);

        colaborador.agregarMedioDeContacto(telefono);
        colaborador.agregarMedioDeContacto(eMail);

        Long colaboradorId = colaboradorService.guardarColaborador(colaborador).getId();

        colaboradorService.asignarUsuario(colaboradorId, usuarioACrear);

        MedioDeContacto medioDeContacto = colaborador.getMedioDeContacto(EMail.class);
        MedioDeContactoService medioDeContactoService = medioDeContactoServiceSelector.obtenerMedioDeContactoService(medioDeContacto.getClass());
        medioDeContactoService.contactar(medioDeContacto.getId(),  i18nService.getMessage("controller.AuthController.guardarPersonaHumana_title"), i18nService.getMessage("controller.AuthController.guardarPersonaHumana_body", username));

        return "redirect:/";
    }

    @GetMapping("/registro-persona-juridica")
    public String mostrarRegistroPersonaJuridica(Model model) {
        model.addAttribute("emails", eMailService.obtenerEMails());
        model.addAttribute("message", i18nService.getMessage("controller.Authcontroller.mostrarRegistroPersonaHumana"));
        return "registro-persona-juridica";
    }

    @PostMapping("/registro-persona-juridica/guardar")
    public String guardarPersonaJuridica(
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

        String username = customUserDetailsService.generarUsername("", razonSocial);
        Usuario usuarioACrear = new Usuario(username, password, "ROL_CJ");

        customUserDetailsService.guardarUsuario(usuarioACrear);

        ColaboradorJuridico colaborador = new ColaboradorJuridico(new NoUsuario(), personaJuridica, domicilio, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);

        colaborador.agregarMedioDeContacto(telefono);
        colaborador.agregarMedioDeContacto(eMail);

        Long colaboradorId = colaboradorService.guardarColaborador(colaborador).getId();

        colaboradorService.asignarUsuario(colaboradorId, usuarioACrear);

        MedioDeContacto medioDeContacto = colaborador.getMedioDeContacto(EMail.class);
        MedioDeContactoService medioDeContactoService = medioDeContactoServiceSelector.obtenerMedioDeContactoService(medioDeContacto.getClass());
        medioDeContactoService.contactar(medioDeContacto.getId(),  i18nService.getMessage("controller.AuthController.guardarPersonaHumana_title"), i18nService.getMessage("controller.AuthController.guardarPersonaHumana_body", username));

        return "redirect:/";
    }

    @GetMapping("/registro-tecnico")
    public String mostrarRegistrarTecnico(Model model) {
        model.addAttribute("emails", eMailService.obtenerEMails());
        model.addAttribute("message", i18nService.getMessage("controller.Authcontroller.mostrarRegistroPersonaHumana"));
        return "registro-tecnico";
    }

    @PostMapping("/registro-tecnico/guardar")
    public String guardarRegistroTecnico(
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("fecha-nacimiento") LocalDate fechaNacimiento,
        @RequestParam("tipo-documento") Documento.TipoDocumento tipoDocumento,
        @RequestParam("numero-documento") String numeroDocumento,
        @RequestParam("sexo-documento") Documento.Sexo sexoDocumento,
        @RequestParam("cuil") String cuil,
        @RequestParam("correo") String correo,
        @RequestParam("latitud-1") Double latitud1,
        @RequestParam("latitud-2") Double latitud2,
        @RequestParam("longitud-1") Double longitud1,
        @RequestParam("longitud-2") Double longitud2,
        @RequestParam("password") String password)
    {
        Documento documento = new Documento(tipoDocumento, numeroDocumento, sexoDocumento);
        PersonaFisica personaFisica = new PersonaFisica(nombre, apellido, documento, fechaNacimiento);
        Area area = new Area(latitud1, longitud1, latitud2, longitud2);

        MedioDeContacto medioDeContacto = new EMail(correo);

        String username = customUserDetailsService.generarUsername(nombre, apellido);
        Usuario usuarioACrear = new Usuario(username, password, "ROL_T");

        Tecnico tecnico = new Tecnico(usuarioACrear, personaFisica, cuil, medioDeContacto, area);

        customUserDetailsService.guardarUsuario(usuarioACrear);

        Long tecnicoId = tecnicoService.guardarTecnico(tecnico).getId();


        tecnicoService.asignarUsuario(tecnicoId, usuarioACrear);

        return "redirect:/registro-tecnico?success=true";
    }

    @GetMapping("/login")
    public String login(
        Model model,
        @RequestParam(value = "error", required = false) String error)
    {
        model.addAttribute("paginaActual", "/login");
        if (error != null) {
            model.addAttribute("loginError", i18nService.getMessage("controller.AuthController.login_err"));
        }

        return "login";
    }

    public void setPaginaActual(String pagina, Model model) {
        model.addAttribute("paginaActual", pagina);
    }
}
