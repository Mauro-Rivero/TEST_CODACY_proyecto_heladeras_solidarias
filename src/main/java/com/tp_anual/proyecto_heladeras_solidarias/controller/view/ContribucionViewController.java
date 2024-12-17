package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.exception.colaborador.ContribucionNoPermitidaException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaColaboradorException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaPESVException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.SolicitudAperturaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.PosesionViandas;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.oferta.Oferta;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaboradorNula;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaPersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.HeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.PosesionViandasService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.ViandaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerableService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta.TarjetaColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta.TarjetaPersonaEnSituacionVulnerableService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ContribucionViewController {

    private final HeladeraService heladeraService;
    private final TarjetaColaboradorService tarjetaColaboradorService;
    private final TarjetaPersonaEnSituacionVulnerableService tarjetaPersonaEnSituacionVulnerableService;
    private final PersonaEnSituacionVulnerableService personaEnSituacionVulnerableService;
    private final ViandaService viandaService;
    private final ColaboradorService colaboradorService;
    private final CargaOfertaCreator cargaOfertaCreator;
    private final DistribucionViandasCreator distribucionViandasCreator;
    private final DonacionDineroCreator donacionDineroCreator;
    private final DonacionViandaCreator donacionViandaCreator;
    private final HacerseCargoDeHeladeraCreator hacerseCargoDeHeladeraCreator;
    private final RegistroDePersonaEnSituacionVulnerableCreator registroDePersonaEnSituacionVulnerableCreator;
    private final ContribucionFinder contribucionFinder;
    private final DistribucionViandasService distribucionViandasService;
    private final PosesionViandasService posesionViandasService;

    public ContribucionViewController(HeladeraService vHeladeraService, TarjetaColaboradorService vTarjetaColaboradorService, PersonaEnSituacionVulnerableService vPersonaEnSituacionVulnerableService, ViandaService vViandaService, TarjetaPersonaEnSituacionVulnerableService vTarjetaPersonaEnSituacionVulnerableService, ColaboradorService vColaboradorService, CargaOfertaCreator vCargaOfertaCreator, DistribucionViandasCreator vDistribucionViandasCreator, DonacionDineroCreator vDonacionDineroCreator, DonacionViandaCreator vDonacionViandaCreator, HacerseCargoDeHeladeraCreator vHacerseCargoDeHeladeraCreator, RegistroDePersonaEnSituacionVulnerableCreator vRegistroDePersonaEnSituacionVulnerableCreator, ContribucionFinder vContribucionFinder, DistribucionViandasService vDistribucionViandasService, PosesionViandasService vPosesionViandasService) {
        heladeraService = vHeladeraService;
        tarjetaColaboradorService = vTarjetaColaboradorService;
        personaEnSituacionVulnerableService = vPersonaEnSituacionVulnerableService;
        tarjetaPersonaEnSituacionVulnerableService = vTarjetaPersonaEnSituacionVulnerableService;
        viandaService = vViandaService;
        colaboradorService = vColaboradorService;
        cargaOfertaCreator = vCargaOfertaCreator;
        distribucionViandasCreator = vDistribucionViandasCreator;
        donacionDineroCreator = vDonacionDineroCreator;
        donacionViandaCreator = vDonacionViandaCreator;
        hacerseCargoDeHeladeraCreator = vHacerseCargoDeHeladeraCreator;
        registroDePersonaEnSituacionVulnerableCreator = vRegistroDePersonaEnSituacionVulnerableCreator;
        contribucionFinder = vContribucionFinder;
        distribucionViandasService = vDistribucionViandasService;
        posesionViandasService = vPosesionViandasService;
    }

    @GetMapping("/colaborar-personas-fisicas")
    public String mostrarColaborarPersonasFisicas(Model model) {
        setPaginaActual("/colaborar", model);

        return "colaborar-personas-fisicas";
    }

    @GetMapping("/colaborar-personas-juridicas")
    public String mostrarColaborarPersonasJuridicas(Model model) {
        setPaginaActual("/colaborar", model);

        return "colaborar-personas-juridicas";
    }

    @GetMapping("/cargar-premio")
    public String mostrarFormCargaOferta(Model model) {
        setPaginaActual("/colaborar", model);
        model.addAttribute("oferta", new Oferta());

        return "cargar-premio";
    }

    @PostMapping("/cargar-premio/guardar")
    public String guardarCargaOferta(@ModelAttribute Oferta oferta) throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException,DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        colaboradorService.colaborar(colaborador.getId(), cargaOfertaCreator, LocalDateTime.now(), oferta);

        return "redirect:/cargar-premio";
    }

    @GetMapping("/distribuir-viandas")
    public String mostrarFormDistribucionViandas(Model model) {
        setPaginaActual("/colaborar", model);
        List<Heladera> heladeras = heladeraService.obtenerHeladeras();
        model.addAttribute("heladeras", heladeras);

        return "distribuir-viandas";
    }

    @PostMapping("/distribuir-viandas/guardar")
    public String guardarDistribucionViandas(
        RedirectAttributes redirectAttributes,
        @RequestParam("heladera-origen") Long heladeraOrigenId,
        @RequestParam("heladera-destino") Long heladeraDestinoId,
        @RequestParam("cantidad") Integer cantidadAMover,
        @RequestParam("motivo") DistribucionViandas.MotivoDistribucion motivo)
        throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException, HeladeraVaciaSolicitudRetiroException, HeladeraLlenaSolicitudIngresoException, DatosInvalidosCrearTarjetaColaboradorException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);
        Long colaboradorId = colaborador.getId();

        TarjetaColaborador tarjeta = colaborador.getTarjeta();

        // Un Colaborador no puede solicitar una Contribución de viandas si tiene otra pendiente
        if (!colaboradorService.donacionesYDistribucionesNoConfirmadas(colaboradorId).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", 1);
            return "redirect:/distribuir-viandas";
        }

        Heladera heladeraOrigen = heladeraService.obtenerHeladera(heladeraOrigenId);
        Heladera heladeraDestino = heladeraService.obtenerHeladera(heladeraDestinoId);

        if (tarjeta.getClass() == TarjetaColaboradorNula.class) {
            tarjeta = tarjetaColaboradorService.crearTarjeta(colaboradorId);
            colaboradorService.agregarTarjeta(colaboradorId, tarjeta);
        }

        // Verifico que la Heladera origen no esté vacía (o pueda vaciarse por reservas) y genero la Solicitud de Apertura y el Permiso de Apertura correspondientes
        try {
            tarjetaColaboradorService.solicitarApertura(tarjeta.getCodigo(), heladeraOrigen, SolicitudAperturaColaborador.MotivoSolicitud.RETIRAR_LOTE_DE_DISTRIBUCION, cantidadAMover);
        } catch (HeladeraVaciaSolicitudRetiroException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            return "redirect:/distribuir-viandas";
        }

        // Verifico que la Heladera destino no esté llena (o pueda llenarse por reservas) y genero la Solicitud de Apertura y el Permiso de Apertura correspondientes
        try {
            tarjetaColaboradorService.solicitarApertura(tarjeta.getCodigo(), heladeraDestino, SolicitudAperturaColaborador.MotivoSolicitud.INGRESAR_LOTE_DE_DISTRIBUCION, cantidadAMover);
        } catch (HeladeraLlenaSolicitudIngresoException e) {
            redirectAttributes.addFlashAttribute("error", 3);
            return "redirect:/distribuir-viandas";
        }

        colaboradorService.colaborar(colaboradorId, distribucionViandasCreator, LocalDateTime.now(), heladeraOrigen, heladeraDestino, cantidadAMover, motivo);

        heladeraService.reservarViandas(heladeraOrigenId, cantidadAMover);
        heladeraService.reservarEspacioParaViandas(heladeraDestinoId, cantidadAMover);

        redirectAttributes.addFlashAttribute("error", 5);

        return "redirect:/distribuir-viandas";
    }

    @GetMapping("/donar-dinero")
    public String mostrarFormDonacionDinero(Model model) {
        setPaginaActual("/colaborar", model);
        return "donar-dinero";
    }

    @PostMapping("/donar-dinero/guardar")
    public String guardarDonacionDinero(
        @RequestParam(value = "monto", defaultValue = "0") Double monto,
        @RequestParam(value = "monto-personalizado", defaultValue = "0") Double montoPersonalizado,
        @RequestParam("frecuencia") DonacionDinero.FrecuenciaDePago frecuencia)
        throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException,DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException
    {
        Double montoFinal = Math.max(monto, montoPersonalizado);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        colaboradorService.colaborar(colaborador.getId(), donacionDineroCreator, LocalDateTime.now(), montoFinal, frecuencia);

        return "redirect:/donar-dinero";
    }

    @GetMapping("/donar-vianda")
    public String mostrarFormDonacionVianda(Model model) {
        setPaginaActual("/colaborar", model);
        List<Heladera> heladeras = heladeraService.obtenerHeladeras();
        model.addAttribute("heladeras", heladeras);

        return "donar-vianda";
    }

    @PostMapping("/donar-vianda/guardar")
    public String guardarDonacionVianda(
        RedirectAttributes redirectAttributes,
        @RequestParam("comida") String comida,
        @RequestParam("heladera") Long heladeraId,
        @RequestParam("fecha-caducidad") LocalDate fechaCaducidad,
        @RequestParam("calorias") Integer calorias,
        @RequestParam("peso") Integer peso)
        throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException, HeladeraVaciaSolicitudRetiroException, HeladeraLlenaSolicitudIngresoException, DatosInvalidosCrearTarjetaColaboradorException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);
        Long colaboradorId = colaborador.getId();

        TarjetaColaborador tarjeta = colaborador.getTarjeta();

        // Un Colaborador no puede solicitar una Contribución de viandas si tiene otra pendiente
        if (!colaboradorService.donacionesYDistribucionesNoConfirmadas(colaboradorId).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", 1);
            return "redirect:/donar-vianda";
        }

        Heladera heladera = heladeraService.obtenerHeladera(heladeraId);

        if (tarjeta.getClass() == TarjetaColaboradorNula.class) {
            tarjeta = tarjetaColaboradorService.crearTarjeta(colaboradorId);
            colaboradorService.agregarTarjeta(colaboradorId, tarjeta);
        }

        // Verifico que la Heladera origen no esté llena (o pueda vaciarse por reservas) y genero la Solicitud de Apertura y el Permiso de Apertura correspondientes
        try {
            tarjetaColaboradorService.solicitarApertura(tarjeta.getCodigo(), heladera, SolicitudAperturaColaborador.MotivoSolicitud.INGRESAR_DONACION, 1);
        } catch (HeladeraLlenaSolicitudIngresoException e) {
            redirectAttributes.addFlashAttribute("error", 3);
            return "redirect:/donar-vianda";
        }

        Vianda vianda = new Vianda(comida, colaborador, fechaCaducidad, LocalDateTime.now(), calorias, peso, false);
        viandaService.guardarVianda(vianda);

        colaboradorService.colaborar(colaboradorId, donacionViandaCreator, LocalDateTime.now(), vianda, heladera);

        heladeraService.reservarEspacioParaViandas(heladeraId, 1);

        redirectAttributes.addFlashAttribute("error", 5);

        return "redirect:/donar-vianda";
    }

    @GetMapping("/colocar-heladera")
    public String mostrarFormHacerseCargoDeHeladera(Model model) {
        setPaginaActual("/colaborar", model);
        model.addAttribute("heladera", new Heladera());

        return "colocar-heladera";
    }

    @PostMapping("/colocar-heladera/guardar")
    public String guardarHacerseCargoDeHeladera(
        @RequestParam String nombre,
        @RequestParam("calle") String calle,
        @RequestParam("altura") String altura,
        @RequestParam("ciudad") String ciudad,
        @RequestParam("codigo-postal") String codigoPostal,
        @RequestParam("temp-minima") Float tempMinima,
        @RequestParam("temp-maxima") Float tempMaxima,
        @RequestParam("capacidad") Integer capacidad,
        @RequestParam("latitud") Double latitud,
        @RequestParam("longitud") Double longitud)
        throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException,DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException
    {
        Ubicacion ubicacion = new Ubicacion(latitud, longitud, (calle + " " + altura), codigoPostal, ciudad, "Argentina");
        Heladera heladera = new Heladera(nombre, ubicacion, capacidad, tempMinima, tempMaxima, new ArrayList<>(), null, LocalDateTime.now(), false);
        heladera.setUbicacion(ubicacion);
        heladera.setFechaApertura(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        colaboradorService.colaborar(colaborador.getId(), hacerseCargoDeHeladeraCreator, LocalDateTime.now(), heladera);

        return "redirect:/colocar-heladera";
    }

    @GetMapping("/registrar-persona-vulnerable")
    public String mostrarFormRegistroDePersonaEnSituacionVulnerable(Model model) {
        setPaginaActual("/colaborar", model);

        return "registrar-persona-vulnerable";
    }

    @PostMapping("/registrar-persona-vulnerable/guardar")
    public String guardarRegistroDePersonaEnSituacionVulnerable(
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("fecha-nacimiento") LocalDate fechaNacimiento,
        @RequestParam("tipo-documento") Documento.TipoDocumento tipoDocumento,
        @RequestParam("numero-documento") String numeroDocumento,
        @RequestParam("sexo-documento") Documento.Sexo sexoDocumento,
        @RequestParam(value = "posee-domicilio", defaultValue = "false") Boolean poseeDomicilio,
        @RequestParam(value = "calle", defaultValue = "sin calle") String calle,
        @RequestParam(value = "altura", defaultValue = "sin altura") String altura,
        @RequestParam(value = "ciudad", defaultValue = "sin ciudad") String ciudad,
        @RequestParam(value = "codigo-postal", defaultValue = "sin código postal") String codigoPostal,
        @RequestParam(value = "menores-a-cargo", defaultValue = "0") Integer menoresACargo)
        throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException, DatosInvalidosCrearTarjetaPESVException
    {
        Documento documento = new Documento(tipoDocumento, numeroDocumento, sexoDocumento);
        PersonaFisica personaFisica = new PersonaFisica(nombre, apellido, documento, fechaNacimiento);

        Ubicacion domicilio = poseeDomicilio ? new Ubicacion(null, null, (calle + " " + altura), codigoPostal, ciudad, "Argentina") : null; // TODO: Hay que arreglar esto, porque cuando ponemos que no tiene domicilio tira error

        LocalDateTime ahora = LocalDateTime.now();

        PersonaEnSituacionVulnerable personaEnSituacionVulnerable = new PersonaEnSituacionVulnerable(personaFisica, domicilio, ahora, menoresACargo, poseeDomicilio);
        Long personaEnSituacionVulnerableId = personaEnSituacionVulnerableService.guardarPersonaEnSituacionVulnerable(personaEnSituacionVulnerable).getId();

        TarjetaPersonaEnSituacionVulnerable tarjetaPersonaEnSituacionVulnerable = tarjetaPersonaEnSituacionVulnerableService.crearTarjeta(personaEnSituacionVulnerableId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        colaboradorService.colaborar(colaborador.getId(), registroDePersonaEnSituacionVulnerableCreator, ahora, tarjetaPersonaEnSituacionVulnerable);

        return "redirect:/registrar-persona-vulnerable";
    }

    @GetMapping("/contribuciones")
    public String mostrarContribuciones(Model model) {
        setPaginaActual("/contribuciones", model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);

        model.addAttribute("colaborador", colaborador);

        return "contribuciones";
    }

    @GetMapping("/confirmar-contribucion/{id}")
    public String confirmarContribucion(@PathVariable Long id) {
        Contribucion contribucion = contribucionFinder.obtenerContribucion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Colaborador colaborador = colaboradorService.obtenerColaboradorPorUsername(username);
        colaboradorService.confirmarContribucion(colaborador.getId(), contribucion, LocalDateTime.now());

        return "redirect:/contribuciones";
    }

    @GetMapping("/confirmar-retiro-viandas/{id}")
    public String confirmarRetiroViandas(@PathVariable Long id) throws PermisoAperturaAusenteException, PermisoAperturaExpiradoException, HeladeraVaciaRetirarViandaException {
        DistribucionViandas distribucionViandas = (DistribucionViandas) contribucionFinder.obtenerContribucion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        TarjetaColaborador tarjeta = colaborador.getTarjeta();

        Heladera heladera = distribucionViandas.getOrigen();
        Integer cantidadViandas = distribucionViandas.getCantidadViandasAMover();

        // Técnicamente el intento de Apertura ocurrió antes de que se ejecute este flujo de código, pero como no tenemos heladeras reales, esta es la única forma de hacerlo (necesitamos llamar al método intentarApertura para generar y guardar la Apertura)
        tarjetaColaboradorService.intentarApertura(tarjeta.getCodigo(), heladera);

        List<Vianda> viandasACargo = new ArrayList<>();

        for (Integer i = 1; i <= cantidadViandas; i++) {
            Vianda viandaAux = heladeraService.retirarVianda(heladera.getId());
            viandaService.quitarDeHeladera(viandaAux.getId());
            viandasACargo.add(viandaAux);
        }

        PosesionViandas posesionViandas = posesionViandasService.crearPosesionViandas(colaborador, viandasACargo, LocalDateTime.now());
        posesionViandasService.guardarPosesionViandas(posesionViandas);

        distribucionViandasService.confirmarRetiro(distribucionViandas.getId());

        colaboradorService.retirarViandas(colaborador.getId());

        return "redirect:/contribuciones";
    }

    @GetMapping("/confirmar-ingreso-viandas/{id}")
    public String confirmarIngresoViandas(@PathVariable Long id) throws PermisoAperturaAusenteException, PermisoAperturaExpiradoException, HeladeraLlenaAgregarViandaException {
        DistribucionViandas distribucionViandas = (DistribucionViandas) contribucionFinder.obtenerContribucion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        TarjetaColaborador tarjeta = colaborador.getTarjeta();

        Heladera heladera = distribucionViandas.getDestino();
        Integer cantidadViandas = distribucionViandas.getCantidadViandasAMover();

        // Técnicamente el intento de Apertura ocurrió antes de que se ejecute este flujo de código, pero como no tenemos heladeras reales, esta es la única forma de hacerlo (necesitamos llamar al método intentarApertura para generar y guardar la Apertura)
        tarjetaColaboradorService.intentarApertura(tarjeta.getCodigo(), heladera);

        // Me traigo la posesión más reciente porque ya sé que no puede haber más de una posesión "activa" a la vez
        PosesionViandas posesionViandas = posesionViandasService.obtenerPosesionViandasMasRecientePorColaborador(colaborador);

        // Copio el array en vez de quitar directamente del array de posesionViandas porque quiero que esta se persista correctamente en la posteridad (no sin viandas)
        List<Vianda> viandasACargo = posesionViandas.getViandas();

        for (Integer i = 1; i <= cantidadViandas; i++) {
            Vianda viandaAux = viandasACargo.removeFirst();
            heladeraService.agregarVianda(heladera.getId(), viandaAux);
            viandaService.agregarAHeladera(viandaAux.getId(), heladera);
        }

        colaboradorService.ingresarViandas(colaborador.getId());
        colaboradorService.confirmarContribucion(colaborador.getId(), distribucionViandas, LocalDateTime.now());

        return "redirect:/contribuciones";
    }

    @GetMapping("/confirmar-ingreso-vianda/{id}")
    public String confirmarIngresoVianda(@PathVariable Long id) throws PermisoAperturaAusenteException, PermisoAperturaExpiradoException, HeladeraLlenaAgregarViandaException {
        DonacionVianda donacionVianda = (DonacionVianda) contribucionFinder.obtenerContribucion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumanoPorUsername(username);

        TarjetaColaborador tarjeta = colaborador.getTarjeta();
        Heladera heladera = donacionVianda.getHeladera();
        Vianda vianda = donacionVianda.getVianda();

        // Técnicamente el intento de Apertura ocurrió antes de que se ejecute este flujo de código, pero como no tenemos heladeras reales, esta es la única forma de hacerlo (necesitamos llamar al método intentarApertura para generar y guardar la Apertura)
        tarjetaColaboradorService.intentarApertura(tarjeta.getCodigo(), heladera);

        heladeraService.agregarVianda(heladera.getId(), vianda);
        viandaService.agregarAHeladeraPrimeraVez(vianda.getId(), heladera);

        colaboradorService.confirmarContribucion(colaborador.getId(), donacionVianda, LocalDateTime.now());

        return "redirect:/contribuciones";
    }

    @GetMapping("/confirmar-colocacion-heladera/{id}")
    public String confirmarColocacionHeladera(@PathVariable Long id) {
        HacerseCargoDeHeladera hacerseCargoDeHeladera = (HacerseCargoDeHeladera) contribucionFinder.obtenerContribucion(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        ColaboradorJuridico colaborador = colaboradorService.obtenerColaboradorJuridicoPorUsername(username);

        Heladera heladera = hacerseCargoDeHeladera.getHeladeraObjetivo();

        heladeraService.marcarComoActiva(heladera.getId());
        heladeraService.actualizarFechaApertura(heladera.getId());

        colaboradorService.confirmarContribucion(colaborador.getId(), hacerseCargoDeHeladera, LocalDateTime.now());

        return "redirect:/contribuciones";
    }

    public void setPaginaActual(String pagina, Model model) {
        model.addAttribute("paginaActual", pagina);
    }
}
