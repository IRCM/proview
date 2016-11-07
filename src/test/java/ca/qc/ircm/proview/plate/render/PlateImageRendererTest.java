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

package ca.qc.ircm.proview.plate.render;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateImageRendererTest {
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  public FractionationService fractionationService;

  @Test
  @Ignore("Not a real test")
  public void render() throws Exception {
    Plate plate = entityManager.find(Plate.class, 26L);
    when(fractionationService.find(any(SampleContainer.class)))
        .thenAnswer(new Answer<FractionationDetail>() {
          @Override
          public FractionationDetail answer(InvocationOnMock invocation) throws Throwable {
            SampleContainer container = (SampleContainer) invocation.getArguments()[0];
            if (container != null && container.getId() == 129L) {
              FractionationDetail detail = new FractionationDetail();
              detail.setSample(container.getSample());
              detail.setPosition(2);
              return detail;
            }
            return null;
          }
        });

    PlateImageRenderer instance =
        new PlateImageRenderer(plate, new LocalizedPlate(fractionationService));
    RenderedImage image = instance.render();

    File baseDir = new File(System.getProperty("user.home"));
    ImageIO.write(image, "png", new File(baseDir, "plate.png"));
  }
}
