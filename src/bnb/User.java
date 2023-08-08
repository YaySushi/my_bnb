package bnb;

public class User {
  private int uid;
  private String sin;
  private String firstname;
  private String lastname;
  private String email;
  private String dob;
  private String occupation;
  private int aid;

  public User(int uid, String sin, String firstname, String lastname, String email, String dob, String occupation, int aid) {
    this.uid = uid;
    this.sin = sin;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.dob = dob;
    this.occupation = occupation;
    this.aid = aid;
  }

  // to be used when converting Renter to User, or Host to User
  public User(User user) {
    this.uid = user.uid;
    this.sin = user.sin;
    this.firstname = user.firstname;
    this.lastname = user.lastname;
    this.email = user.email;
    this.dob = user.dob;
    this.occupation = user.occupation;
    this.aid = user.aid;
  }

  public int getUid() {
    return uid;
  }

  public String toString() {
    return firstname + " " + lastname;
  }
}
