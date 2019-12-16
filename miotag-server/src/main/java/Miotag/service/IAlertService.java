package Miotag.service;

import Miotag.dto.AlertDto;
import Miotag.model.Alert;

import java.util.List;

public interface IAlertService {
    Alert newAlert(Alert alert);
    List<AlertDto> getAlerts(String name);
    boolean deleteAlert(String name, AlertDto alertDto);
}
