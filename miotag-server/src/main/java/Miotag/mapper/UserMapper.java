package Miotag.mapper;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper mapper) {
        this.modelMapper = mapper;
        modelMapper.addMappings(new UserPropertyMap());
    }

    public User map(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto map(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    class UserPropertyMap extends PropertyMap<User, UserDto> {
        protected void configure() {
            skip().setPassword(null);
        }
    }
}
