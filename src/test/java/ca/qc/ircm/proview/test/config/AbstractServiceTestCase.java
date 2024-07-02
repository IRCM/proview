package ca.qc.ircm.proview.test.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Additional functions for service testing.
 */
public abstract class AbstractServiceTestCase {
  @PersistenceContext
  private EntityManager entityManager;

  protected void detach(Object... entities) {
    for (Object entity : entities) {
      entityManager.detach(entity);
    }
  }

  protected void refresh(Object... entities) {
    for (Object entity : entities) {
      entityManager.refresh(entity);
    }
  }
}
