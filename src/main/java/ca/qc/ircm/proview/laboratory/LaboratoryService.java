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

package ca.qc.ircm.proview.laboratory;

import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.InvalidUserException;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserNotMemberOfLaboratoryException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Laboratory service class.
 */
@Service
@Transactional
public class LaboratoryService {
  private final Logger logger = LoggerFactory.getLogger(LaboratoryService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private CacheFlusher cacheFlusher;
  @Inject
  private AuthorizationService authorizationService;

  protected LaboratoryService() {
  }

  protected LaboratoryService(EntityManager entityManager, CacheFlusher cacheFlusher,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.cacheFlusher = cacheFlusher;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects laboratory from database.
   *
   * @param id
   *          database identifier of laboratory
   * @return laboratory
   */
  public Laboratory get(Long id) {
    if (id == null) {
      return null;
    }

    Laboratory laboratory = entityManager.find(Laboratory.class, id);
    authorizationService.checkLaboratoryReadPermission(laboratory);
    return laboratory;
  }

  /**
   * Update laboratory.<br>
   * <strong> Manager will not change. </strong><br>
   * <strong> Field indicating if laboratory is inside IRCM will not change. </strong>
   *
   * @param laboratory
   *          Laboratory with new information.
   * @throws UnauthorizedException
   *           Exception user's laboratory must match signed user's laboratory
   */
  public void update(Laboratory laboratory) {
    authorizationService.checkLaboratoryManagerPermission(laboratory);

    entityManager.merge(laboratory);

    logger.info("Laboratory {} updated", laboratory);
  }

  /**
   * Add user to laboratory's managers.
   *
   * @param laboratory
   *          laboratory
   * @param user
   *          new manager
   * @throws UserNotMemberOfLaboratoryException
   *           if user is not a member of laboratory
   * @throws InvalidUserException
   *           user is invalid and cannot be a manager of laboratory
   */
  public void addManager(Laboratory laboratory, User user)
      throws UserNotMemberOfLaboratoryException, InvalidUserException {
    authorizationService.checkAdminRole();

    laboratory = entityManager.merge(laboratory);
    entityManager.refresh(laboratory);
    user = entityManager.merge(user);
    entityManager.refresh(user);

    if (!laboratory.equals(user.getLaboratory())) {
      throw new UserNotMemberOfLaboratoryException();
    }
    if (!user.isValid()) {
      throw new InvalidUserException();
    }

    if (!laboratory.getManagers().contains(user)) {
      laboratory.getManagers().add(user);
    }
    user.setActive(true);

    cacheFlusher.flushShiroCache();
  }

  /**
   * Remove manager from laboratory's managers.
   *
   * @param laboratory
   *          laboratory
   * @param manager
   *          manager
   * @throws UserNotMemberOfLaboratoryException
   *           if manager is not a member of laboratory
   * @throws UnmanagedLaboratoryException
   *           if laboratory would have no more manager if manager is removed
   */
  public void removeManager(Laboratory laboratory, User manager)
      throws UserNotMemberOfLaboratoryException, UnmanagedLaboratoryException {
    authorizationService.checkAdminRole();

    laboratory = entityManager.merge(laboratory);
    entityManager.refresh(laboratory);
    manager = entityManager.merge(manager);
    entityManager.refresh(manager);

    if (!laboratory.equals(manager.getLaboratory())) {
      throw new UserNotMemberOfLaboratoryException();
    }

    if (laboratory.getManagers().contains(manager) && laboratory.getManagers().size() <= 1) {
      throw new UnmanagedLaboratoryException();
    } else {
      laboratory.getManagers().remove(manager);
    }

    cacheFlusher.flushShiroCache();
  }
}
