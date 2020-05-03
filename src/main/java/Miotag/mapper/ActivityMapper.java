package Miotag.mapper;

import Miotag.dto.ActivityDto;
import Miotag.model.Activity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {
    private ModelMapper modelMapper;

    @Autowired
    public ActivityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Activity map(ActivityDto ActivityDto) {
        return modelMapper.map(ActivityDto, Activity.class);
    }

    public ActivityDto map(Activity Activity) {
        return modelMapper.map(Activity, ActivityDto.class);
    }
}
