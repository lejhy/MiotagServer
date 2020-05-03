package Miotag.mapper;

import Miotag.dto.ActivityLogDto;
import Miotag.model.ActivityLog;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {
    private ModelMapper modelMapper;

    @Autowired
    public ActivityLogMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.createTypeMap(ActivityLogDto.class, ActivityLog.class)
                .addMappings(mapper -> mapper.map(ActivityLogDto::getActivity, ActivityLog::setActivity));
    }

    public ActivityLog map(ActivityLogDto ActivityLogDto) {
        return modelMapper.map(ActivityLogDto, ActivityLog.class);
    }

    public ActivityLogDto map(ActivityLog ActivityLog) {
        return modelMapper.map(ActivityLog, ActivityLogDto.class);
    }
}
