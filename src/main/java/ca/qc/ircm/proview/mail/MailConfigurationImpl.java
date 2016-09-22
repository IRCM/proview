package ca.qc.ircm.proview.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = MailConfigurationImpl.PREFIX)
public class MailConfigurationImpl implements MailConfiguration {
  public static final String PREFIX = "email";
  @Value("${spring.application.name}")
  private String applicationName;
  private boolean enabled;
  private String host;
  private String from;
  private String to;

  @Override
  public String getSubject() {
    return applicationName;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @Override
  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }
}
