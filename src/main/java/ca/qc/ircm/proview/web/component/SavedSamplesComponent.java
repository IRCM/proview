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

package ca.qc.ircm.proview.web.component;

import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLES;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Gets/sets saved samples in session.
 */
public interface SavedSamplesComponent extends Component {
  default void saveSamples(Collection<? extends Sample> samples) {
    getUI().getSession().setAttribute(SAVED_SAMPLES, samples);
  }

  /**
   * Returns saved samples.
   *
   * @return saved samples, never null
   */
  @SuppressWarnings("unchecked")
  default List<Sample> savedSamples() {
    Collection<Sample> samples =
        (Collection<Sample>) getUI().getSession().getAttribute(SAVED_SAMPLES);
    return samples == null ? new ArrayList<>() : new ArrayList<>(samples);
  }

  /**
   * Returns true if saved samples are from multiple users, false otherwise.
   *
   * @return true if saved samples are from multiple users, false otherwise
   */
  default boolean savedSampleFromMultipleUsers() {
    List<Sample> samples = savedSamples();
    return samples.stream().filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> ((SubmissionSample) sample).getSubmission().getUser())
        .filter(user -> user != null).map(user -> user.getId()).distinct().count() > 1;
  }
}
