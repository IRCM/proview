package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService}.
 */
@Service
public class SpringDataUserDetailsService implements UserDetailsService {

  private UserRepository userRepository;

  @Autowired
  protected SpringDataUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username).orElse(null);
    if (null == user) {
      throw new UsernameNotFoundException("No user with username: " + username);
    } else {
      Collection<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(USER));
      authorities
          .add(new SimpleGrantedAuthority(UserAuthority.laboratoryMember(user.getLaboratory())));
      if (user.isAdmin()) {
        authorities.add(new SimpleGrantedAuthority(ADMIN));
      }
      if (user.isManager()) {
        authorities.add(new SimpleGrantedAuthority(MANAGER));
      }
      /*
      if (user.isExpiredPassword()) {
        authorities.add(new SimpleGrantedAuthority(FORCE_CHANGE_PASSWORD));
      }
      */
      if (user.getPasswordVersion() != null) {
        user.setHashedPassword("{" + user.getPasswordVersion() + "}" + user.getHashedPassword()
            + ShiroPasswordEncoder.SEPARATOR + user.getSalt());
      }
      return new UserDetailsWithId(user, authorities);
    }
  }
}