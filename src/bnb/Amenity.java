package bnb;

public class Amenity {
  private String description;
  private String type;
  private String amenity;

  public Amenity(String type, String amenity, String description) {
    this.description = description;
    this.type = type;
    this.amenity = amenity;
  }

  public String getAmenity() { return amenity; }

}
