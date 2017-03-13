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

package ca.qc.ircm.proview.cache;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class CacheFlusherTest {
  private CacheFlusher cacheFlusher;
  @Mock
  private CachingSecurityManager securityManager;
  @Mock
  private CacheManager cacheManager;
  @Mock
  private Cache<Object, Object> cache;
  @Mock
  private SecurityConfiguration securityConfiguration;
  private String authorizationCacheName = "authorizationCache";

  @Before
  public void beforeTest() {
    cacheFlusher = new CacheFlusher(securityConfiguration);
    when(securityConfiguration.authorizationCacheName()).thenReturn(authorizationCacheName);
  }

  @Test
  public void flushShiroCache() {
    ThreadContext.bind(securityManager);
    when(securityManager.getCacheManager()).thenReturn(cacheManager);
    when(cacheManager.getCache(any())).thenReturn(cache);

    cacheFlusher.flushShiroCache();

    verify(securityManager).getCacheManager();
    verify(securityConfiguration).authorizationCacheName();
    verify(cacheManager).getCache(authorizationCacheName);
    verify(cache).clear();
  }
}
