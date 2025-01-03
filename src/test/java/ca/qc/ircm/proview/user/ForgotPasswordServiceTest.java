package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.util.StringUtils;

/**
 * Tests for {@link ForgotPasswordService}.
 */
@ServiceTestAnnotations
public class ForgotPasswordServiceTest {
  private static final String MESSAGES_PREFIX = messagePrefix(ForgotPasswordService.class);
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceTest.class);
  @Autowired
  private ForgotPasswordService service;
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private MessageSource messageSource;
  @MockBean
  private ApplicationConfiguration applicationConfiguration;
  @MockBean
  private EmailService emailService;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @Mock
  private ForgotPasswordWebContext forgotPasswordWebContext;
  @Mock
  private MimeMessageHelper email;
  @Captor
  private ArgumentCaptor<ForgotPassword> forgotPasswordCaptor;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private String hashedPassword;
  private User user;
  private String confirmNumber;
  private String forgotPasswordUrl = "/validate/user";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() throws Throwable {
    user = userRepository.findById(10L).orElse(null);
    when(applicationConfiguration.getUrl(any(String.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return forgotPasswordUrl;
      }
    });
    hashedPassword = "da78f3a74658706/4ae8470fc73a83f369fed012";
    when(passwordEncoder.encode(any(String.class))).thenReturn(hashedPassword);
    when(emailService.htmlEmail()).thenReturn(email);
    when(forgotPasswordWebContext.getChangeForgottenPasswordUrl(any(), any()))
        .thenReturn(forgotPasswordUrl);
    confirmNumber = "70987756";
  }

  @Test
  public void get() throws Exception {
    ForgotPassword forgotPassword = service.get(9L, "174407008").get();

    assertEquals((Long) 9L, forgotPassword.getId());
    assertEquals("174407008", forgotPassword.getConfirmNumber());
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(forgotPassword.getRequestMoment()));
    assertTrue(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)
        .isBefore(forgotPassword.getRequestMoment()));
  }

  @Test
  public void get_Expired() throws Exception {
    assertFalse(service.get(7L, "803369922").isPresent());
  }

  @Test
  public void get_Invalid() throws Exception {
    assertFalse(service.get(20L, "435FA").isPresent());
  }

  @Test
  public void get_NullConfirmNumber() throws Exception {
    assertFalse(service.get(7L, null).isPresent());
  }

  @Test
  public void get_Used() throws Exception {
    assertFalse(service.get(10L, "460559412").isPresent());
  }

  @Test
  public void insert_Robot() throws Exception {
    user = userRepository.findById(1L).orElse(null);

    try {
      service.insert(user.getEmail(), forgotPasswordWebContext);
      fail("Expected AccessDeniedException");
    } catch (AccessDeniedException e) {
      // Ignore.
    }
  }

  @Test
  public void insert() throws Exception {
    service.insert(user.getEmail(), forgotPasswordWebContext);

    repository.flush();
    verify(forgotPasswordWebContext).getChangeForgottenPasswordUrl(forgotPasswordCaptor.capture(),
        any());
    ForgotPassword forgotPassword = forgotPasswordCaptor.getValue();
    assertNotNull(forgotPassword.getId());
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    forgotPassword = repository.findById(forgotPassword.getId()).orElse(null);
    assertNotNull(forgotPassword.getConfirmNumber());
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(forgotPassword.getRequestMoment()));
    assertTrue(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)
        .isBefore(forgotPassword.getRequestMoment()));
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

    service.insert(user.getEmail(), forgotPasswordWebContext);

    repository.flush();
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    verify(email).addTo(user.getEmail());
    verify(email).setSubject(messageSource.getMessage(MESSAGES_PREFIX + "subject", null, locale));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(
        textContent.contains(messageSource.getMessage("user.forgotpassword.header", null, locale)));
    assertTrue(htmlContent.contains(StringUtils
        .escapeXml(messageSource.getMessage("user.forgotpassword.header", null, locale))));
    assertTrue(textContent
        .contains(messageSource.getMessage("user.forgotpassword.message", null, locale)));
    assertTrue(htmlContent.contains(StringUtils
        .escapeXml(messageSource.getMessage("user.forgotpassword.message", null, locale))));
    assertTrue(
        textContent.contains(messageSource.getMessage("user.forgotpassword.footer", null, locale)));
    assertTrue(htmlContent.contains(StringUtils
        .escapeXml(messageSource.getMessage("user.forgotpassword.footer", null, locale))));
    String url = applicationConfiguration.getUrl(forgotPasswordUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void updatePassword() throws Exception {
    ForgotPassword forgotPassword = repository.findById(9L).orElse(null);

    service.updatePassword(forgotPassword, "abc");

    repository.flush();
    assertFalse(service.get(forgotPassword.getId(), forgotPassword.getConfirmNumber()).isPresent());
    verify(passwordEncoder).encode("abc");
    User user = forgotPassword.getUser();
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
  }

  @Test
  public void updatePassword_Expired() throws Exception {
    ForgotPassword forgotPassword = repository.findById(7L).orElse(null);

    try {
      service.updatePassword(forgotPassword, "abc");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }
}
