package ca.qc.ircm.proview.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
  @MockitoBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_Id() {
    Laboratory laboratory = service.get(2L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(READ));
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals("Translational Proteomics", laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void all() {
    List<Laboratory> laboratories = service.all();

    assertEquals(4, laboratories.size());
  }

  @Test
  @WithMockUser(authorities = {UserRole.MANAGER, UserRole.USER})
  public void all_AccessDenied() {
    assertThrows(AccessDeniedException.class, () -> service.all());
  }

  @Test
  public void save_New() {
    Laboratory laboratory = new Laboratory();
    laboratory.setName("Test laboratory");
    laboratory.setDirector("Test director");

    service.save(laboratory);

    repository.flush();
    assertNotEquals(0, laboratory.getId());
    laboratory = repository.findById(laboratory.getId()).orElseThrow();
    assertEquals("Test laboratory", laboratory.getName());
    assertEquals("Test director", laboratory.getDirector());
    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
  }

  @Test
  public void save_Update() {
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    laboratory.setName("Test laboratory");

    service.save(laboratory);

    repository.flush();
    laboratory = repository.findById(laboratory.getId()).orElseThrow();
    assertEquals("Test laboratory", laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
  }
}
