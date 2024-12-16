package com.tp_anual.proyecto_heladeras_solidarias.service.contacto;

import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.EMail;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.repository.contacto.EMailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class EMailService extends MedioDeContactoService {

    private final EMailRepository eMailRepository;
    private final EMailSender eMailSender;

    private final I18nService i18nService;

    public EMailService(EMailRepository vEMailRepository, EMailSender vEMailSender, I18nService vI18nService) {
        super();
        eMailRepository = vEMailRepository;
        eMailSender = vEMailSender;

        i18nService = vI18nService;
    }

    @Override
    public EMail obtenerMedioDeContacto(Long eMailId) {
        return eMailRepository.findById(eMailId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<EMail> obtenerEMails() {
        return eMailRepository.findAll();
    }

    public EMail guardarMedioDeContacto(MedioDeContacto eMail) {
        return eMailRepository.save((EMail) eMail);
    }

    @Override
    @Async
    public void contactar(Long eMailId, String asunto, String cuerpo) {
        EMail eMail = obtenerMedioDeContacto(eMailId);

        eMailSender.enviarEMail(eMail.getDireccionCorreo(), asunto, cuerpo);
        log.log(Level.INFO, i18nService.getMessage("contacto.EMail.contactar_info", eMail.getDireccionCorreo()));
    }
}
