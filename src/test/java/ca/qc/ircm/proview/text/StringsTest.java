package ca.qc.ircm.proview.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringsTest {
  @Test
  public void normalize() {
    assertEquals("bateau", Strings.normalize("bàteau"));
    assertEquals("BATEAU", Strings.normalize("BÀTEAU"));
    assertEquals("bateau", Strings.normalize("bâteau"));
    assertEquals("BATEAU", Strings.normalize("BÂTEAU"));
    assertEquals("bateau", Strings.normalize("bäteau"));
    assertEquals("BATEAU", Strings.normalize("BÄTEAU"));
    assertEquals("pepin", Strings.normalize("pépin"));
    assertEquals("pepin", Strings.normalize("pèpin"));
    assertEquals("pepin", Strings.normalize("pêpin"));
    assertEquals("pepin", Strings.normalize("pëpin"));
    assertEquals("pepin", Strings.normalize("pepîn"));
    assertEquals("pepin", Strings.normalize("pepïn"));
    assertEquals("pepon", Strings.normalize("pepôn"));
    assertEquals("pepon", Strings.normalize("pepön"));
    assertEquals("pepun", Strings.normalize("pepùn"));
    assertEquals("pepun", Strings.normalize("pepûn"));
    assertEquals("pepun", Strings.normalize("pepün"));
    assertEquals("pepin", Strings.normalize("pépîn"));
    // Test Polish, out of curiosity.
    assertEquals("a", Strings.normalize("ą"));
    assertEquals("c", Strings.normalize("ć"));
    assertEquals("e", Strings.normalize("ę"));
    //assertEquals("l", Strings.normalize("ł")); Doesn't work because ł in Unicode is not l with a slash, but its own character.
    assertEquals("n", Strings.normalize("ń"));
    assertEquals("o", Strings.normalize("ó"));
    assertEquals("s", Strings.normalize("ś"));
    assertEquals("z", Strings.normalize("ź"));
    assertEquals("z", Strings.normalize("ż"));
  }
}
