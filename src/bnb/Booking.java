package bnb;
import java.sql.SQLException;

public class Booking {
  private int bid;
  private int rid;
  private int lid;
  private String startdate;
  private String enddate;
  private double cost;
  private String comment;
  private int rating;
  private boolean cancelled;

  public Booking(int bid, int rid, int lid, String startdate, String enddate,
                 double cost, String comment, int rating) {
    this.bid = bid;
    this.rid = rid;
    this.lid = lid;
    this.startdate = startdate;
    this.enddate = enddate;
    this.cost = cost;
    this.comment = comment;
    this.rating = rating;
    this.cancelled = false;
  }

  public String display(DAO dao) {
    try {
      Listing listing = dao.getListingFromID(lid);
      return listing.toString() + " from " + startdate + " to " + enddate + " for cost of " + cost;
    } catch (SQLException e) {
      return "listing id: " + lid + " from " + startdate + " to " + enddate + " for cost of " + cost;
    }
  }

  public int getLid() { return lid; }

  public int getBid() { return bid; }

  public String getStartDate() { return startdate; }

  public String getEndDate() { return enddate; }

  public String getComment() { return comment; }
}
