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
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import javax.inject.Inject;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.util.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ForgotPasswordServiceTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceTest.class);
  @Inject
  private ForgotPasswordService service;
  @Inject
  private ForgotPasswordRepository repository;
  @Inject
  private UserRepository userRepository;
  @MockBean
  private ApplicationConfiguration applicationConfiguration;
  @MockBean
  private EmailService emailService;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @Mock
  private MimeMessageHelper email;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private String hashedPassword;
  private User user;
  private Integer confirmNumber;
  private String forgotPasswordUrl = "/validate/user";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    user = userRepository.findOne(10L);
    when(applicationConfiguration.getUrl(any(String.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return forgotPasswordUrl;
      }
    });
    hashedPassword = "da78f3a74658706/4ae8470fc73a83f369fed012";
    when(passwordEncoder.encode(any(String.class))).thenReturn(hashedPassword);
    when(emailService.htmlEmail()).thenReturn(email);
    confirmNumber = 70987756;
  }

  private ForgotPasswordWebContext forgotPasswordWebContext() {
    return (forgotPassword, locale) -> forgotPasswordUrl;
  }

  @Test
  public void get() throws Exception {
    ForgotPassword forgotPassword = service.get(9L, 174407008);

    forgotPassword = service.get(forgotPassword.getId(), forgotPassword.getConfirmNumber());

    assertEquals((Long) 9L, forgotPassword.getId());
    assertEquals(174407008, forgotPassword.getConfirmNumber());
    assertTrue(
        Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(forgotPassword.getRequestMoment()));
    assertTrue(
        Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(forgotPassword.getRequestMoment()));
  }

  @Test
  public void get_Expired() throws Exception {
    ForgotPassword forgotPassword = service.get(7L, 803369922);

    assertNull(forgotPassword);
  }

  @Test
  public void get_NullId() throws Exception {
    ForgotPassword forgotPassword = service.get(null, confirmNumber);

    assertNull(forgotPassword);
  }

  @Test
  public void get_NullConfirmNumber() throws Exception {
    ForgotPassword forgotPassword = service.get(7L, null);

    assertNull(forgotPassword);
  }

  @Test
  public void get_Used() throws Exception {
    ForgotPassword forgotPassword = service.get(10L, 460559412);

    assertNull(forgotPassword);
  }

  @Test
  public void insert_Robot() throws Exception {
    user = userRepository.findOne(1L);

    try {
      service.insert(user.getEmail(), forgotPasswordWebContext());
      fail("Expected AccessDeniedException");
    } catch (AccessDeniedException e) {
      // Ignore.
    }
  }

  @Test
  public void insert() throws Exception {
    ForgotPassword forgotPassword = service.insert(user.getEmail(), forgotPasswordWebContext());

    repository.flush();
    assertNotNull(forgotPassword.getId());
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    forgotPassword = repository.findOne(forgotPassword.getId());
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
    userRepository.save(user);

    service.insert(user.getEmail(), forgotPasswordWebContext());

    repository.flush();
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
    ForgotPassword forgotPassword = repository.findOne(9L);

    service.updatePassword(forgotPassword, "abc");

    repository.flush();
    assertNull(service.get(forgotPassword.getId(), forgotPassword.getConfirmNumber()));
    verify(passwordEncoder).encode("abc");
    User user = forgotPassword.getUser();
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
  }

  @Test
  public void updatePassword_Expired() throws Exception {
    ForgotPassword forgotPassword = repository.findOne(7L);

    try {
      service.updatePassword(forgotPassword, "abc");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }
}
