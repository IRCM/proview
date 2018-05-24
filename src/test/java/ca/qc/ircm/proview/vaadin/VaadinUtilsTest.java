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

package ca.qc.ircm.proview.vaadin;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.sample.QSubmissionSample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public class VaadinUtilsTest {
  private static final String name =
      QSubmissionSample.submissionSample.name.getMetadata().getName();
  private static final String status =
      QSubmissionSample.submissionSample.status.getMetadata().getName();
  private List<SubmissionSample> samples;
  private SubmissionSample sample1;
  private SubmissionSample sample2;
  private SubmissionSample sample3;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    sample1 = new SubmissionSample(null, "first by name");
    sample1.setStatus(SampleStatus.ANALYSED);
    sample2 = new SubmissionSample(null, "second by name");
    sample2.setStatus(SampleStatus.DIGESTED);
    sample3 = new SubmissionSample(null, "second by name");
    sample3.setStatus(SampleStatus.TO_APPROVE);
    samples = Arrays.asList(sample1, sample2, sample3);
  }

  @Test
  public void property() {
    assertEquals("", VaadinUtils.property((Object) null));
    assertEquals("true", VaadinUtils.property(true));
    assertEquals("sample", VaadinUtils.property("sample"));
    assertEquals("sample", VaadinUtils.property("sample", null));
    assertEquals("sample.name", VaadinUtils.property("sample", null, "name"));
    assertEquals("sample.true", VaadinUtils.property("sample", true));
    assertEquals("sample.name", VaadinUtils.property("sample.name"));
    assertEquals("sample.name", VaadinUtils.property("sample", "name"));
    assertEquals("sample.standards.name", VaadinUtils.property("sample.standards.name"));
    assertEquals("sample.standards.name", VaadinUtils.property("sample.standards", "name"));
    assertEquals("sample.standards.name", VaadinUtils.property("sample", "standards.name"));
    assertEquals("sample.standards.name", VaadinUtils.property("sample", "standards", "name"));
  }

  @Test
  public void styleName() {
    assertEquals("", VaadinUtils.property((Object) null));
    assertEquals("true", VaadinUtils.styleName(true));
    assertEquals("sample", VaadinUtils.styleName("sample"));
    assertEquals("sample-true", VaadinUtils.styleName("sample", true));
    assertEquals("sample", VaadinUtils.styleName("sample", null));
    assertEquals("sample-name", VaadinUtils.styleName("sample", null, "name"));
    assertEquals("sample-name", VaadinUtils.styleName("sample-name"));
    assertEquals("sample-name", VaadinUtils.styleName("sample.name"));
    assertEquals("sample-name", VaadinUtils.styleName("sample", "name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample-standards-name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample.standards.name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample.standards-name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample-standards.name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample-standards", "name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample.standards", "name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample", "standards-name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample", "standards.name"));
    assertEquals("sample-standards-name", VaadinUtils.styleName("sample", "standards", "name"));
  }

  @Test
  public void gridComparator() {
    Grid<SubmissionSample> grid = new Grid<>();
    grid.setItems(new ArrayList<>(samples));
    grid.addColumn(SubmissionSample::getName).setId(name);
    grid.addColumn(SubmissionSample::getStatus).setId(status);

    grid.sort(status);
    Comparator<SubmissionSample> comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample3, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample1, samples.get(2));

    grid.sort(status, SortDirection.DESCENDING);
    comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample1, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample3, samples.get(2));

    grid.setSortOrder(GridSortOrder.asc(grid.getColumn(name)).thenAsc(grid.getColumn(status)));
    comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample1, samples.get(0));
    assertEquals(sample3, samples.get(1));
    assertEquals(sample2, samples.get(2));

    grid.setSortOrder(GridSortOrder.asc(grid.getColumn(name)).thenDesc(grid.getColumn(status)));
    comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample1, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample3, samples.get(2));

    grid.setSortOrder(GridSortOrder.desc(grid.getColumn(name)).thenAsc(grid.getColumn(status)));
    comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample3, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample1, samples.get(2));

    grid.setSortOrder(GridSortOrder.desc(grid.getColumn(name)).thenDesc(grid.getColumn(status)));
    comparator = VaadinUtils.gridComparator(grid);
    samples.sort(comparator);
    assertEquals(sample2, samples.get(0));
    assertEquals(sample3, samples.get(1));
    assertEquals(sample1, samples.get(2));
  }

  @Test
  public void gridItems() {
    Grid<SubmissionSample> grid = new Grid<>();
    grid.setItems(new ArrayList<>(samples));
    grid.addColumn(SubmissionSample::getName).setId(name);
    grid.addColumn(SubmissionSample::getStatus).setId(status);

    grid.sort(status);
    List<SubmissionSample> samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample3, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample1, samples.get(2));

    grid.sort(status, SortDirection.DESCENDING);
    samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample1, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample3, samples.get(2));

    grid.setSortOrder(GridSortOrder.asc(grid.getColumn(name)).thenAsc(grid.getColumn(status)));
    samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample1, samples.get(0));
    assertEquals(sample3, samples.get(1));
    assertEquals(sample2, samples.get(2));

    grid.setSortOrder(GridSortOrder.asc(grid.getColumn(name)).thenDesc(grid.getColumn(status)));
    samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample1, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample3, samples.get(2));

    grid.setSortOrder(GridSortOrder.desc(grid.getColumn(name)).thenAsc(grid.getColumn(status)));
    samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample3, samples.get(0));
    assertEquals(sample2, samples.get(1));
    assertEquals(sample1, samples.get(2));

    grid.setSortOrder(GridSortOrder.desc(grid.getColumn(name)).thenDesc(grid.getColumn(status)));
    samples = VaadinUtils.gridItems(grid).collect(Collectors.toList());
    assertEquals(sample2, samples.get(0));
    assertEquals(sample3, samples.get(1));
    assertEquals(sample1, samples.get(2));
  }
}
