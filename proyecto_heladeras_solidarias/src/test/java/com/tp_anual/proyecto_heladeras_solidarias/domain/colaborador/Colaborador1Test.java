package com.tp_anual.proyecto_heladeras_solidarias.domain.colaborador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import com.tp_anual.proyecto_heladeras_solidarias.domain.contacto.EMail;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contacto.WhatsApp;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.HacerseCargoDeHeladeraCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.RegistroDePersonaEnSituacionVulnerableCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.domain.documento.Documento.Sexo;
import com.tp_anual.proyecto_heladeras_solidarias.domain.documento.Documento.TipoDocumento;
import com.tp_anual.proyecto_heladeras_solidarias.domain.heladera.HeladeraActiva;
import com.tp_anual.proyecto_heladeras_solidarias.domain.persona.PersonaJuridica;
import com.tp_anual.proyecto_heladeras_solidarias.domain.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.domain.tarjeta.TarjetaPersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.domain.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.i18n.I18n;

public class Colaborador1Test {
    
    @Test
    @DisplayName("Testeo la obtención de un Contacto del Colaborador")
    public void GetContactoTest() {
        ColaboradorHumano colaboradorHumano = new ColaboradorHumano(new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "1064", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d, "NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDateTime.parse("2003-01-01T00:00:00")); // Uso ColaboradorHumano porque Colaborador es abstract y el metodo es igual para ambos (Humano y Juridico)
        colaboradorHumano.darDeAlta();
        EMail eMail = new EMail("correoprueba@gmail.com");
        colaboradorHumano.agregarContacto(eMail);

        Assertions.assertEquals(colaboradorHumano.getContacto(EMail.class), eMail);
    }

    @Test
    @DisplayName("Testeo la NoSuchElementException al pasar un MedioDeContacto que no posee el Colaborador")
    public void NoSuchElementGetContactoTest() {
        ColaboradorHumano colaboradorHumano = new ColaboradorHumano(new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "1064", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d, "NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDateTime.parse("2003-01-01T00:00:00")); // Uso ColaboradorHumano porque Colaborador es abstract y el metodo es igual para ambos (Humano y Juridico)
        colaboradorHumano.darDeAlta();
        EMail eMail = new EMail("correoprueba@gmail.com");
        colaboradorHumano.agregarContacto(eMail);

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            colaboradorHumano.getContacto(WhatsApp.class);
        });
    
        Assertions.assertEquals(I18n.getMessage("colaborador.Colaborador.getContacto_exception"), exception.getMessage());
    }

    @Test
    @DisplayName("Testeo la IllegalArgumentException para Contribuciones que el ColaboradorHumano no es capaz de hacer")
    public void IllegalArgumentColaborarCHTest() {
        ColaboradorHumano colaboradorHumano = new ColaboradorHumano(new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "1064", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d, "NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDateTime.parse("2003-01-01T00:00:00"));
        colaboradorHumano.darDeAlta();
        HeladeraActiva heladera = new HeladeraActiva("HeladeraPrueba", new Ubicacion(-34.601978, -58.383865, "Tucumán 1171", "1049", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), 20, LocalDateTime.now(), -20f, 5f);
        
        HacerseCargoDeHeladeraCreator hacerseCargoDeHeladeraCreator = new HacerseCargoDeHeladeraCreator();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            colaboradorHumano.colaborar(hacerseCargoDeHeladeraCreator, LocalDateTime.now(), heladera);
        });

        Assertions.assertEquals(I18n.getMessage("colaborador.Colaborador.colaborar_exception"), exception.getMessage());
    }

    @Test
    @DisplayName("Testeo la IllegalArgumentException para Contribuciones que el ColaboradorJuridico no es capaz de hacer")
    public void IllegalArgumentColaborarCJTest() {
        ColaboradorJuridico colaboradorJuridico = new ColaboradorJuridico(new Ubicacion(-34.6098, -58.3925, "Avenida Entre Ríos", "1033", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d, "RazonSocialPrueba", "RubroPrueba", PersonaJuridica.TipoPersonaJuridica.EMPRESA);
        colaboradorJuridico.darDeAlta();
        PersonaEnSituacionVulnerable personaEnSituacionVulnerable = new PersonaEnSituacionVulnerable("NombrePruebaPESV", "ApellidoPruebaPESV", new Documento(TipoDocumento.DNI, "40123450", Sexo.MASCULINO), LocalDateTime.parse("2003-01-01T00:00:00"), new Ubicacion(-34.63927052902741, -58.50938609197106, "Avenida Rivadavia 10357", "1408", "Ciudad Autónoma de Buenos Aires", "Argentina"), LocalDateTime.now(), 2);
        TarjetaPersonaEnSituacionVulnerable tarjeta = new TarjetaPersonaEnSituacionVulnerable(null);

        RegistroDePersonaEnSituacionVulnerableCreator registroDePersonaEnSituacionVulnerableCreator = new RegistroDePersonaEnSituacionVulnerableCreator();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            colaboradorJuridico.colaborar(registroDePersonaEnSituacionVulnerableCreator, LocalDateTime.now(), tarjeta);
        });

        Assertions.assertEquals(I18n.getMessage("colaborador.Colaborador.colaborar_exception"), exception.getMessage());
    }
}
