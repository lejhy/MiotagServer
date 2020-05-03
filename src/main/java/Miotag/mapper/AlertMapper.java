package Miotag.mapper;

import Miotag.dto.AlertDto;
import Miotag.model.Alert;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {
    private ModelMapper modelMapper;

    @Autowired
    public AlertMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.createTypeMap(AlertDto.class, Alert.class)
                .addMappings(mapper -> mapper.map(AlertDto::getUser, Alert::setUser));
    }

    public Alert map(AlertDto AlertDto) {
        return modelMapper.map(AlertDto, Alert.class);
    }

    public AlertDto map(Alert Alert) {
        return modelMapper.map(Alert, AlertDto.class);
    }
}
