package bnb;

import java.sql.SQLException;
import java.util.Locale;

public class Host extends User {

  public Host(User user) {
    super(user);
  }

  public static boolean signup(DAO dao, String sin, String firstname, String lastname, String email, String password, String dob,
                               String occupation, String address, String city, String province, String country,
                               String postalcode)
          throws IllegalArgumentException, SQLException {
    int aid = dao.createAddress(
            address.trim(),
            city.trim(),
            province.trim(),
            country.trim(),
            postalcode.trim()
    );

    int uid = dao.createUser(
            sin.toLowerCase(Locale.ROOT).trim(),
            firstname.trim(),
            lastname.trim(),
            email.toLowerCase(Locale.ROOT).trim(),
            password.toLowerCase(Locale.ROOT).trim(),
            dob.trim(),
            occupation.toLowerCase().trim(),
            aid
    );

    dao.createHost(uid);
    System.out.println("Host sign up successful.");
    return true;
  }

  public static User login(DAO dao, String email, String password) throws SQLException {
    User user = dao.getUserByEmail(email, password);
    if (user == null) {
      System.out.println("Incorrect password or non-registered email.");
      return null;
    }
    Host host = dao.getHostFromUser(user);
    if (host == null) {
      System.out.println("Email is not registered as a host");
      return null;
    }
    return host;
  }

  public static boolean exists(DAO dao, String email, String password) throws SQLException {
    if (email.isEmpty()) throw new IllegalArgumentException();
    User user = dao.getUserByEmail(email.toLowerCase(Locale.ROOT).trim(), password.trim());
    if (user != null) {
      Host host = dao.getHostFromUser(user);
      if (host == null) {  // then it must be a renter.
        dao.createHost(user.getUid());
        System.out.println("Account added as a host!");
        return true;
      }
      System.out.println("Email is already registered as a host.");
      return true;
    }
    return false;
  }
}
