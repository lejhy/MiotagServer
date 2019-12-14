package Miotag.service;

import Miotag.dto.AlertDto;
import Miotag.model.Alert;
import Miotag.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService implements IAlertService {

    private final AlertRepository alertRepository;
    private final ISecurityService securityService;

    @Autowired
    public AlertService(AlertRepository alertRepository, ISecurityService securityService) {
        this.alertRepository = alertRepository;
        this.securityService = securityService;
    }

    @Override
    public List<Alert> getAlerts(String name) {
        return alertRepository.findAllByUser(securityService.findUser(name));
    }

    @Override
    public Alert newAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public boolean deleteAlert(String name, AlertDto alertDto) {
        securityService.findUser(name);
        if (alertRepository.existsById(alertDto.getId())) {
            alertRepository.deleteById(alertDto.getId());
            return true;
        } else {
            return false;
        }
    }
}
