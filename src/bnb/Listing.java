package bnb;

import java.sql.SQLException;
import java.util.Locale;

public class Listing {
  private int lid;
  private String listingtype;
  private double lat;
  private double lng;
  private Address address;

  public Listing(int lid, String listingtype, double lat, double lng, Address address) {
    this.lid = lid;
    this.listingtype = listingtype;
    this.address = address;
    this.lat = lat;
    this.lng = lng;
  }

  public int getLid() {
    return lid;
  }

  @Override
  public String toString() {
    return listingtype + " at " + address;
  }
}
