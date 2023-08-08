package bnb;

import java.sql.SQLException;
import java.util.Locale;

public class Renter extends User {
  private String creditcard;

  public Renter(User user, String cc) {
    super(user);
    this.creditcard = cc;
  }

  public static boolean signup(DAO dao, String sin, String firstname, String lastname, String email, String password, String dob,
                               String occupation, String address, String city, String province, String country,
                               String postalcode, String creditcard)
          throws IllegalArgumentException, SQLException {
    int aid = dao.createAddress(
            address.toLowerCase(Locale.ROOT).trim(),
            city.toLowerCase(Locale.ROOT).trim(),
            province.toLowerCase(Locale.ROOT).trim(),
            country.toLowerCase(Locale.ROOT).trim(),
            postalcode.toLowerCase(Locale.ROOT).trim()
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

    dao.createRenter(uid, creditcard.toLowerCase(Locale.ROOT).trim());
    System.out.println("Renter sign up successful.");
    return true;
  }

  public static User login(DAO dao, String email, String password) throws SQLException {
    User user = dao.getUserByEmail(email, password);
    if (user == null) {
      System.out.println("Incorrect password or non-registered email.");
      return null;
    }
    Renter renter = dao.getRenterFromUser(user);
    if (renter == null) {
      System.out.println("Email is not registered as a renter.");
      return null;
    }
    return renter;
  }
  public static boolean exists(DAO dao, String email, String password, String creditcard) throws SQLException {
    if (creditcard.isEmpty() || email.isEmpty()) throw new IllegalArgumentException();

    User user = dao.getUserByEmail(email.toLowerCase(Locale.ROOT).trim(), password.trim());
    if (user != null) {
      Renter renter = dao.getRenterFromUser(user);
      if (renter == null) { // then must be a host.
        // user is an active host, signup as renter
        dao.createRenter(user.getUid(), creditcard.toLowerCase(Locale.ROOT).trim());
        System.out.println("Account added as a renter." );
        return true;
      }
      System.out.println("Email is already registered as a renter.");
      return true;
    }
    return false;
  }


}
