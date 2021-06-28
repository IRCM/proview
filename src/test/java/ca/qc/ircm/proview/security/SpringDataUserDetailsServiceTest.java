/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserAuthority.FORCE_CHANGE_PASSWORD;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.InitializeDatabaseExecutionListener;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Tests for {@link SpringDataUserDetailsService}.
 */
@NonTransactionalTestAnnotations
public class SpringDataUserDetailsServiceTest {
  private SpringDataUserDetailsService userDetailsService;
  @Mock
  private UserRepository userRepository;
  private User user;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    userDetailsService = new SpringDataUserDetailsService(userRepository);
    user = new User();
    user.setId(2L);
    user.setEmail("proview@ircm.qc.ca");
    user.setName("A User");
    user.setHashedPassword(InitializeDatabaseExecutionListener.PASSWORD_PASS1);
    user.setActive(true);
    user.setLaboratory(new Laboratory(1L));
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
  }

  private Optional<? extends GrantedAuthority>
      findAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
    return authorities.stream().filter(autho -> autho.getAuthority().equals(authority)).findFirst();
  }

  @Test
  public void loadUserByUsername() {
    UserDetails userDetails = userDetailsService.loadUserByUsername("proview@ircm.qc.ca");

    verify(userRepository).findByEmail("proview@ircm.qc.ca");
    assertEquals("proview@ircm.qc.ca", userDetails.getUsername());
    assertEquals(InitializeDatabaseExecutionListener.PASSWORD_PASS1, userDetails.getPassword());
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    assertEquals(2, authorities.size());
    for (GrantedAuthority authority : authorities) {
      assertTrue(authority instanceof SimpleGrantedAuthority);
    }
    assertTrue(findAuthority(authorities, USER).isPresent());
    assertTrue(findAuthority(authorities, UserAuthority.laboratoryMember(user.getLaboratory()))
        .isPresent());
    assertTrue(userDetails.isEnabled());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails instanceof AuthenticatedUser);
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) userDetails;
    assertEquals((Long) 2L, authenticatedUser.getId());
  }

  @Test
  public void loadUserByUsername_Admin() {
    user.setAdmin(true);

    UserDetails userDetails = userDetailsService.loadUserByUsername("proview@ircm.qc.ca");

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    assertEquals(3, authorities.size());
    for (GrantedAuthority authority : authorities) {
      assertTrue(authority instanceof SimpleGrantedAuthority);
    }
    assertTrue(findAuthority(authorities, USER).isPresent());
    assertTrue(findAuthority(authorities, UserAuthority.laboratoryMember(user.getLaboratory()))
        .isPresent());
    assertTrue(findAuthority(authorities, ADMIN).isPresent());
  }

  @Test
  public void loadUserByUsername_Manager() {
    user.setManager(true);

    UserDetails userDetails = userDetailsService.loadUserByUsername("proview@ircm.qc.ca");

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    assertEquals(3, authorities.size());
    for (GrantedAuthority authority : authorities) {
      assertTrue(authority instanceof SimpleGrantedAuthority);
    }
    assertTrue(findAuthority(authorities, USER).isPresent());
    assertTrue(findAuthority(authorities, UserAuthority.laboratoryMember(user.getLaboratory()))
        .isPresent());
    assertTrue(findAuthority(authorities, MANAGER).isPresent());
  }

  @Test
  public void loadUserByUsername_AdminManager() {
    user.setAdmin(true);
    user.setManager(true);

    UserDetails userDetails = userDetailsService.loadUserByUsername("proview@ircm.qc.ca");

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    assertEquals(4, authorities.size());
    for (GrantedAuthority authority : authorities) {
      assertTrue(authority instanceof SimpleGrantedAuthority);
    }
    assertTrue(findAuthority(authorities, USER).isPresent());
    assertTrue(findAuthority(authorities, UserAuthority.laboratoryMember(user.getLaboratory()))
        .isPresent());
    assertTrue(findAuthority(authorities, ADMIN).isPresent());
    assertTrue(findAuthority(authorities, MANAGER).isPresent());
  }

  @Test
  public void loadUserByUsername_NotExists() {
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailsService.loadUserByUsername("proview@ircm.qc.ca");
    });
  }

  @Test
  @Disabled("Expired password not supported")
  public void loadUserByUsername_ExpiredPassword() {
    //user.setExpiredPassword(true);

    UserDetails userDetails = userDetailsService.loadUserByUsername("proview@ircm.qc.ca");

    assertEquals("proview@ircm.qc.ca", userDetails.getUsername());
    assertEquals(InitializeDatabaseExecutionListener.PASSWORD_PASS1, userDetails.getPassword());
    List<? extends GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
    assertEquals(3, authorities.size());
    for (GrantedAuthority authority : authorities) {
      assertTrue(authority instanceof SimpleGrantedAuthority);
    }
    assertTrue(findAuthority(authorities, USER).isPresent());
    assertTrue(findAuthority(authorities, UserAuthority.laboratoryMember(user.getLaboratory()))
        .isPresent());
    assertTrue(findAuthority(authorities, FORCE_CHANGE_PASSWORD).isPresent());
    assertTrue(userDetails.isEnabled());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
  }
}
