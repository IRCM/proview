package ca.qc.ircm.proview.utils.xml;

import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;

/**
 * An element parsed by a {@link SAXParser}.
 */
public interface SaxElement {
  /**
   * Returns element's name.
   *
   * @return element's name
   */
  public String name();

  /**
   * Returns true if element's name is equal to expectedName, false otherwise.
   *
   * @param expectedName
   *          expected element's name
   * @return true if element's name is equal to expectedName, false otherwise
   */
  public boolean name(String expectedName);

  /**
   * Returns all attributes of element.
   *
   * @return all attributes of element
   */
  public Attributes attributes();

  /**
   * Returns value of specified attribute or null if attribute is not present in element.
   *
   * @param name
   *          attribute's name
   * @return value of specified attribute or null if attribute is not present in element
   */
  public String attribute(String name);

  /**
   * Returns true if element has an attribute with specified name, false otherwise.
   *
   * @param name
   *          attribute's name
   * @return true if element has an attribute with specified name, false otherwise
   */
  public boolean hasAttribute(String name);

  /**
   * Returns parent element of this element.
   *
   * @return parent element of this element
   */
  public SaxElement parent();

  /**
   * Returns true if parent element's name is equal to expectedParent, false otherwise.
   *
   * @param expectedParent
   *          expected parent element's name
   * @return true if parent element's name is equal to expectedParent, false otherwise
   */
  public boolean parent(String expectedParent);

  /**
   * Returns the closest ancestor of element that have specified name, or null if element doesn't
   * have an ancestor with that name.
   *
   * @param name
   *          ancestor's name
   * @return the closest ancestor of element that have specified name, or null if element doesn't
   *         have an ancestor with that name
   */
  public SaxElement ancestor(String name);

  /**
   * Returns true if element has an ancestor element with specified name, false otherwise.
   *
   * @param name
   *          ancestor's name
   * @return true if element has an ancestor element with specified name, false otherwise
   */
  public boolean hasAncestor(String name);

  /**
   * Returns text inside this element. Does not include text of children.
   *
   * @return text inside this element
   */
  public String getText();
}
