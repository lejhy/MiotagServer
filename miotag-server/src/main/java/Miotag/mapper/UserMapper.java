package Miotag.mapper;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.createTypeMap(UserDto.class, User.class)
                .setPropertyCondition(Conditions.isNotNull())
                .addMappings(mapper -> mapper.skip(User::setId));
        modelMapper.createTypeMap(User.class, UserDto.class)
                .addMappings(mapper -> mapper.skip(UserDto::setPassword));
    }

    public User map(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto map(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public void map(UserDto userDto, User user) { modelMapper.map(userDto, user); }
}
