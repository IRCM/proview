package ca.qc.ircm.proview.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.Collator;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Strings}.
 */
public class StringsTest {

  @Test
  public void property() {
    assertEquals("true", Strings.property(true));
    assertEquals("sample", Strings.property("sample"));
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
    assertEquals("true", Strings.styleName(true));
    assertEquals("sample", Strings.styleName("sample"));
    assertEquals("sample-true", Strings.styleName("sample", true));
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
    assertEquals("fijn", Strings.normalize("fĳn"));
    assertEquals("aequo", Strings.normalize("æquo"));
    assertEquals("oeuf", Strings.normalize("œuf"));
    // Test Polish, out of curiosity.
    assertEquals("a", Strings.normalize("ą"));
    assertEquals("c", Strings.normalize("ć"));
    assertEquals("e", Strings.normalize("ę"));
    // Doesn't work because ł in Unicode is not l with a slash, but its own character.
    //assertEquals("l", Strings.normalize("ł"));
    assertEquals("n", Strings.normalize("ń"));
    assertEquals("o", Strings.normalize("ó"));
    assertEquals("s", Strings.normalize("ś"));
    assertEquals("z", Strings.normalize("ź"));
    assertEquals("z", Strings.normalize("ż"));
  }

  @Test
  public void normalizedCollator() {
    Collator collator = Strings.normalizedCollator();
    assertEquals(0, collator.compare("bateau", "bàteau"));
    assertEquals(0, collator.compare("bateau", "bàteau"));
    assertEquals(0, collator.compare("bateau", "BÀTEAU"));
    assertEquals(0, collator.compare("BATEAU", "BÀTEAU"));
    assertEquals(0, collator.compare("bateau", "bâteau"));
    assertEquals(0, collator.compare("BATEAU", "BÂTEAU"));
    assertEquals(0, collator.compare("bateau", "bäteau"));
    assertEquals(0, collator.compare("BATEAU", "BÄTEAU"));
    assertEquals(0, collator.compare("pepin", "pépin"));
    assertEquals(0, collator.compare("pepin", "pèpin"));
    assertEquals(0, collator.compare("pepin", "pêpin"));
    assertEquals(0, collator.compare("pepin", "pëpin"));
    assertEquals(0, collator.compare("pepin", "pepîn"));
    assertEquals(0, collator.compare("pepin", "pepïn"));
    assertEquals(0, collator.compare("pepin", "PÉPIN"));
    assertEquals(0, collator.compare("pepon", "pepôn"));
    assertEquals(0, collator.compare("pepon", "pepön"));
    assertEquals(0, collator.compare("pepun", "pepùn"));
    assertEquals(0, collator.compare("pepun", "pepûn"));
    assertEquals(0, collator.compare("pepun", "pepün"));
    assertEquals(0, collator.compare("pepin", "pépîn"));
    assertTrue(collator.compare("pepin", "peqin") < 0);
    assertTrue(collator.compare("peqin", "pepin") > 0);
    assertTrue(collator.compare("pepin", "pepin1") < 0);
    assertTrue(collator.compare("pepin1", "pepin") > 0);
    assertTrue(collator.compare("pepin", "péqîn") < 0);
    assertTrue(collator.compare("peqin", "pépîn") > 0);
    assertTrue(collator.compare("", "pepin") < 0);
    assertTrue(collator.compare("pepin", "") > 0);
    // Does not work for some reason, but we care only about French special chars.
    //assertEquals(0, collator.compare("fijn", "fĳn"));
    assertEquals(0, collator.compare("aequo", "æquo"));
    assertEquals(0, collator.compare("oeuf", "œuf"));
    // Test Polish, out of curiosity.
    assertEquals(0, collator.compare("a", "ą"));
    assertEquals(0, collator.compare("c", "ć"));
    assertEquals(0, collator.compare("e", "ę"));
    // Doesn't work because ł in Unicode is not l with a slash, but its own character.
    //assertEquals(0, collator.compare("l", "ł"));
    assertEquals(0, collator.compare("n", "ń"));
    assertEquals(0, collator.compare("o", "ó"));
    assertEquals(0, collator.compare("s", "ś"));
    assertEquals(0, collator.compare("z", "ź"));
    assertEquals(0, collator.compare("z", "ż"));
  }

