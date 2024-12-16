package com.tp_anual.proyecto_heladeras_solidarias.service.migrador;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.migrador.FilaDeDatosIncompletaException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.Usuario;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.usuario.CustomUserDetailsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log
public class Migrador {

    private final ExtraccionDeDatos protocoloExtraccion;
    private final TransformacionDeDatos transformador;
    private final EnvioDeDatos protocoloEnvio;
    private final ColaboradorService colaboradorService;
    private final CustomUserDetailsService customUserDetailsService;

    private final String ASUNTO = "Gracias por tu apoyo! Aquí están tus credenciales de acceso al nuevo Sistema";

    private final String CUERPO =
            """
            Estimado/a %s,

            Nos complace informarte que hemos recibido tu colaboración. Gracias por tu compromiso con nuestra causa!

            Como parte de nuestro esfuerzo por mejorar y simplificar la gestión de nuestros donantes y colaboradores, hemos migrado nuestros datos a un nuevo sistema.
            Te creamos y asignamos una cuenta para que puedas seguir colaborando y mantenerte informado sobre nuestras actividades.

            A continuación, te proporcionamos tus credenciales de acceso:

            Usuario: %s
            Contraseña temporal (modificable): %%s (completar y quitar el primer "%" al completar)

            Por favor, seguí estos pasos para acceder a tu cuenta:
            1. Visita nuestro sitio web %%s (completar y quitar el primer "%" al completar)
            2. Inicia sesión con las credenciales proporcionadas.
            3. Se te pedirá que confirmes tus datos y completes cualquier información faltante.

            Tu colaboración es fundamental para nosotros y queremos asegurarnos de que tus datos estén correctos en nuestro sistema.
            Una vez que hayas ingresado, vas a poder verificar y actualizar tu información personal (incluyendo tu contraseña).

            Si tenes alguna pregunta o necesitas asistencia, no dudes en contactarnos a %%s (completar y quitar el primer "%" al completar) o al %%s (completar y quitar el primer "%" al completar).

            Gracias nuevamente por tu apoyo.

            Atentamente,

            [Nombre del Responsable]
            [Cargo]
            [Nombre de la ONG]
            [Datos de Contacto de la ONG]
            """;

    private final I18nService i18nService;

    public Migrador(@Qualifier("extraccionCSV") ExtraccionDeDatos vProtocoloExtraccion, TransformacionDeDatos vTransformador, @Qualifier("envioEMail") EnvioDeDatos vProtocoloEnvio, ColaboradorService vColaboradorService, CustomUserDetailsService vCustomUserDetailsService, I18nService vI18nService) {
        protocoloExtraccion = vProtocoloExtraccion;
        transformador = vTransformador;
        protocoloEnvio = vProtocoloEnvio;
        colaboradorService = vColaboradorService;
        customUserDetailsService = vCustomUserDetailsService;

        i18nService = vI18nService;
    }

    public void confirmarLoading() {
        log.log(Level.INFO, i18nService.getMessage("migrador.Migrador.confirmarLoading_info"));
    }

    // Este método usa una ruta
    public List<ColaboradorHumano> migrar(String csv, Boolean contactar) throws IOException, URISyntaxException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, FilaDeDatosIncompletaException {
        List<String[]> dataColaboradores = protocoloExtraccion.extract(csv);
        List<ColaboradorHumano> colaboradoresAMigrar = transformador.transform(dataColaboradores);

        for (ColaboradorHumano colaborador : colaboradoresAMigrar) {
            colaboradorService.guardarColaborador(colaborador);

            if (contactar)
                protocoloEnvio.send(colaborador, ASUNTO, CUERPO);
        }

        confirmarLoading();

        return colaboradoresAMigrar;
    }

    // Esta método usa directamente un archivo
    public List<ColaboradorHumano> migrar(MultipartFile file, Boolean contactar) throws IOException, URISyntaxException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, FilaDeDatosIncompletaException {
        List<String[]> dataColaboradores = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            dataColaboradores = protocoloExtraccion.extract(inputStream);
        }

        List<ColaboradorHumano> colaboradoresAMigrar = transformador.transform(dataColaboradores);

        for (ColaboradorHumano colaborador : colaboradoresAMigrar) {
            Usuario usuario = colaborador.getUsuario();
            customUserDetailsService.guardarUsuario(usuario);

            colaboradorService.guardarColaborador(colaborador);

            // Tengo que asignar el Colaborador a las Contribuciones después de guardarlo para no tener problemas de persistencia cíclica
            for (Contribucion contribucion : colaborador.getContribuciones()) {
                contribucion.setColaborador(colaborador);
            }

            // Guardo nuevamente el Colaborador para que se guarde la modificación de cada Contribución por cascada
            colaboradorService.guardarColaborador(colaborador);

            if (contactar)
                protocoloEnvio.send(colaborador, ASUNTO, CUERPO);
        }

        confirmarLoading();

        return colaboradoresAMigrar;
    }
}
