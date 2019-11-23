package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.exception.EmailExistsException;
import Miotag.mapper.UserMapper;
import Miotag.model.User;
import Miotag.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto registerUser(UserDto userDto) throws EmailExistsException {
        if (emailExist(userDto.getEmail())) {
            throw new EmailExistsException();
        }
        User user = prepareNewUser(userDto);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userMapper.map(findUser(email));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        if (!email.equals(user.getEmail()) && emailExist(userDto.getEmail())) {
            throw new EmailExistsException();
        }
        userMapper.map(userDto, user);
        return userMapper.map(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findUser(email);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("No user found with username " + email)
        );
    }

    private boolean emailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User prepareNewUser(UserDto userDto) {
        User user = userMapper.map(userDto);
        user.setId(0);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
}