  @Test
  public void normalizedCollator_KeyExtractor() {
    Comparator<TestName> comparator = Comparator.comparing(TestName::getName,
        Strings.normalizedCollator());
    assertEquals(0, comparator.compare(name("bateau"), name("bàteau")));
    assertEquals(0, comparator.compare(name("bateau"), name("bàteau")));
    assertEquals(0, comparator.compare(name("bateau"), name("BÀTEAU")));
    assertEquals(0, comparator.compare(name("BATEAU"), name("BÀTEAU")));
    assertEquals(0, comparator.compare(name("bateau"), name("bâteau")));
    assertEquals(0, comparator.compare(name("BATEAU"), name("BÂTEAU")));
    assertEquals(0, comparator.compare(name("bateau"), name("bäteau")));
    assertEquals(0, comparator.compare(name("BATEAU"), name("BÄTEAU")));
    assertEquals(0, comparator.compare(name("pepin"), name("pépin")));
    assertEquals(0, comparator.compare(name("pepin"), name("pèpin")));
    assertEquals(0, comparator.compare(name("pepin"), name("pêpin")));
    assertEquals(0, comparator.compare(name("pepin"), name("pëpin")));
    assertEquals(0, comparator.compare(name("pepin"), name("pepîn")));
    assertEquals(0, comparator.compare(name("pepin"), name("pepïn")));
    assertEquals(0, comparator.compare(name("pepin"), name("PÉPIN")));
    assertEquals(0, comparator.compare(name("pepon"), name("pepôn")));
    assertEquals(0, comparator.compare(name("pepon"), name("pepön")));
    assertEquals(0, comparator.compare(name("pepun"), name("pepùn")));
    assertEquals(0, comparator.compare(name("pepun"), name("pepûn")));
    assertEquals(0, comparator.compare(name("pepun"), name("pepün")));
    assertEquals(0, comparator.compare(name("pepin"), name("pépîn")));
    assertTrue(comparator.compare(name("pepin"), name("peqin")) < 0);
    assertTrue(comparator.compare(name("peqin"), name("pepin")) > 0);
    assertTrue(comparator.compare(name("pepin"), name("pepin1")) < 0);
    assertTrue(comparator.compare(name("pepin1"), name("pepin")) > 0);
    assertTrue(comparator.compare(name("pepin"), name("péqîn")) < 0);
    assertTrue(comparator.compare(name("peqin"), name("pépîn")) > 0);
    assertTrue(comparator.compare(name(""), name("pepin")) < 0);
    assertTrue(comparator.compare(name("pepin"), name("")) > 0);
    // Does not work for some reason, but we care only about French special chars.
    //assertEquals(0, comparator.compare(name("fijn"), name("fĳn")));
    assertEquals(0, comparator.compare(name("aequo"), name("æquo")));
    assertEquals(0, comparator.compare(name("oeuf"), name("œuf")));
    // Test Polish, out of curiosity.
    assertEquals(0, comparator.compare(name("a"), name("ą")));
    assertEquals(0, comparator.compare(name("c"), name("ć")));
    assertEquals(0, comparator.compare(name("e"), name("ę")));
    // Doesn't work because ł in Unicode is not l with a slash, but its own character.
    //assertEquals(0, collator.compare("l", "ł"));
    assertEquals(0, comparator.compare(name("n"), name("ń")));
    assertEquals(0, comparator.compare(name("o"), name("ó")));
    assertEquals(0, comparator.compare(name("s"), name("ś")));
    assertEquals(0, comparator.compare(name("z"), name("ź")));
    assertEquals(0, comparator.compare(name("z"), name("ż")));
  }

  private TestName name(String name) {
    TestName testName = new TestName();
    testName.name = name;
    return testName;
  }

  private static class TestName {

    private String name;

    public String getName() {
      return name;
    }
  }
}
