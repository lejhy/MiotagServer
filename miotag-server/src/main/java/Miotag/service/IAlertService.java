package Miotag.service;

import Miotag.dto.AlertDto;
import Miotag.model.Alert;

import java.util.List;

public interface IAlertService {
    List<Alert> getAlerts(String name);
    Alert newAlert(Alert alert);
    boolean deleteAlert(String name, AlertDto alertDto);
}
