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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link LaboratoryPermissionEvaluator}.
 */
@ServiceTestAnnotations
public class LaboratoryPermissionEvaluatorTest {
  private static final String LABORATORY_CLASS = Laboratory.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = BasePermission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = BasePermission.WRITE;
  private LaboratoryPermissionEvaluator permissionEvaluator;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleValidator roleValidator;

  @BeforeEach
  public void beforeTest() {
    permissionEvaluator =
        new LaboratoryPermissionEvaluator(laboratoryRepository, userRepository, roleValidator);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_Member() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Read_NotMember() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() throws Throwable {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() throws Throwable {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() throws Throwable {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() throws Throwable {
    Laboratory laboratory = new Laboratory("new lab");
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_MemberManager() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_MemberNotManager() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Write_NotMember() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  public void hasPermission_NullAuthentication() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(null, laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(null, laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, laboratory, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(null, laboratory.getId(), LABORATORY_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(null, laboratory.getId(), LABORATORY_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(null, laboratory.getId(), LABORATORY_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(null, laboratory.getId(), LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Null_Anonymous() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Null() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotLaboratory() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotLongId() throws Throwable {
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        BASE_WRITE));
  }
}
