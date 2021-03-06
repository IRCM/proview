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

package ca.qc.ircm.proview.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Strings}.
 */
public class StringsTest {
  @Test
  public void property() {
    assertEquals("", Strings.property((Object) null));
    assertEquals("true", Strings.property(true));
    assertEquals("sample", Strings.property("sample"));
    assertEquals("sample", Strings.property("sample", null));
    assertEquals("sample.name", Strings.property("sample", null, "name"));
    assertEquals("sample.true", Strings.property("sample", true));
    assertEquals("sample.name", Strings.property("sample.name"));
    assertEquals("sample.name", Strings.property("sample", "name"));
    assertEquals("sample.standards.name", Strings.property("sample.standards.name"));
    assertEquals("sample.standards.name", Strings.property("sample.standards", "name"));
    assertEquals("sample.standards.name", Strings.property("sample", "standards.name"));
    assertEquals("sample.standards.name", Strings.property("sample", "standards", "name"));
  }

  @Test
  public void styleName() {
    assertEquals("", Strings.property((Object) null));
    assertEquals("true", Strings.styleName(true));
    assertEquals("sample", Strings.styleName("sample"));
    assertEquals("sample-true", Strings.styleName("sample", true));
    assertEquals("sample", Strings.styleName("sample", null));
    assertEquals("sample-name", Strings.styleName("sample", null, "name"));
    assertEquals("sample-name", Strings.styleName("sample-name"));
    assertEquals("sample-name", Strings.styleName("sample.name"));
    assertEquals("sample-name", Strings.styleName("sample", "name"));
    assertEquals("sample-standards-name", Strings.styleName("sample-standards-name"));
    assertEquals("sample-standards-name", Strings.styleName("sample.standards.name"));
    assertEquals("sample-standards-name", Strings.styleName("sample.standards-name"));
    assertEquals("sample-standards-name", Strings.styleName("sample-standards.name"));
    assertEquals("sample-standards-name", Strings.styleName("sample-standards", "name"));
    assertEquals("sample-standards-name", Strings.styleName("sample.standards", "name"));
    assertEquals("sample-standards-name", Strings.styleName("sample", "standards-name"));
    assertEquals("sample-standards-name", Strings.styleName("sample", "standards.name"));
    assertEquals("sample-standards-name", Strings.styleName("sample", "standards", "name"));
  }

  @Test
  public void normalize() {
    assertEquals("bateau", Strings.normalize("b??teau"));
    assertEquals("BATEAU", Strings.normalize("B??TEAU"));
    assertEquals("bateau", Strings.normalize("b??teau"));
    assertEquals("BATEAU", Strings.normalize("B??TEAU"));
    assertEquals("bateau", Strings.normalize("b??teau"));
    assertEquals("BATEAU", Strings.normalize("B??TEAU"));
    assertEquals("pepin", Strings.normalize("p??pin"));
    assertEquals("pepin", Strings.normalize("p??pin"));
    assertEquals("pepin", Strings.normalize("p??pin"));
    assertEquals("pepin", Strings.normalize("p??pin"));
    assertEquals("pepin", Strings.normalize("pep??n"));
    assertEquals("pepin", Strings.normalize("pep??n"));
    assertEquals("pepon", Strings.normalize("pep??n"));
    assertEquals("pepon", Strings.normalize("pep??n"));
    assertEquals("pepun", Strings.normalize("pep??n"));
    assertEquals("pepun", Strings.normalize("pep??n"));
    assertEquals("pepun", Strings.normalize("pep??n"));
    assertEquals("pepin", Strings.normalize("p??p??n"));
    // Test Polish, out of curiosity.
    assertEquals("a", Strings.normalize("??"));
    assertEquals("c", Strings.normalize("??"));
    assertEquals("e", Strings.normalize("??"));
    // Doesn't work because ?? in Unicode is not l with a slash, but its own character.
    //assertEquals("l", Strings.normalize("??"));
    assertEquals("n", Strings.normalize("??"));
    assertEquals("o", Strings.normalize("??"));
    assertEquals("s", Strings.normalize("??"));
    assertEquals("z", Strings.normalize("??"));
    assertEquals("z", Strings.normalize("??"));
  }
}
