package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.exception.colaborador.SuscripcionNoCorrespondeAColaboradorException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.colaborador.SuscripcionNoValidaException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion.Suscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion.Suscripcion.CondicionSuscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoFinder;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.HeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.suscripcion.SuscripcionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SuscripcionViewController {

    private final HeladeraService heladeraService;
    private final ColaboradorService colaboradorService;
    private final SuscripcionService suscripcionService;
    private final MedioDeContactoFinder medioDeContactoFinder;

    public SuscripcionViewController(HeladeraService vHeladeraService, ColaboradorService vColaboradorService, SuscripcionService vSuscripcionService, MedioDeContactoFinder vMedioDeContactoFinder) {
        heladeraService = vHeladeraService;
        colaboradorService = vColaboradorService;
        medioDeContactoFinder = vMedioDeContactoFinder;
        suscripcionService = vSuscripcionService;
    }

    @GetMapping("/suscribirse")
    public String mostrarFormSuscripcion(Model model) {
        setPaginaActual("/suscribirse", model);

        List<Heladera> heladeras = heladeraService.obtenerHeladeras();
        model.addAttribute("heladeras", heladeras);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        model.addAttribute("colaborador", colaborador);

        return "suscribirse";
    }

    @PostMapping("/suscribirse/guardar")
    public String guardarSuscripcion(
        @RequestParam("heladera") Long heladeraId,
        @RequestParam("condicion") CondicionSuscripcion condicion,
        @RequestParam("medio-de-contacto") Long medioDeContactoId,
        @RequestParam(value = "limite-inferior", defaultValue = "0") Integer limiteInferior,
        @RequestParam(value = "limite-superior", defaultValue = "0") Integer limiteSuperior)
        throws SuscripcionNoValidaException
    {
        Integer limite = Math.max(limiteInferior, limiteSuperior);

        Heladera heladera = heladeraService.obtenerHeladera(heladeraId);
        MedioDeContacto medioDeContacto = medioDeContactoFinder.obtenerMedioDeContacto(medioDeContactoId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);
        colaboradorService.suscribirse(colaborador.getId(), heladera, medioDeContacto, condicion, limite);

        return "redirect:/suscribirse";
    }

    @GetMapping("/suscripciones")
    public String mostrarSuscripciones(Model model) {
        setPaginaActual("/suscripciones", model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        List<Suscripcion> suscripciones = suscripcionService.obtenerSuscripcionesPorColaborador(colaborador);

        model.addAttribute("colaborador", colaborador);
        model.addAttribute("suscripciones", suscripciones);

        return "suscripciones";
    }

    @PostMapping("/modificar-suscripcion/guardar")
    public String modificarSuscripcion(
            @RequestParam("suscripcion-id") Long suscripcionId,
            @RequestParam("nuevo-valor") Integer nuevoValor)
            throws SuscripcionNoCorrespondeAColaboradorException
    {
        Suscripcion suscripcion = suscripcionService.obtenerSuscripcion(suscripcionId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        colaboradorService.modificarSuscripcion(colaborador.getId(), suscripcion, nuevoValor);

        return "redirect:/suscripciones";
    }

    @GetMapping("/suscripcion/borrar/{id}")
    public String borrarSuscripcion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Suscripcion suscripcion = suscripcionService.obtenerSuscripcion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        colaboradorService.eliminarSuscripcion(colaborador.getId(), suscripcion);

        return "redirect:/suscripciones";
    }

    void setPaginaActual(String pagina, Model model) {;
        model.addAttribute("paginaActual", pagina);
    }
}
