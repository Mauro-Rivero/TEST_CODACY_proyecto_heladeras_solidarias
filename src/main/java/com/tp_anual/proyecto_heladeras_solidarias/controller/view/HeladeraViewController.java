package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.HeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
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


import java.util.ArrayList;
import java.util.List;

@Controller
public class HeladeraViewController {

    private final ColaboradorService colaboradorService;
    private final HeladeraService heladeraService;
    private final I18nService i18nService;

    public HeladeraViewController(ColaboradorService vColaboradorService, HeladeraService vHeladeraService, I18nService i18nService) {
        colaboradorService = vColaboradorService;
        heladeraService = vHeladeraService;
        this.i18nService = i18nService;
    }

    @GetMapping("/heladeras")
    public String mostrarHeladeras(Model model) {
        setPaginaActual("/heladeras", model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        ColaboradorJuridico colaborador = colaboradorService.obtenerColaboradorJuridicoPorUsername(username);

        List<Heladera> heladeras = heladeraService.obtenerHeladerasPorColaborador(colaborador);

        model.addAttribute("colaborador", colaborador);
        model.addAttribute("heladeras", heladeras);

        return "heladeras";
    }

    @PostMapping("/modificar-heladera/guardar")
    public String modificarHeladera(
        @RequestParam("heladera-id") Long heladeraId,
        @RequestParam("nombre") String nombre,
        @RequestParam("direccion") String direccion,
        @RequestParam("codigo-postal") String codigoPostal,
        @RequestParam("ciudad") String ciudad)
    {
        Heladera heladera = heladeraService.obtenerHeladera(heladeraId);
        Ubicacion ubicacion = new Ubicacion(heladera.getUbicacion().getLatitud(), heladera.getUbicacion().getLongitud(), direccion, codigoPostal, ciudad, heladera.getUbicacion().getPais());

        heladera.setNombre(nombre);
        heladera.setUbicacion(ubicacion);

        heladeraService.guardarHeladera(heladera);

        return "redirect:/heladeras";
    }

    @GetMapping("/heladera/borrar/{id}")
    public String borrarHeladera(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!heladeraService.estaVacia(id)) {
            redirectAttributes.addFlashAttribute("message", i18nService.getMessage("controller.HeladeraController.borrarHeladera_err"));
            return "redirect:/heladeras";
        }

        heladeraService.elimiarHeladera(id);

        return "redirect:/heladeras";
    }

    public void setPaginaActual(String pagina, Model model) {
        model.addAttribute("paginaActual", pagina);
    }
}
