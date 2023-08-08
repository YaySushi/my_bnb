package bnb;

public class Availability {
  private int lid;
  private double price;
  private String date;
  private String status;

  public Availability(int lid, String status, double price, String date) {
    this.lid = lid;
    this.price = price;
    this.date = date;
    this.status = status;
  }

  @Override
  public String toString() {
    return status + " at " + date + " for $" + price;
  }
}
