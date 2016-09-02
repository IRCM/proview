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
        new PlateImageRenderer(plate, new LocalizedPlateDefault(fractionationService));
    RenderedImage image = instance.render();

    File baseDir = new File(System.getProperty("user.home"));
    ImageIO.write(image, "png", new File(baseDir, "plate.png"));
  }
}
