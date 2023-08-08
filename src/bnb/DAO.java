package bnb;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DAO {
  public final Connection conn;

  public DAO(String dbName, String user, String password) throws SQLException {
    String url = "jdbc:mysql://localhost/" + dbName;
    conn = DriverManager.getConnection(url, user, password);
  }

  public int createUser(String sin, String firstname, String lastname, String email, String password, String dob, String occupation, int aid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Users(sin, firstname, lastname, email, password, dob, occupation, aid) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    stmt.setString(1, sin);
    stmt.setString(2, firstname);
    stmt.setString(3, lastname);
    stmt.setString(4, email);
    stmt.setString(5, password);
    stmt.setString(6, dob);
    stmt.setString(7, occupation);
    stmt.setInt(8, aid);
    stmt.executeUpdate();
    return getUserByEmail(email, password).getUid();
  }

  public void deleteUser(int uid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE uid = ?");
    stmt.setInt(1, uid);
    stmt.executeUpdate();
  }

  public void createHost(int uid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Hosts VALUES (?)");
    stmt.setInt(1, uid);
    stmt.executeUpdate();
  }
  public void createRenter(int uid, String creditcard) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Renters VALUES (?, ?)");
    stmt.setInt(1, uid);
    stmt.setString(2, creditcard);
    stmt.executeUpdate();
  }
  public int createListing(int hid, String listingtype, double lat, double lng, int aid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Listings(hid, listingtype, lat, lng, aid) " +
                    "VALUES (?, ?, ?, ?, ?)");
    stmt.setInt(1, hid);
    stmt.setString(2, listingtype);
    stmt.setDouble(3, lat);
    stmt.setDouble(4, lng);
    stmt.setInt(5, aid);
    stmt.executeUpdate();
    return getListingID(aid);
  }
  public int createAddress(String address, String city, String province, String country, String postalcode) throws SQLException {
    int aid = getAddressID(address, city, province, country);
    if (aid != -1) return aid;
    PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Addresses(address, city, province, country, postalcode) VALUES (?, ?, ?, ?, ?)");
    stmt.setString(1, address);
    stmt.setString(2, city);
    stmt.setString(3, province);
    stmt.setString(4, country);
    stmt.setString(5, postalcode);
    stmt.executeUpdate();
    return getAddressID(address, city, province, country);
  }
  public void createBooking(int rid, int lid, String startdate, String enddate, double cost) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO " +
            "Bookings(rid, lid, startdate, enddate, cost) VALUES(?, ?, ?, ?, ?)");
    stmt.setInt(1, rid);
    stmt.setInt(2, lid);
    stmt.setString(3, startdate);
    stmt.setString(4, enddate);
    stmt.setDouble(5, cost);
    stmt.executeUpdate();
  }

  public ArrayList<Listing> getListingsByHid(int hid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Listings NATURAL JOIN Addresses WHERE hid=?");
    stmt.setInt(1, hid);

    ResultSet rs = stmt.executeQuery();
    ArrayList<Listing> result = new ArrayList<>();
    while(rs.next()) {
      int lid = rs.getInt("lid");
      String listingtype = rs.getString("listingtype");
      double lat = rs.getDouble("lat");
      double lng = rs.getDouble("lng");
      int aid = rs.getInt("aid");
      String address = rs.getString("address");
      String city = rs.getString("city");
      String province = rs.getString("province");
      String country = rs.getString("country");
      String postalcode = rs.getString("postalcode");

      Address newAddress = new Address(aid, address, city, province, country, postalcode);
      result.add(new Listing(lid, listingtype, lat, lng, newAddress));
    }
    return result;
  }

  public int getAddressID(String address, String city, String province, String country) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Addresses WHERE address=? AND city=? AND province=? AND country=?");
    stmt.setString(1, address);
    stmt.setString(2, city);
    stmt.setString(3, province);
    stmt.setString(4, country);
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) return rs.getInt("aid");
    return -1;
  }
  public int getListingID(int aid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Listings WHERE aid=?");
    stmt.setInt(1, aid);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) return rs.getInt("lid");
    return -1;
  }
  public User getUserByEmail(String email, String password) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE email=?");
    stmt.setString(1, email);
    ResultSet rs = stmt.executeQuery();
    if(rs.next()) {
      User user = new User(
              rs.getInt("uid"),
              rs.getString("sin"),
              rs.getString("firstname"),
              rs.getString("lastname"),
              email,
              rs.getString("dob"),
              rs.getString("occupation"),
              rs.getInt("aid"));
      return rs.getString("password").equals(password) ? user : null;
    }
    return null;
  }
  public Renter getRenterFromUser(User user) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Renters where rid=?");
    stmt.setInt(1, user.getUid());
    ResultSet rs = stmt.executeQuery();

    if(rs.next()) return new Renter(user, rs.getString("creditcard"));
    return null;
  }
  public Host getHostFromUser(User user) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Hosts where hid=?");
    stmt.setInt(1, user.getUid());
    ResultSet rs = stmt.executeQuery();

    if(rs.next()) return new Host(user);
    return null;
  }
  public ArrayList<String> getAmenitiesListByType(String type) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Amenities WHERE type=?");
    stmt.setString(1, type);
    ResultSet rs = stmt.executeQuery();
    ArrayList<String> amenities = new ArrayList<>();
    while (rs.next()) amenities.add(rs.getString("amenity"));

    return amenities;
  }

  public void addAmenity(int lid, String amenity) throws SQLException{
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO has VALUES (?, ?)");
    stmt.setInt(2, lid);
    stmt.setString(1, amenity);
    stmt.executeUpdate();
  }

  public double getCost(int lid, String startdate, String enddate) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT SUM(price) as cost FROM Availabilities " +
            "WHERE lid = ? AND date BETWEEN ? AND ?");
    stmt.setInt(1, lid);
    stmt.setString(2, startdate);
    stmt.setString(3, enddate);
    ResultSet rs = stmt.executeQuery();
    if (rs.next())  return rs.getDouble("cost");
    return -1;
  }
  public boolean checkAvailability(int lid, String startdate, String enddate) throws SQLException {
    // check if a listing is available during the ENTIRETY of [startdate, enddate]
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT lid FROM Availabilities " +
            "WHERE lid = ? AND status='Available' AND date BETWEEN ? AND ? " +
            "GROUP BY lid HAVING COUNT(*)=DATEDIFF(?, ?)+1");
    stmt.setInt(1, lid);
    stmt.setString(2, startdate);
    stmt.setString(3, enddate);
    stmt.setString(4, enddate);
    stmt.setString(5, startdate);
    return stmt.executeQuery().next();
  }

  public int numAvailabilityInRange(int lid, String start, String end) throws SQLException {
    LocalDate currdate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
    LocalDate enddate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);

    enddate = enddate.plusDays(1);
    int i = 0;

    while (!currdate.equals(enddate)) {
      if (checkAvailabilitiesInRange(lid, currdate.toString(), currdate.toString())) i++;
      currdate = currdate.plusDays(1);
    }
    return i;
  }

  public boolean checkAvailabilitiesInRange(int lid, String start, String end) throws SQLException {
    /* Return true if there is SOME availability in the given date range. */
    // do this by getting all the availabilities, then checking if size of that list is > 0.
    return getAvailabilitiesInRange(lid, start, end).size() > 0;
  }
  public int createAvailabilitiesInRange(int lid, String start, String end, double price) throws SQLException {
    /* Returns the number of availabilities created */
    LocalDate currdate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
    LocalDate enddate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);

    enddate = enddate.plusDays(1);
    int count = 0;

    while (!currdate.equals(enddate)) {
      if (!checkAvailabilitiesInRange(lid, currdate.toString(), currdate.toString())) {
        createAvailability(lid, currdate.toString(), price);
        count++;
      }
      currdate = currdate.plusDays(1);
    }
    return count;
  }
  public int updateAvailabilityInRange(int lid, String start, String end, double price) throws SQLException {
    LocalDate currdate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
    LocalDate enddate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);

    enddate = enddate.plusDays(1);

    while (!currdate.equals(enddate)) {
      updateAvailabilityPrice(lid, currdate.toString(), price);
      currdate = currdate.plusDays(1);
    }

    //the # of availabilities modified is just the # of availabilities available.
    return numAvailabilityInRange(lid, start, end);
  }
  public int cancelAvailabilitiesInRange(int lid, String start, String end) throws SQLException {
    LocalDate currdate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
    LocalDate enddate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);

    int ans = numAvailabilityInRange(lid, start, end);
    enddate = enddate.plusDays(1);

    while (!currdate.equals(enddate)) {
      cancelAvailability(lid, currdate.toString());
      currdate = currdate.plusDays(1);
    }

    //the # of availabilities modified is just the # of availabilities available.
    return ans;
  }
  public ArrayList<Availability> getAvailabilitiesInRange(int lid, String start, String end) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Availabilities WHERE lid=? AND status='Available' AND `date` BETWEEN ? AND ?");
    stmt.setInt(1, lid);
    stmt.setString(2, start);
    stmt.setString(3, end);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Availability> result = new ArrayList<>();
    while (rs.next()) {
      String date = rs.getString("date");
      double price = rs.getDouble("price");
      String status = rs.getString("status");

      result.add(new Availability(lid, status, price, date));
    }
    return result;
  }

  public void createAvailability(int lid, String date, double price) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Availabilities VALUES (?, 'Available', ?, ?)");
    stmt.setString(1, date);
    stmt.setDouble(2, price);
    stmt.setInt(3, lid);
    stmt.executeUpdate();
  }
  public void cancelAvailability(int lid, String date) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM Availabilities WHERE status='Available' AND lid=? AND date=?");
    stmt.setInt(1, lid);
    stmt.setString(2, date);
    stmt.executeUpdate();
  }
  public void updateAvailabilityPrice(int lid, String date, double price) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Availabilities SET price=? WHERE status='Available' AND " +
            "lid=? AND date=?");
    stmt.setDouble(1, price);
    stmt.setInt(2, lid);
    stmt.setString(3, date);
    stmt.executeUpdate();
  }
  public void updateAvailabilityStatus(int lid, String startdate, String enddate, String status) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Availabilities SET status = ? " +
            "WHERE lid = ? AND date BETWEEN ? AND ?");
    stmt.setString(1, status);
    stmt.setInt(2, lid);
    stmt.setString(3, startdate);
    stmt.setString(4, enddate);
    stmt.executeUpdate();
  }

  public ArrayList<Listing> getListingsFromView(String view, String str) throws SQLException {
    PreparedStatement stmt;

    if (str.equals("ASC") || str.equals("DESC")) {
      stmt = conn.prepareStatement("SELECT L.*, AVG(price) AS price FROM " + view + " L, Availabilities A " +
              "WHERE L.lid=A.lid GROUP BY L.lid ORDER BY price " + str);
    } else {
      stmt = conn.prepareStatement("(SELECT L.*, AVG(price) AS price FROM " + view + " L, Availabilities A " +
              "WHERE L.lid=A.lid GROUP BY L.lid) UNION (SELECT L.*, -1 AS price FROM " + view + " L, Availabilities A " +
              "WHERE L.lid NOT IN (SELECT lid FROM Availabilities))");
    }

    ResultSet rs = stmt.executeQuery();
    ArrayList<Listing> result = new ArrayList<>();
    while(rs.next()) {
      int lid = rs.getInt("lid");
      String listingtype = rs.getString("listingtype");
      double lat = rs.getDouble("lat");
      double lng = rs.getDouble("lng");

      int aid = rs.getInt("aid");
      String address = rs.getString("address");
      String city = rs.getString("city");
      String province = rs.getString("province");
      String country = rs.getString("country");
      String postalCode = rs.getString("postalcode");

      Address newAddress = new Address(aid, address, city, province, country, postalCode);
      result.add(new Listing(lid, listingtype, lat, lng, newAddress));
    }
    return result;
  }
  public Listing getListingFromID(int lid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Listings  NATURAL JOIN Addresses WHERE lid=?");
    stmt.setInt(1, lid);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) {
      String listingtype = rs.getString("listingtype");
      double lat = rs.getDouble("lat");
      double lng = rs.getDouble("lng");

      int aid = rs.getInt("aid");
      String address = rs.getString("address");
      String city = rs.getString("city");
      String country = rs.getString("country");
      String province = rs.getString("province");
      String postalcode = rs.getString("postalcode");

      Address newAddress = new Address(aid, address, city, province, country, postalcode);
      return new Listing(lid, listingtype, lat, lng, newAddress);
    }
    return null;
  }

  // Commenting code
  /* Creates a comment for a host/user. Assumes that all arguments are valid. */
  public void createComment(int hid, int rid, String comment, int rating, String reviewer) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO comments (comment, rating, rid, hid, reviewer) VALUES (?, ?, ?, ?, ?)");
    stmt.setString(1, comment);
    stmt.setInt(2, rating);
    stmt.setInt(3, rid);
    stmt.setInt(4, hid);
    stmt.setString(5, reviewer);
    stmt.executeUpdate();
  }
  public Comment resultToComment(ResultSet rs) throws SQLException {
    int commentCid = rs.getInt("cid");
    String comment = rs.getString("comment");
    int rating = rs.getInt("rating");
    int hid = rs.getInt("hid");
    int rid = rs.getInt("rid");
    String reviewer = rs.getString("reviewer");
    return new Comment(commentCid, comment, rating, hid, rid, reviewer);
  }
  public Comment getComment(int cid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM comments WHERE cid=?");
    stmt.setInt(1, cid);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) {
      return resultToComment(rs);
    }
    return null;
  }
  public ArrayList<Comment> getCommentsByUser(int uid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Comments WHERE hid=? OR rid=?");
    stmt.setInt(1, uid);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Comment> result = new ArrayList<>();
    while (rs.next()) {
      result.add(resultToComment(rs));
    }
    return result;
  }
  public User resultToUser(ResultSet rs) throws SQLException {
    int uid = rs.getInt("uid");
    String sin = rs.getString("sin");
    String firstname = rs.getString("firstname");
    String lastname = rs.getString("lastname");
    String email = rs.getString("email");
    String dob = rs.getString("dob");
    String occupation = rs.getString("occupation");
    int aid = rs.getInt("aid");
    return new User(uid, sin, firstname, lastname, email, dob, occupation, aid);
  }
  public void commentOnBooking(int bid, String comment, int rating) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("UPDATE bookings SET comment = ?, rating = ? WHERE bid = ?");
    stmt.setString(1, comment);
    stmt.setInt(2, rating);
    stmt.setInt(3, bid);
    stmt.executeUpdate();
  }

  public void cancelBooking(int bid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Bookings SET cancelled=true WHERE bid = ?");
    stmt.setInt(1, bid);
    stmt.executeUpdate();
  }

  public ArrayList<User> getRentersForHost(int hid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT U.* FROM Users U, Bookings B, Listings L " +
                    "WHERE U.uid = B.rid AND L.hid = ? AND B.lid = L.lid " +
                    "AND DATEDIFF(CURDATE(), B.enddate) BETWEEN 0 AND 30");
    stmt.setInt(1, hid);
    ResultSet rs = stmt.executeQuery();
    ArrayList<User> result = new ArrayList<>();
    while (rs.next()) {
      result.add(resultToUser(rs));
    }
    System.out.println(result);
    return result;
  }
  public ArrayList<User> getHostsForRenter(int rid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
            "SELECT U.* FROM Users U, Bookings B, Listings L " +
                    "WHERE U.uid = L.hid AND B.rid = ? AND B.lid = L.lid " +
                    "AND DATEDIFF(CURDATE(), B.enddate) BETWEEN 0 AND 30");
    stmt.setInt(1, rid);
    ResultSet rs = stmt.executeQuery();
    ArrayList<User> result = new ArrayList<>();
    while (rs.next()) {
      result.add(resultToUser(rs));
    }
    return result;
  }

  public void deleteView(String view) throws SQLException {
    conn.prepareStatement("DROP VIEW IF EXISTS "+view).execute();
  }
  public void createView(String view, String query) throws SQLException {
    conn.prepareStatement("CREATE VIEW " + view + " AS (" + query + ")").execute();
  }


  public List<Booking> getHostsBookings(String status, int hid) throws SQLException {
    PreparedStatement stmt;
    if (status.equals("future")) stmt = conn.prepareStatement("SELECT B.* FROM Bookings B, Listings L " +
            "WHERE B.lid=L.lid AND B.startdate > CURDATE() AND L.hid = ?");
    else if (status.equals("past")) stmt = conn.prepareStatement("SELECT B.* FROM Bookings B, Listings L " +
            "WHERE B.lid=L.lid AND enddate <= CURDATE() AND L.hid = ?");
    else stmt = conn.prepareStatement("SELECT B.* FROM Bookings B, Listings L " +
              "WHERE B.lid=L.lid AND cancelled=true AND L.hid = ?");

    stmt.setInt(1, hid);
    ResultSet rs = stmt.executeQuery();

    List<Booking> bookings = new ArrayList<Booking>();
    while (rs.next()) {
      int bid = rs.getInt("bid");
      int rid = rs.getInt("rid");
      int lid = rs.getInt("lid");
      String startdate = rs.getString("startdate");
      String enddate = rs.getString("enddate");
      Double cost = rs.getDouble("cost");
      String comment = rs.getString("comment");
      int rating = rs.getInt("rating");
      bookings.add(new Booking(bid, rid, lid, startdate, enddate, cost, comment, rating));
    }
    return bookings;
  }
  public List<Booking> getRentersBookings(String status, int rid) throws SQLException {
    PreparedStatement stmt;
    if (status.equals("future")) stmt = conn.prepareStatement("SELECT * FROM Bookings WHERE startdate > CURDATE() AND rid = ?");
    else if (status.equals("past")) stmt = conn.prepareStatement("SELECT * FROM Bookings WHERE enddate <= CURDATE() AND rid = ?");
    else stmt = conn.prepareStatement("SELECT * FROM Bookings WHERE cancelled = true AND rid = ?");
    stmt.setInt(1, rid);
    ResultSet rs = stmt.executeQuery();

    List<Booking> bookings = new ArrayList<Booking>();
    while (rs.next()) {
      int bid = rs.getInt("bid");
      int lid = rs.getInt("lid");
      String startdate = rs.getString("startdate");
      String enddate = rs.getString("enddate");
      Double cost = rs.getDouble("cost");
      String comment = rs.getString("comment");
      int rating = rs.getInt("rating");
      bookings.add(new Booking(bid, rid, lid, startdate, enddate, cost, comment, rating));
    }
    return bookings;
  }

  // Reports

  public int getNumBookings(String startdate, String enddate, String city, String postalCode) throws SQLException {
    if (startdate == null || enddate == null || city == null) {
      throw new IllegalArgumentException("Null arguments");
    }
    StringBuilder query = new StringBuilder("SELECT COUNT(*) AS numBookings FROM Listings NATURAL JOIN Bookings NATURAL JOIN Addresses " +
            "WHERE startdate >= ? AND enddate <= ? AND city = ?");
    if (postalCode != null) {
      query.append(" AND postalcode = ?");
    }
    PreparedStatement stmt = conn.prepareStatement(query.toString());
    stmt.setString(1, startdate);
    stmt.setString(2, enddate);
    stmt.setString(3, city);
    if (postalCode != null) {
      stmt.setString(4, postalCode);
    }
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt(1);
  }

  public int getNumListingsPerCountry(String country, String city, String postalCode) throws SQLException {
    if (country == null) {
      throw new IllegalArgumentException("Null argument");
    }
    StringBuilder query = new StringBuilder("SELECT COUNT(*) AS numListings FROM Listings NATURAL JOIN Addresses " +
            "WHERE country = ?");
    if (city != null) {
      query.append(" AND city = ?");
    }
    if (postalCode != null) {
      query.append(" AND postalcode = ?");
    }
    PreparedStatement stmt = conn.prepareStatement(query.toString());
    stmt.setString(1, country);
    if (city != null) {
      stmt.setString(2, city);
    }
    if (postalCode != null) {
      stmt.setString(3, postalCode);
    }
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt(1);
  }

  public User getUserFromUID(int uid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE uid = ?");
    stmt.setInt(1, uid);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) return resultToUser(rs);
    return null;
  }

  public ArrayList<Map<String, Object>> getRankHosts(String country, String city) throws SQLException {
    if (country == null) {
      throw new IllegalArgumentException("Null argument");
    }
    StringBuilder query = new StringBuilder("SELECT u.uid, COUNT(*) AS num" +
            " FROM Users u JOIN Hosts h on u.uid = h.hid JOIN Listings l ON h.hid = l.hid" +
            " JOIN Addresses a ON l.aid = a.aid AND a.country = ?");
    if (city != null) {
      query.append(" AND a.city = ?");
    }
    query.append(" GROUP BY country");
    if (city != null) {
      query.append(", city");
    }
    query.append(", u.uid ORDER BY num DESC");
    PreparedStatement stmt = conn.prepareStatement(query.toString());
    stmt.setString(1, country);
    if (city != null) {
      stmt.setString(2, city);
    }
    ResultSet rs = stmt.executeQuery();
    ArrayList<Map<String, Object>> hosts = new ArrayList<>();
    while (rs.next()) {
      int uid = rs.getInt("uid");
      User user = getUserFromUID(uid);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("host", getUserFromUID(uid));
      map.put("num", rs.getInt("num"));
      hosts.add(map);
    }
    return hosts;
  }

  public ArrayList<Map<String, Object>> getHostsWithMoreThanTenPercent(String country, String city) throws SQLException {
    if (country == null) {
      throw new IllegalArgumentException("Null argument");
    }
    StringBuilder query = new StringBuilder("CREATE OR REPLACE VIEW country_listings AS " +
            "SELECT Listings.hid, country FROM " +
            "Listings JOIN Addresses ON Listings.aid = Addresses.aid " +
            "WHERE country = ?");
    if (city != null) {
      query.append(" AND city = ?");
    }

    PreparedStatement stmt = conn.prepareStatement(query.toString());
    stmt.setString(1, country);
    if (city != null) {
      stmt.setString(2, city);
    }
    stmt.execute();

    query.setLength(0);
    query.append("SELECT u.uid, " +
            "(SELECT COUNT(*) FROM country_listings WHERE hid = h.hid) " +
            "/ (SELECT COUNT(*) FROM country_listings) * 100 AS percentage " +
            "FROM Users u JOIN Hosts h on u.uid = h.hid JOIN country_listings " +
            "GROUP BY country");
    if (city != null) {
      query.append(", city");
    }
    query.append(", u.uid HAVING percentage > 10 ORDER BY percentage DESC");

    stmt = conn.prepareStatement(query.toString());
    ResultSet rs = stmt.executeQuery();
    ArrayList<Map<String, Object>> hostsTenPercent = new ArrayList<>();

    while (rs.next()) {
      int uid = rs.getInt("uid");
      User user = getUserFromUID(uid);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("host", getUserFromUID(uid));
      map.put("percentage", rs.getDouble("percentage"));
      hostsTenPercent.add(map);
    }
    return hostsTenPercent;
  }

  public ArrayList<Map<String, Object>> getRankedRenters(String startdate, String enddate, String city) throws SQLException {
    if (startdate == null || enddate == null) {
      throw new IllegalArgumentException("Null arguments");
    }
    StringBuilder query = new StringBuilder("CREATE OR REPLACE VIEW bookings_in_range AS ");
    query.append("SELECT r.rid FROM Renters r " +
            "JOIN bookings b ON r.rid = b.rid ");
    if (city != null) {
      query.append("NATURAL JOIN Listings NATURAL JOIN Addresses ");
    }
    query.append("WHERE startdate >= ? AND enddate <= ?");
    if (city != null) {
      query.append(" AND city = ?");
    }
    PreparedStatement stmt = conn.prepareStatement(query.toString());
    stmt.setString(1, startdate);
    stmt.setString(2, enddate);
    if (city != null) {
      stmt.setString(3, city);
    }
    stmt.execute();

    query.setLength(0);
    query.append("SELECT u.uid, COUNT(*) AS num " +
            "FROM Users u JOIN bookings_in_range b ON u.uid = b.rid " +
            "GROUP BY u.uid ORDER BY num DESC");
    stmt = conn.prepareStatement(query.toString());
    ResultSet rs = stmt.executeQuery();

    ArrayList<Map<String, Object>> rentersRanked = new ArrayList<>();
    while (rs.next()) {
      int uid = rs.getInt("uid");
      User user = getUserFromUID(uid);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("renter", getUserFromUID(uid));
      map.put("num", rs.getInt("num"));
      rentersRanked.add(map);
    }
    return rentersRanked;
  }

  public ArrayList<Map<String, Object>> getMostCancellations(String type) throws SQLException {
    if (type == null) {
      throw new IllegalArgumentException("Null argument");
    }
    StringBuilder query = new StringBuilder();
    query.append("SELECT u.uid, COUNT(*) AS num ");
    if (type.equals("r")) {
      query.append("FROM Users u JOIN bookings b ON u.uid = b.rid ");
    } else {
      query.append("FROM Listings l JOIN Bookings b ON l.lid = b.lid JOIN Users u ON u.uid = l.hid ");
    }
    query.append("WHERE b.cancelled = true ");
    query.append("GROUP BY u.uid ORDER BY num DESC");

    PreparedStatement stmt = conn.prepareStatement(query.toString());
    ResultSet rs = stmt.executeQuery();

    ArrayList<Map<String, Object>> userCancellations = new ArrayList<>();
    while (rs.next()) {
      int uid = rs.getInt("uid");
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("user", getUserFromUID(uid));
      map.put("num", rs.getInt("num"));
      userCancellations.add(map);
    }

    return userCancellations;
  }

  public List<String> extractNounPhrases(StanfordCoreNLP pipeline, String comment) {
    List <String> nounPhrases = new ArrayList<>();

    Annotation annotation = new Annotation(comment);
    pipeline.annotate(annotation);

    for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
      for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        if (pos.startsWith("NN")) {
          nounPhrases.add(token.get(CoreAnnotations.TextAnnotation.class));
        }
      }
    }

    return nounPhrases;
  }

  public ArrayList<String> getNounPhrases(ArrayList<String> comments) {
//    Logger.getLogger("edu.stanford.nlp").setLevel(Level.SEVERE);
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Map<String, Integer> nounPhraseCounts = new HashMap<>();
    for (String comment : comments) {
      List <String> nounPhrases = extractNounPhrases(pipeline, comment);
      for (String nounPhrase : nounPhrases) {
        nounPhraseCounts.put(nounPhrase, nounPhraseCounts.getOrDefault(nounPhrase, 0) + 1);
      }
    }

    ArrayList<String> topNounPhrases = new ArrayList<>();
    nounPhraseCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> topNounPhrases.add(entry.getKey()));

    return topNounPhrases;
  }

  public ArrayList<Map<String, Object>> getPopularNounPhrases() throws SQLException {
    StringBuilder query = new StringBuilder();
    query.append("SELECT lid FROM Listings");
    PreparedStatement stmt = conn.prepareStatement(query.toString());
    ResultSet rs = stmt.executeQuery();

    ArrayList<Map<String, Object>> listings = new ArrayList<>();
    while (rs.next()) {
      int lid = rs.getInt("lid");
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("lid", lid);
      listings.add(map);
    }

    for (Map<String, Object> stringObjectMap : listings) {
      query.setLength(0);
      query.append("SELECT comment FROM Bookings WHERE lid = ? ");
      query.append("AND comment IS NOT NULL");
      stmt = conn.prepareStatement(query.toString());
      stmt.setInt(1, (int) stringObjectMap.get("lid"));
      rs = stmt.executeQuery();
      ArrayList<String> comments = new ArrayList<>();
      while (rs.next()) {
        comments.add(rs.getString("comment"));
      }
      ArrayList<String> nounPhrases = getNounPhrases(comments);
      stringObjectMap.put("nounPhrases", nounPhrases);
    }
    return listings;
  }

  public String amenitiesListToString(List<Amenity> amenities) {
    StringBuilder set = new StringBuilder();
    set.append("(");
    for (int i = 0; i < amenities.size(); i++) {
      if (i == 0) set.append("'" + amenities.get(i).getAmenity() + "'");
      else set.append("," + "'" + amenities.get(i).getAmenity() + "'");
    }
    return set.append(")").toString();
  }

  public List<Amenity> getAmenitiesListByLID(int lid) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("SELECT amenity, type, description FROM has " +
            "NATURAL JOIN Amenities WHERE lid = ?");
    stmt.setInt(1, lid);
    ResultSet rs = stmt.executeQuery();
    List<Amenity> amenities = new ArrayList<>();
    while(rs.next()) {
      String amenity = rs.getString("amenity");
      String type = rs.getString("type");
      String description = rs.getString("description");
      amenities.add(new Amenity(type, amenity, description));
    }
    return amenities;
  }

  public Amenity getTopNewEssential(List<Amenity> offered) throws SQLException {
    String set = amenitiesListToString(offered);
    PreparedStatement stmt = null;
    if (offered.isEmpty()) {
      stmt = conn.prepareStatement("SELECT Amenities.*, COUNT(*) AS num " +
              "FROM has NATURAL JOIN Amenities WHERE type='Essentials' " +
              "GROUP BY amenity ORDER BY num DESC LIMIT 1");
    } else {
      stmt = conn.prepareStatement("SELECT Amenities.*, COUNT(*) AS num " +
              "FROM has NATURAL JOIN Amenities WHERE type='Essentials' " +
              "AND amenity NOT IN " + set + " GROUP BY amenity ORDER BY num DESC LIMIT 1");
    }

    return resultMaker(stmt.executeQuery());
  }
  public Amenity getTopUncommonFeature(List<Amenity> offered) throws SQLException {
    String set = amenitiesListToString(offered);
    PreparedStatement stmt = null;
    if (offered.isEmpty()) {
      stmt = conn.prepareStatement("SELECT Amenities.*, COUNT(*) as num " +
              "FROM has NATURAL JOIN Amenities WHERE type='Features' " +
              "GROUP BY amenity ORDER BY num ASC LIMIT 1");
    } else {
      stmt = conn.prepareStatement("SELECT Amenities.*, COUNT(*) as num " +
              "FROM has NATURAL JOIN Amenities WHERE type='Features' " +
              "AND amenity NOT IN " + set + " GROUP BY amenity ORDER BY num ASC LIMIT 1");
    }

    return resultMaker(stmt.executeQuery());
  }
  public Amenity resultMaker(ResultSet rs) throws SQLException {
    if (rs.next()) {
      String amenity = rs.getString("amenity");
      String type = rs.getString("type");
      String description = rs.getString("description");
      return new Amenity(type, amenity, description);
    }
    return null;
  }

  public double avgPriceOfListings(String type, List<Amenity> amenities, String country, String province, String city) throws SQLException{
    String set = amenitiesListToString(amenities);
    PreparedStatement stmt = null;
    if (amenities.isEmpty()) {
        stmt = conn.prepareStatement("WITH " +
                "filter AS " +
                "(SELECT lid FROM Listings NATURAL JOIN Addresses WHERE listingtype = ? AND country LIKE ? AND province LIKE ? AND city LIKE ?), " +
                "temp AS " +
                "(SELECT AVG(price) AS price FROM Availabilities WHERE lid IN (SELECT * FROM filter) GROUP BY lid) " +
                "SELECT AVG(price) AS answer FROM temp");
    } else {
      stmt = conn.prepareStatement("WITH " +
              "filterlistings AS " +
              "(SELECT lid FROM Listings NATURAL JOIN Addresses WHERE listingtype = ? AND country LIKE ? AND province LIKE ? AND city LIKE ?), " +
              "filteramenities " +
              "AS (SELECT lid FROM Listings NATURAL JOIN has WHERE amenity IN " + set + " GROUP BY lid HAVING COUNT(*)=" + amenities.size() + "), " +
              "temp AS " +
              "(SELECT AVG(price) AS price FROM Availabilities " +
              "WHERE lid IN (SELECT * FROM filterlistings) AND lid IN (SELECT * FROM filteramenities) " +
              "GROUP BY lid) SELECT AVG(price) AS answer FROM temp");
    }
    stmt.setString(1, type);
    stmt.setString(2, country);
    stmt.setString(3, province);
    stmt.setString(4, city);

    ResultSet rs = stmt.executeQuery();
    if (rs.next()) return rs.getDouble("answer");
    return -1;
  }

  public void close() throws SQLException {
    conn.close();
  }
}
