package tl.gov.mci.lis.services.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Execute LoadUserByUsername method: {}", username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username not found");
                    return new ForbiddenException("Username not found");
                });

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                List.of(user.getRole().getName())
        );

    }

}
