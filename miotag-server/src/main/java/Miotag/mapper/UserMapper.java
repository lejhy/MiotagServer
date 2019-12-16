package Miotag.mapper;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserMapper {

    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        Converter<String, String> encode = ctx -> passwordEncoder.encode(ctx.getSource());
        modelMapper.createTypeMap(UserDto.class, User.class)
                .setPropertyCondition(Conditions.isNotNull())
                .addMappings(mapper -> mapper.when(Objects::nonNull).using(encode).map(UserDto::getPassword, User::setPassword));
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
