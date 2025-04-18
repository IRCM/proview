package ca.qc.ircm.proview.test.config;

import org.springframework.context.ApplicationContext;

/**
 * Injects dependencies in this.
 */
public interface InjectDependencies {

  default void injectDependencies(ApplicationContext context) {
    context.getAutowireCapableBeanFactory().autowireBean(this);
  }
}
