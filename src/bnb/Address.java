package bnb;

public class Address {
  private int aid;
  private String address;
  private String city;
  private String province;
  private String country;
  private String postalCode;

  public Address(int aid, String address, String city, String province, String country, String postalCode) {
    this.aid = aid;
    this.address = address;
    this.city = city;
    this.province = province;
    this.country = country;
    this.postalCode = postalCode;
  }

  @Override
  public String toString() {
    return address + ", " + city + ", " + province + ", " + country + ", " + postalCode;
  }
}