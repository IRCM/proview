package ca.qc.ircm.proview.files.web;

import static org.junit.Assert.fail;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class CategoryFormTest {
  private CategoryForm view;
  @Mock
  private CategoryFormPresenter presenter;

  @Before
  public void beforeTest() {
    view = new CategoryForm(presenter);
  }

  @Test
  public void setValue_Attached() {
    fail("Program test");
  }

  @Test
  public void setValue_NotAttached() {
    fail("Program test");
  }
}
