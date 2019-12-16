package Miotag.service;

import Miotag.dto.AlertDto;
import Miotag.mapper.AlertMapper;
import Miotag.model.Alert;
import Miotag.model.User;
import Miotag.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService implements IAlertService {

    private final AlertRepository alertRepository;
    private final ISecurityService securityService;
    private final AlertMapper alertMapper;

    @Autowired
    public AlertService(AlertRepository alertRepository, ISecurityService securityService, AlertMapper alertMapper) {
        this.alertRepository = alertRepository;
        this.securityService = securityService;
        this.alertMapper = alertMapper;
    }

    @Override
    public List<AlertDto> getAlerts(String name) {
        User user = securityService.findUser(name);
        List<Alert> alerts = alertRepository.findAllByUser(user);
        return alerts.stream().map(alertMapper::map).collect(Collectors.toList());
    }

    @Override
    public Alert newAlert(Alert alert) { //TODO consider extracting into separate service to prevent bleeding entity declarations into controller
        alert.setId(0);
        alert.setDate(new Date());
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
