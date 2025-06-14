package tl.gov.mci.lis.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomUserDetails implements UserDetails {
    // setters for Jackson
    private String username;
    private String password;
    @Getter
    private List<String> roleNames;
    private boolean enabled = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;

    public CustomUserDetails() {
    }

    public CustomUserDetails(String username, String password,
                             List<String> authorities) {
        this.username = username;
        this.password = password;
        this.roleNames = authorities;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleNames == null ? List.of() :
                roleNames.stream().map(SimpleGrantedAuthority::new).toList();
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
