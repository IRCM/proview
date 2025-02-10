package ca.qc.ircm.proview;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application's configuration.
 */
@ConfigurationProperties(prefix = ApplicationConfiguration.PREFIX)
public class ApplicationConfiguration {

  public static final String APPLICATION_NAME = "proview";
  public static final String PREFIX = "main";
  @Value("${logging.path:${user.dir}}/${logging.file.name:" + APPLICATION_NAME + "log}")
  private String logfile;
  private String serverUrl;

  public Path getLogFile() {
    return Paths.get(logfile);
  }

  /**
   * Returns urlEnd with prefix that allows to access application from anywhere.
   *
   * <p>For example, to obtain the full URL <code><a
   * href="http://myserver.com/proview/myurl?param1=abc">
   * http://myserver.com/proview/myurl?param1=abc</a></code> , the urlEnd parameter should be
   * <code>/proview/myurl?param1=abc</code>
   * </p>
   *
   * @param urlEnd end portion of URL
   * @return urlEnd with prefix that allows to access application from anywhere
   */
  public String getUrl(String urlEnd) {
    return serverUrl + urlEnd;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  @UsedBy(SPRING)
  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }
}
