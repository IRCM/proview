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

package ca.qc.ircm.proview.test.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.PasswordVersion;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.util.ThreadState;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Sets Shiro's subject.
 */
public class SubjectRule implements TestRule {
  public static final int PASSWORD_VERSION = 1;
  public static final String PASSWORD_ALGORITHM = "SHA-256";
  public static final int PASSWORD_ITERATIONS = 1000;
  public static final String COOKIE_ENCRYPTION = "AcEG7RqLxcP6enoSBJKNjA==";
  private static final Logger logger = LoggerFactory.getLogger(SubjectRule.class);
  private ThreadState threadState;

  public SubjectRule() {
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        long userId = 1L;
        boolean anonymous = false;
        WithSubject withSubject = null;
        if (description.getAnnotation(WithSubject.class) != null) {
          withSubject = description.getAnnotation(WithSubject.class);
        } else if (description.getTestClass().getAnnotation(WithSubject.class) != null) {
          withSubject = description.getTestClass().getAnnotation(WithSubject.class);
        }
        if (withSubject != null) {
          userId = withSubject.userId();
          anonymous = withSubject.anonymous();
        }
        logger.debug("Set {} as user", userId);
        Subject subject = mock(Subject.class);
        if (!anonymous) {
          when(subject.getPrincipal()).thenReturn(userId);
        }
        ThreadContext.bind(new DefaultSecurityManager());
        threadState = new SubjectThreadState(subject);
        try {
          threadState.bind();
          base.evaluate();
        } finally {
          threadState.restore();
        }
      }
    };
  }

  /**
   * Returns default password version for tests.
   *
   * @return default password version for tests
   */
  public static PasswordVersion getPasswordVersion() {
    PasswordVersion passwordVersion = new PasswordVersion();
    passwordVersion.setAlgorithm(PASSWORD_ALGORITHM);
    passwordVersion.setIterations(PASSWORD_ITERATIONS);
    passwordVersion.setVersion(PASSWORD_VERSION);
    return passwordVersion;
  }

  /**
   * Returns value of remember me cookie.
   *
   * @param userId
   *          user id
   * @return value of remember me cookie
   */
  public static String rememberCookie(Long userId) {
    PrincipalCollection principals = new SimplePrincipalCollection(userId, "proview");
    ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
    try (ObjectOutputStream output = new ObjectOutputStream(byteArrayOutput)) {
      output.writeObject(principals);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    CipherService cipherService = new AesCipherService();
    byte[] encrypted = cipherService
        .encrypt(byteArrayOutput.toByteArray(), Base64.decode(COOKIE_ENCRYPTION)).getBytes();
    return Base64.encodeToString(encrypted);
  }
}