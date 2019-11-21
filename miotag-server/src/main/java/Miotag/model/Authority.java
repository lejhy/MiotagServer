package Miotag.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private long id;
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
