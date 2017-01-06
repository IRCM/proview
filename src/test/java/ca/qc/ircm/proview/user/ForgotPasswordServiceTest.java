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

package ca.qc.ircm.proview.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.SpringConfiguration;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.HashedPassword;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.shiro.authz.AuthorizationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ForgotPasswordServiceTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceTest.class);
  private ForgotPasswordService forgotPasswordServiceDefault;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Mock
  private ApplicationConfiguration applicationConfiguration;
  @Mock
  private EmailService emailService;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private MimeMessageHelper email;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private HashedPassword hashedPassword;
  private User user;
  private Integer confirmNumber;
  private String forgotPasswordUrl = "/validate/user";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    SpringConfiguration springConfiguration = new SpringConfiguration();
    TemplateEngine templateEngine = springConfiguration.templateEngine();
    forgotPasswordServiceDefault = new ForgotPasswordService(entityManager, jpaQueryFactory,
        authenticationService, templateEngine, emailService, applicationConfiguration);
    user = entityManager.find(User.class, 10L);
    when(applicationConfiguration.getUrl(any(String.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return forgotPasswordUrl;
      }
    });
    hashedPassword = new HashedPassword("da78f3a74658706", "4ae8470fc73a83f369fed012", 1);
    when(authenticationService.hashPassword(any(String.class))).thenReturn(hashedPassword);
    when(emailService.htmlEmail()).thenReturn(email);
    confirmNumber = 70987756;
  }

  private ForgotPasswordWebContext forgotPasswordWebContext() {
    return (forgotPassword, locale) -> forgotPasswordUrl;
  }

  @Test
  public void get() throws Exception {
    ForgotPassword forgotPassword = forgotPasswordServiceDefault.get(9L, 174407008);

    forgotPassword =
        forgotPasswordServiceDefault.get(forgotPassword.getId(), forgotPassword.getConfirmNumber());

    assertEquals((Long) 9L, forgotPassword.getId());
    assertEquals(174407008, forgotPassword.getConfirmNumber());
    assertTrue(
        Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(forgotPassword.getRequestMoment()));
    assertTrue(
        Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(forgotPassword.getRequestMoment()));
  }

  @Test
  public void get_Expired() throws Exception {
    ForgotPassword forgotPassword = forgotPasswordServiceDefault.get(7L, 803369922);

    assertNull(forgotPassword);
  }

  @Test
  public void get_NullId() throws Exception {
    ForgotPassword forgotPassword = forgotPasswordServiceDefault.get(null, confirmNumber);

    assertNull(forgotPassword);
  }

  @Test
  public void get_NullConfirmNumber() throws Exception {
    ForgotPassword forgotPassword = forgotPasswordServiceDefault.get(7L, null);

    assertNull(forgotPassword);
  }

  @Test
  public void get_Used() throws Exception {
    ForgotPassword forgotPassword = forgotPasswordServiceDefault.get(10L, 460559412);

    assertNull(forgotPassword);
  }

  @Test
  public void insert_Robot() throws Exception {
    when(authenticationService.isRobot(any(Long.class))).thenReturn(true);

    try {
      forgotPasswordServiceDefault.insert(user.getEmail(), forgotPasswordWebContext());
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  public void insert() throws Exception {
    ForgotPassword forgotPassword =
        forgotPasswordServiceDefault.insert(user.getEmail(), forgotPasswordWebContext());

    entityManager.flush();
    assertNotNull(forgotPassword.getId());
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    forgotPassword = entityManager.find(ForgotPassword.class, forgotPassword.getId());
    assertNotNull(forgotPassword.getConfirmNumber());
    assertTrue(
        Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(forgotPassword.getRequestMoment()));
    assertTrue(
        Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(forgotPassword.getRequestMoment()));
  }

  @Test
  public void insert_EmailEn() throws Exception {
    insert_Email(Locale.CANADA);
  }

  @Test
  public void insert_EmailFr() throws Exception {
    insert_Email(Locale.CANADA_FRENCH);
  }

  private void insert_Email(final Locale locale) throws Exception {
    user.setLocale(locale);
    entityManager.merge(user);

    forgotPasswordServiceDefault.insert(user.getEmail(), forgotPasswordWebContext());

    entityManager.flush();
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    verify(email).addTo(user.getEmail());
    MessageResource resources =
        new MessageResource(ForgotPasswordService.class.getName() + "_Email", locale);
    verify(email).setSubject(resources.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains(resources.message("header")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("header"))));
    assertTrue(textContent.contains(resources.message("message")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
    assertTrue(textContent.contains(resources.message("footer")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
    String url = applicationConfiguration.getUrl(forgotPasswordUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void updatePassword() throws Exception {
    ForgotPassword forgotPassword = entityManager.find(ForgotPassword.class, 9L);

    forgotPasswordServiceDefault.updatePassword(forgotPassword, "abc");

    entityManager.flush();
    assertNull(forgotPasswordServiceDefault.get(forgotPassword.getId(),
        forgotPassword.getConfirmNumber()));
    verify(authenticationService).hashPassword("abc");
    User user = forgotPassword.getUser();
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
  }

  @Test
  public void updatePassword_Expired() throws Exception {
    ForgotPassword forgotPassword = entityManager.find(ForgotPassword.class, 7L);

    try {
      forgotPasswordServiceDefault.updatePassword(forgotPassword, "abc");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }
}