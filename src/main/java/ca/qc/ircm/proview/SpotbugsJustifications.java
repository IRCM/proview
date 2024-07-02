package ca.qc.ircm.proview;

/**
 * Justifications for ignoring SpotBugs warnings.
 */
public class SpotbugsJustifications {
  public static final String ENTITY_EI_EXPOSE_REP =
      "Entities should expose internal representation like objects and lists to allow modification";
  public static final String SPRING_BOOT_EI_EXPOSE_REP =
      "Exposed internal representation for objects created by Spring Boot is acceptable";
  public static final String INNER_CLASS_EI_EXPOSE_REP =
      "Exposed internal representation for inner classes is acceptable";
  public static final String CHILD_COMPONENT_EI_EXPOSE_REP =
      "Exposed internal representation for some sub components fields is acceptable";
}
