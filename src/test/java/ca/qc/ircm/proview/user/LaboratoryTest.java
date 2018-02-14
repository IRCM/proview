package ca.qc.ircm.proview.user;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LaboratoryTest {
  private User user(Long id, String name) {
    User user = new User(id);
    user.setName(name);
    return user;
  }

  @Test
  public void getDirector() {
    Laboratory laboratory = new Laboratory();
    User director = new User(3L, "Unit Test");
    laboratory.setManagers(
        Arrays.asList(director, user(4L, "User4"), user(10L, "User4"), user(25L, "User4")));

    assertEquals(director.getName(), laboratory.getDirector());
  }

  @Test
  public void getDirector_DirectorNoFirst() {
    Laboratory laboratory = new Laboratory();
    User director = new User(3L, "Unit Test");
    laboratory.setManagers(
        Arrays.asList(user(4L, "User4"), user(10L, "User4"), director, user(25L, "User4")));

    assertEquals(director.getName(), laboratory.getDirector());
  }

  @Test
  public void getDirector_DirectorNoName() {
    Laboratory laboratory = new Laboratory();
    User director = new User(3L, null);
    laboratory.setManagers(
        Arrays.asList(user(4L, "User4"), user(10L, "User4"), director, user(25L, "User4")));

    assertEquals(null, laboratory.getDirector());
  }

  @Test
  public void getDirector_NoManager() {
    Laboratory laboratory = new Laboratory();
    laboratory.setManagers(new ArrayList<>());

    assertEquals(null, laboratory.getDirector());
  }

  @Test
  public void getDirector_ManagersNull() {
    Laboratory laboratory = new Laboratory();
    laboratory.setManagers(null);

    assertEquals(null, laboratory.getDirector());
  }
}
