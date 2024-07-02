package ca.qc.ircm.proview.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link LaboratoryService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class LaboratoryServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  private static final String WRITE = "write";
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(LaboratoryServiceTest.class);
  @Autowired
  private LaboratoryService service;
  @Autowired
  private LaboratoryRepository repository;
  @MockBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() throws Throwable {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_Id() throws Throwable {
    Laboratory laboratory = service.get(2L).get();

    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(READ));
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals("Translational Proteomics", laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void get_NullId() throws Throwable {
    assertFalse(service.get((Long) null).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void all() throws Throwable {
    List<Laboratory> laboratories = service.all();

    assertEquals(4, laboratories.size());
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void all_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.all();
    });
  }

  @Test
  public void save_New() {
    Laboratory laboratory = new Laboratory();
    laboratory.setName("Test laboratory");

    service.save(laboratory);

    repository.flush();
    assertNotNull(laboratory.getId());
    laboratory = repository.findById(laboratory.getId()).get();
    assertEquals("Test laboratory", laboratory.getName());
    assertEquals(null, laboratory.getDirector());
    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
  }

  @Test
  public void save_Update() {
    Laboratory laboratory = repository.findById(1L).get();
    laboratory.setName("Test laboratory");

    service.save(laboratory);

    repository.flush();
    assertNotNull(laboratory.getId());
    laboratory = repository.findById(laboratory.getId()).get();
    assertEquals("Test laboratory", laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
  }
}
