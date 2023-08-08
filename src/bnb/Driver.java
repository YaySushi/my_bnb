package bnb;

import java.sql.SQLException;
import java.util.*;

public class Driver {
  private static Scanner scanner;
  private static DAO dao;
  private static User loggedInUser = null;

  public static boolean regularInput(int choice) throws SQLException {
    if (choice == 1) {
      if (!signup()) System.out.println("Sign up failed.");
    } else if (choice == 2) {
      if (login()) {
        System.out.println("Log in successful.");
        return true;
      } else System.out.println("Log in failed.");
    } else if (choice == 3) {
      try { handleReportsInput(); }
      catch (SQLException e) {
        e.printStackTrace();
        System.out.print("Something went wrong.");
      }
    } else {
      System.out.println("Exiting...");
      dao.close();
      System.exit(0);
    }
    return false;
  }
  public static boolean hostInput(int choice) {
    List<Booking> bookings;
    try {
      if (choice == 1) createListing();
      else if (choice == 2) viewHostListings();
      else if (choice == 3) {

        bookings = displayBookings("future");
        if (bookings.isEmpty()) System.out.println("No bookings to display");
        else cancelBooking(bookings);

      } else if (choice == 4) {

        bookings = displayBookings("past");
        if (bookings.isEmpty()) System.out.println("No bookings to display");

      } else if (choice == 5) {

        bookings = displayBookings("cancelled");
        if (bookings.isEmpty()) System.out.println("No bookings to display");

      } else if (choice == 6) {

        List<User> renters = displayUsers();
        if (renters.isEmpty()) System.out.println("No renters to display");
        else commentOnUser(renters);

      } else if (choice == 7) {
        dao.deleteUser(loggedInUser.getUid());
        System.out.println("User deleted successfully");
        loggedInUser = null;
        return false;
      } else if (choice == 8) {

        System.out.println("Thank you for using MyBnB!");
        loggedInUser = null;
        return false;

      } else System.out.println("Invalid Choice");
    } catch (Exception e) {
      System.out.println("Something went wrong");
      e.printStackTrace();
    }
    return true;
  }
  public static boolean renterInput(int choice) {
    try {
      if (choice == 1) {

        List<Listing> listings = showListings();
        if (listings.isEmpty()) System.out.println("No listings to show");
        else createBooking(listings);

      } else if (choice == 2) {

        List<Booking> futureBookings = displayBookings("future");
        if (futureBookings.isEmpty())  System.out.println("No bookings to show");
        else cancelBooking(futureBookings);

      } else if (choice == 3) {

        List<Booking> pastBookings = displayBookings("past");
        if (pastBookings.isEmpty()) System.out.println("No bookings to show");
        else commentOnBooking(pastBookings);

      } else if (choice == 4) {

        List<Booking> cancelledBookings = displayBookings("cancelled");
        if (cancelledBookings.isEmpty()) System.out.println("No bookings to display");
        else commentOnBooking(cancelledBookings);

      } else if (choice == 5) {

        List<User> hosts = displayUsers();
        if (hosts.isEmpty()) System.out.println("No hosts to display");
        else commentOnUser(hosts);

      } else if (choice == 6) {
        dao.deleteUser(loggedInUser.getUid());
        System.out.println("User deleted successfully");
        loggedInUser = null;
        return false;
      } else if (choice == 7) {

        System.out.println("Thank you for using MyBnB!");
        return false;

      } else System.out.println("Invalid choice");
    } catch (Exception e) {
      System.out.println("Something went wrong");
      e.printStackTrace();
    }
    return true;
  }

  public static List<Listing> showListings() throws SQLException {
    System.out.println("============Listing Search Menu ============");
    System.out.println("Choose search option: ");
    System.out.println("1: All listings");
    System.out.println("2: All listings near a coordinate");
    System.out.println("3: All listings near a postal code");
    System.out.println("4: All listings at an address");

    System.out.print("Enter Input: ");
    int input = scanner.nextInt();

    StringBuilder query = new StringBuilder();

    //build a big query progressively.
    query.append("SELECT * FROM Listings NATURAL JOIN Addresses ");

    if (input == 2) {
      System.out.print("Enter latitude (-90.0 to 90.0) and longitude (-180.0 to 180.0): ");
      double lat = scanner.nextDouble();
      double lng = scanner.nextDouble();

      System.out.print("Specify distance (km) or enter -1 for default (5000km): ");
      double distance = scanner.nextDouble() * 1000;
      if (distance < 0) distance = 5000;

      query.setLength(0);
      query.append("WITH temp AS (SELECT *, ST_Distance_Sphere(point(" + lng + ", " + lat + "), " +
              "point(lng, lat)) as dist FROM Listings NATURAL JOIN Addresses) " +
              "SELECT * FROM temp WHERE dist <= " + distance + " ORDER BY dist");
    } else if (input == 3) {
      System.out.print("Enter postal code (length >= 3): ");
      String postalcode = scanner.next().toLowerCase(Locale.ROOT).substring(0, 3);
      query.append(" WHERE SUBSTRING(postalcode, 1, 3)='" + postalcode + "'");
    } else if (input == 4) {
      scanner.nextLine();
      System.out.print("Enter address: ");
      String address = scanner.nextLine().toLowerCase(Locale.ROOT).trim();
      System.out.print("City: ");
      String city = scanner.nextLine().toLowerCase(Locale.ROOT).trim();
      System.out.print("Province: ");
      String province = scanner.nextLine().toLowerCase(Locale.ROOT).trim();
      System.out.print("Country: ");
      String country = scanner.nextLine().toLowerCase(Locale.ROOT).trim();
      query.append((" WHERE address='%s' AND city='%s' AND province='%s' AND country='%s'").formatted(address, city, province, country));
    }

    return specSearch(query);
  }
  public static List<Listing> specSearch(StringBuilder query) throws SQLException {

    dao.deleteView("NoFilter");
    dao.deleteView("FilterDate");
    dao.deleteView("FilterPrice");
    dao.deleteView("FilterType");
    dao.deleteView("FilterAmenity");

    dao.createView("NoFilter", query.toString());

    query = new StringBuilder();

    System.out.print("Filter by date range? (y/n): ");
    String response = scanner.next().trim().toLowerCase(Locale.ROOT);

    if (response.equals("y")) {
      System.out.print("Enter start date (YYYY-MM-DD): ");
      String startdate = scanner.next();
      System.out.print("Enter end date (YYYY-MM-DD): ");
      String enddate = scanner.next();

      query.append("SELECT * FROM NoFilter WHERE lid IN (SELECT L.lid FROM Listings L, Availabilities A " +
              "WHERE L.lid=A.lid AND A.status='Available' AND " +
              "date BETWEEN '%s' AND '%s' ".formatted(startdate, enddate) +
              "GROUP BY L.lid HAVING COUNT(*)=DATEDIFF('%s', '%s')+1)".formatted(enddate, startdate));
      dao.createView("FilterDate", query.toString());
    } else dao.createView("FilterDate", "SELECT * FROM NoFilter");

    query = new StringBuilder();

    System.out.print("Filter by price range? (y/n): ");
    response = scanner.next();

    if (response.equalsIgnoreCase("y")) {
      System.out.print("Enter minimum price: $");
      Double min = scanner.nextDouble();
      System.out.print("Enter maximum price: $");
      Double max = scanner.nextDouble();

      query.append("SELECT * FROM FilterDate WHERE lid IN (SELECT L.lid FROM Listings L, Availabilities A "+
              "WHERE L.lid=A.lid AND price BETWEEN " + min + " AND " + max + ")");
      dao.createView("FilterPrice", query.toString());
    } else dao.createView("FilterPrice", "SELECT * FROM FilterDate");


    query = new StringBuilder();

    System.out.print("Filter by amenities offered? (y/n): ");
    response = scanner.next().toLowerCase(Locale.ROOT);
    if (response.equals("y")) {
      scanner.nextLine();
      //TODO: change this mechanism into entering one amenity per line.
      System.out.print("Enter amenities (comma separated): ");
      String str = scanner.nextLine();
      String [] amenities = str.split(",");
      StringBuilder set = new StringBuilder();
      set.append("(");
      for (int i=0; i<amenities.length; i++) {
        if (i==0) {
          set.append("'" + amenities[i].trim() + "'");
        } else {
          set.append("," + "'" + amenities[i].trim() + "'");
        }
      }
      set.append(")");

      query.append("SELECT * FROM FilterPrice WHERE lid IN (SELECT lid FROM Listings NATURAL JOIN has " +
              "WHERE amenity IN " + set + " GROUP BY lid HAVING COUNT(*)=" + amenities.length + ")");
      dao.createView("FilterAmenity", query.toString());
    } else dao.createView("FilterAmenity", "SELECT * FROM FilterPrice");

    query = new StringBuilder();

    System.out.print("Filter by type? (y/n): ");
    response = scanner.next();

    if (response.equalsIgnoreCase("y")) {
      System.out.print("Enter type (Apartment, House, Guesthouse, Hotel, Other): ");
      String type = scanner.next().trim().toLowerCase(Locale.ROOT);
      query.append("SELECT * FROM FilterAmenity WHERE listingtype = '%s'".formatted(type));
      dao.createView("FilterType", query.toString());
    } else dao.createView("FilterType", "SELECT * FROM FilterAmenity");


    ArrayList<Listing> listings;

    System.out.print("Rank by price? (asc/desc/n): ");
    String str = scanner.next().trim().toUpperCase(Locale.ROOT);
    listings = dao.getListingsFromView("FilterType", str);

    for (int j = 0; j < listings.size(); j++) System.out.println(j + ") " + listings.get(j));
    return listings;
  }

  public static List<User> displayUsers() throws SQLException {
    List<User> users;
    if (loggedInUser.getClass() == Host.class) users = dao.getRentersForHost(loggedInUser.getUid());
    else users = dao.getHostsForRenter(loggedInUser.getUid());

    for (int i = 0; i < users.size(); i++) System.out.println(i + ": " + users.get(i).toString());

    return users;
  }
  public static List<Booking> displayBookings(String status) throws SQLException {
    List<Booking> bookings;
    if (loggedInUser.getClass().equals(Renter.class)) bookings = dao.getRentersBookings(status, loggedInUser.getUid());
    else bookings = dao.getHostsBookings(status, loggedInUser.getUid());

    for (int i=0; i<bookings.size(); i++) System.out.println(i + ") " + bookings.get(i).display(dao));

    return bookings;
  }
  public static void createBooking(List<Listing> listings) throws SQLException {
    System.out.print("Select a listing to book (-1 to exit): ");
    int input = scanner.nextInt();

    if (input == -1) return;

    if (input < 0 || input > listings.size()) {
      System.out.println("Invalid listing selected");
      return;
    }

    int lid = listings.get(input).getLid();

    System.out.print("Would you like to see availabilities in a date range for this listing? (y/n): ");
    String in = scanner.next().toLowerCase(Locale.ROOT).trim();
    while (in.equals("y")) {
      viewAvailabilities(lid);
      System.out.print("Would you like to see more availabilities? (y/n): ");
      in = scanner.next().toLowerCase(Locale.ROOT).trim();
    }

    System.out.print("Enter start date of booking (YYYY-MM-DD): ");
    String startDate = scanner.next();
    System.out.print("Enter end date of booking (YYYY-MM-DD): ");
    String endDate = scanner.next();

    System.out.print("Enter y to confirm this booking: ");
    String response = scanner.next().trim();

    if (response.equalsIgnoreCase("y")) {
      if (dao.checkAvailability(lid, startDate, endDate)) {
        Double cost = dao.getCost(lid, startDate, endDate);
        if (cost == -1) System.out.println("Something went wrong computing the cost.\nUnable to create booking.");
        else {
          dao.updateAvailabilityStatus(lid, startDate, endDate, "Booked");
          dao.createBooking(loggedInUser.getUid(), lid, startDate, endDate, cost);
          System.out.println("New booking was created. Enjoy!");
        }
      } else System.out.println("Booking failed: listing is not available for the given date range");
    } else System.out.println("Booking was not created.");
  }

  /* Displays all host's listings and allows host to select which they want to update */
  public static void viewHostListings() {
    try {
      boolean exit = false;
      while (!exit) {
        System.out.println("============ Active Listings ============");
        ArrayList<Listing> hostListings = dao.getListingsByHid(loggedInUser.getUid());

        for (int i = 0; i < hostListings.size(); i++) System.out.println(i + 1 + ": " + hostListings.get(i));


        // get input for updating or viewing listings
        System.out.println("Options:");
        System.out.println("1. View availabilities for a listing");
        System.out.println("2. Update a listing");
        System.out.println("3. Exit");
        System.out.print("Select an option: ");
        String input = scanner.next();

        if (input.equals("1")) {

          System.out.print("Enter listing number to view its availabilities: ");
          int listing = scanner.nextInt();
          if (listing - 1 >= 0 && listing - 1 < hostListings.size()) {
            viewAvailabilities(hostListings.get(listing - 1).getLid());
          } else System.out.println("Invalid input.");

        } else if (input.equals("2")) {

          System.out.print("Enter listing number to update: ");
          int listing = scanner.nextInt();
          if (listing - 1 >= 0 && listing - 1 < hostListings.size()) {
            updateListing(hostListings.get(listing - 1).getLid());
          } else System.out.println("Invalid input.");

        } else if (input.equals("3")) exit = true;
        else System.out.println("Invalid input.");

      }

    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("There was a problem getting listings.");
    }
  }

  /* Allows host to view availabilities for a listing within a date range. */
  public static void viewAvailabilities(int lid) {
    System.out.print("Enter start date (YYYY-MM-DD): ");
    String startDate = scanner.next();

    System.out.print("Enter end date (YYYY-MM-DD): ");
    String endDate = scanner.next();

    try {
      ArrayList<Availability> availabilities = dao.getAvailabilitiesInRange(lid, startDate, endDate);

      //display availabilities
      for (int i = 0; i < availabilities.size(); i++) System.out.println(availabilities.get(i));

      System.out.println("Total number of availabilities in this range: " + availabilities.size());
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("Issue retrieving availabilities");
    }
  }

  private static void handleReportsInput() throws SQLException {
    System.out.println("============ Reports Menu ============");
    System.out.println("1: Number of bookings in a date range by city (and postal code)");
    System.out.println("2: Number of listings per country (and city (and postal code))");
    System.out.println("3: Ranking of hosts by total number of listings per country (and city)");
    System.out.println("4: Display hosts with more than 10% of the listings in the country (and city)");
    System.out.println("5: Ranking of renters by the number of bookings in a date range (and city)");
    System.out.println("6: Hosts or renters with highest number of cancellations per year");
    System.out.println("7: Display popular noun phrases for each listing");

    System.out.print("Select an option: ");
    int choice = scanner.nextInt();
    switch (choice) {
      // 1: Number of bookings in a date range and city (and postal code)
      case 1: {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.next().trim();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.next().trim();
        scanner.nextLine();
        System.out.print("Enter city: ");
        String city = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        System.out.print("Would you like to search by postal code as well? (y/n): ");
        String postalCode = optionalResponse("postal code");

        int numBookings = dao.getNumBookings(startDate, endDate, city, postalCode);
        System.out.println("Number of bookings: " + numBookings);
        break;
      }

      // 2: Number of listings per country (and city (and postal code))
      case 2: {
        System.out.print("Enter country: ");
        String country = scanner.next().trim().toLowerCase(Locale.ROOT);
        System.out.print("Would you like to search by city as well? (y/n): ");
        String city = optionalResponse("city");

        System.out.print("Would you like to search by postal code as well? (y/n): ");
        String postalCode = optionalResponse("postal code");

        int numListingsPerCountry = dao.getNumListingsPerCountry(country, city, postalCode);
        System.out.println("Number of listings: " + numListingsPerCountry);
        break;
      }

      // 3: Rank hosts based on number of listings per country (or city)
      case 3: {
        System.out.print("Enter country: ");
        String country = scanner.next().trim().toLowerCase(Locale.ROOT);
        System.out.print("Would you like to search by city as well? (y/n): ");
        String city = optionalResponse("city");

        ArrayList<Map<String, Object>> hostRankings = dao.getRankHosts(country, city);
        if (hostRankings.isEmpty()) {
          System.out.println("No hosts found with search parameters.");
          break;
        }
        for (int i = 0; i < hostRankings.size(); i++) {
          Map<String, Object> hostElem = hostRankings.get(i);
          System.out.println(i + 1 + ": " + hostElem.get("host") + "\t (" + hostElem.get("num") + " listing(s))");
        }
        break;
      }

      // 4: Display hosts with more than 10% listings in that country (and city)
      case 4: {
        System.out.print("Enter country: ");
        String country = scanner.next().trim().toLowerCase(Locale.ROOT);
        System.out.print("Would you like to search by city as well? (y/n): ");
        String city = optionalResponse("city");

        ArrayList<Map<String, Object>> hostsTenPercent = dao.getHostsWithMoreThanTenPercent(country, city);
        if (hostsTenPercent.isEmpty()) {
          System.out.println("No hosts found with search parameters.");
          break;
        }

        for (int i = 0; i < hostsTenPercent.size(); i++) {
          Map<String, Object> hostElem = hostsTenPercent.get(i);
          System.out.println(i + 1 + ": " + hostElem.get("host") + "\t (" + hostElem.get("percentage") + "%)");
        }
        break;
      }

      // 5: Rank renters based on number of bookings in a date range (and city)
      case 5: {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.next().trim();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.next().trim();
        System.out.print("Would you like to search by city as well? (y/n): ");
        String city = optionalResponse("city");

        ArrayList<Map<String, Object>> renterRankings = dao.getRankedRenters(startDate, endDate, city);
        if (renterRankings.isEmpty()) {
          System.out.println("No renters found with search parameters.");
          break;
        }
        for (int i = 0; i < renterRankings.size(); i++) {
          Map<String, Object> renterElem = renterRankings.get(i);
          System.out.println(i + 1 + ": " + renterElem.get("renter") + "\t (" + renterElem.get("num") + " booking(s))");
        }
        break;
      }

      // 6: Hosts or renters with the highest number of cancellations
      case 6: {
        System.out.print("Would you like to search for hosts or renters with the most cancellations? (h/r): ");
        String type = scanner.next().trim().toLowerCase(Locale.ROOT);
        if (!(type.equals("h") || type.equals("r"))) {
          System.out.println("Invalid input.");
          break;
        }
        ArrayList<Map<String, Object>> cancellations = dao.getMostCancellations(type);
        if (cancellations.isEmpty()) {
          System.out.println("No cancellations found.");
          break;
        }

        for (int i = 0; i < cancellations.size(); i++) {
          Map<String, Object> cancellationElem = cancellations.get(i);
          System.out.println(i + 1 + ": " + cancellationElem.get("user") + "\t (" + cancellationElem.get("num") + " cancellation(s))");
        }
        break;
      }

      // 7: Display popular noun phrases for each listing
      case 7: {
        ArrayList<Map<String, Object>> nounPhrases = dao.getPopularNounPhrases();

        for (Map<String, Object> nounPhrase : nounPhrases) {
          ArrayList<String> nounPhrasesForListing = (ArrayList<String>) nounPhrase.get("nounPhrases");
          System.out.println("lid " + nounPhrase.get("lid") + ": " + nounPhrasesForListing);
        }
        break;
      }
    }
  }

  public static String optionalResponse(String type) {
    String response = scanner.next().trim();
    if (response.equalsIgnoreCase("y")) {
      System.out.print("Enter " + type + ": ");
      return scanner.next().trim().toLowerCase(Locale.ROOT);
    }
    return null;
  }

  public static void hostToolkit(int lid, String type, String city, String province, String country, String postalcode) {
    try {
      System.out.println("============ Host Toolkit ==============");
      List<Amenity> offered = dao.getAmenitiesListByLID(lid);
      double price = getPriceRecc(type, offered, city, province, country);
      if (price <= 0) {
        System.out.println("We could not find a suitable price recommendation.");
      } else {
        System.out.println("We recommend a price of: $" + price);
      }

      // average price of a listing with at least the offered amenities
      price = getAvgPriceWithAmenities(type, offered, city, province, country);
      Amenity topEssential = dao.getTopNewEssential(offered);
      Amenity topUncommonFeature = dao.getTopUncommonFeature(offered);

      if (topEssential == null) System.out.println("No essentials to recommend");
      else {
        System.out.println("Consider adding the following essential/safety amenity: " + topEssential.getAmenity());

        //calculate price difference.
        offered.add(topEssential);
        double newPrice = getAvgPriceWithAmenities(type, offered, city, province, country);
        double priceIncrease = newPrice - price;
        if (priceIncrease > 0) System.out.println("...with price increase of " + priceIncrease);
      }

      if (topUncommonFeature == null) System.out.println("No features to recommend");
      else {
        System.out.println("Consider adding the following feature amenity: " + topUncommonFeature.getAmenity());

        //calculate price difference.
        offered.add(topUncommonFeature);
        double newPrice = getAvgPriceWithAmenities(type, offered, city, province, country);
        double priceIncrease = newPrice - price;
        if (priceIncrease > 0) System.out.println("...with price increase of " + priceIncrease);
      }
      System.out.println("=========================================");
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Something went wrong trying to recommend listings");
    }
  }

  public static double getPriceRecc(String type, List<Amenity> offered,
                                    String city, String province, String country) throws SQLException {
    double price;

    // try looking at listings at all specified parameters
    price = dao.avgPriceOfListings(type, offered, country, province, city);
    if (price > 0) return price;

    // otherwise look at listings ignoring the city
    price = dao.avgPriceOfListings(type, offered, country, province, "%");
    if (price > 0) return price;

    // otherwise look at listings ignoring the city and province.
    price = dao.avgPriceOfListings(type, offered, country, "%", "%");
    if (price > 0) return price;

    return 0;
  }

  public static double getAvgPriceWithAmenities(String type, List<Amenity> offered,
                                             String city, String province, String country) throws SQLException {
    // try looking at listings at all specified parameters
    double price;
    price = dao.avgPriceOfListings(type, offered, country, province, city);
    if (price > 0) return price;

    // otherwise look at listings ignoring the city
    price = dao.avgPriceOfListings(type, offered, country, province, "%");
    if (price > 0) return price;

    // otherwise look at listings ignoring the city and province.
    price = dao.avgPriceOfListings(type, offered, country, "%", "%");
    if (price > 0) return price;
    return price;
  }

  public static void addAmenities(int lid, String type, String city, String province, String country, String postalcode) {
    System.out.println("===== Adding amenities to listing =====");

    try {
      ArrayList<String>[] types = new ArrayList[4];
      types[0] = dao.getAmenitiesListByType("Essentials");
      types[1] = dao.getAmenitiesListByType("Features");
      types[2] = dao.getAmenitiesListByType("Location");
      types[3] = dao.getAmenitiesListByType("Safety");

      // loop to get user input
      while (true) {
        System.out.println("Select the type of amenity (-1 to exit):");
        System.out.println("1. Essentials");
        System.out.println("2. Features");
        System.out.println("3. Location");
        System.out.println("4. Safety");
        System.out.println("5. View host toolkit for suggestions");

        int input = scanner.nextInt();
        scanner.nextLine();

        if (input == -1) break;
        else if (input == 5) {
          hostToolkit(lid, type, city, province, country, postalcode);
        } else {
          while (true) {
            System.out.println("Available amenities of this type: ");
            System.out.println(types[input - 1]);

            System.out.print("Enter amenity to add (q to quit): ");
            String amenity = scanner.nextLine().trim();

            if (amenity.equals("q")) break;

            if (types[input - 1].contains(amenity)) {
              types[input - 1].remove(amenity);
              dao.addAmenity(lid, amenity);
              System.out.println("Amenity added.");
            } else {
              System.out.println("Invalid amenity.");
            }
          }
        }
      }
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("There was a problem adding the amenity.");
    } catch (InputMismatchException ime) {
      System.out.printf("Invalid input.");
    }

    System.out.println("Finished adding amenities.");
  }

  public static void updateListing(int lid) {
    System.out.println("Possible operations: ");
    System.out.println("1. Add availability");
    System.out.println("2. Modify availability price");
    System.out.println("3. Remove availability");

    System.out.print("Select operation (-1 to exit): ");
    int input = scanner.nextInt();
    double price;
    int temp;

    if (input == -1) return;

    System.out.print("Enter start date (YYYY-MM-DD): ");
    String startdate = scanner.next();
    System.out.print("Enter end date (YYYY-MM-DD): ");
    String enddate = scanner.next();
    try {
      if (input == 1) {
        System.out.print("Enter price: $");
        price = scanner.nextDouble();
        temp = dao.createAvailabilitiesInRange(lid, startdate, enddate, price);
        System.out.println("Number of availabilities made available: " + temp);
      } else if (input == 2) {
        System.out.print("Enter new price: $");
        price = scanner.nextDouble();
        temp = dao.updateAvailabilityInRange(lid, startdate, enddate, price);
        System.out.println("Number of availabilities modified: " + temp);
        System.out.println("NOTE: the price of booked availabilities of a listing did NOT change.");
      } else if (input == 3) {
        temp = dao.cancelAvailabilitiesInRange(lid, startdate, enddate);
        System.out.println("Number of availabilities cancelled: " + temp);
      }
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("An error occurred with this operation. Please try again.");
    }
  }

  public static void createListing() {
    System.out.print("Type of listing (House, Apartment, Guesthouse, Hotel, Other): ");
    String type = scanner.next();

    System.out.print("Latitude (-90.00 to 90.00): ");
    double lat = scanner.nextDouble();

    System.out.print("Longitude (-180.00 to 180.00): ");
    double lng = scanner.nextDouble();

    scanner.nextLine();

    System.out.print("Address: ");
    String address = scanner.nextLine().trim();

    System.out.print("City: ");
    String city = scanner.nextLine().trim();

    System.out.print("Province: ");
    String province = scanner.nextLine().trim();

    System.out.print("Country: ");
    String country = scanner.nextLine().trim();

    System.out.print("Postal code: ");
    String postalcode = scanner.next().trim();

    try {
      int aid = dao.createAddress(
              address.toLowerCase(Locale.ROOT).trim(),
              city.toLowerCase(Locale.ROOT).trim(),
              province.toLowerCase(Locale.ROOT).trim(),
              country.toLowerCase(Locale.ROOT).trim(),
              postalcode.toLowerCase(Locale.ROOT).trim()
      );
      int lid = dao.createListing(loggedInUser.getUid(), type, lat, lng, aid);
      System.out.println("Listing created successfully!");

      System.out.print("\nWould you like to add amenities right now? (y/n): ");

      String response = scanner.next().trim().toLowerCase(Locale.ROOT);
      if (response.equals("y")) addAmenities(lid, type, city, province, country, postalcode);

      System.out.print("\nWould you like to add available dates right now? (y/n): ");
      response = scanner.next().trim().toLowerCase(Locale.ROOT);
      if (response.equals("y")) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startdate = scanner.next();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String enddate = scanner.next();

        System.out.print("Enter price: $");
        double price = scanner.nextDouble();

        int created = dao.createAvailabilitiesInRange(lid, startdate, enddate, price);
        System.out.println("Number of availabilities made available: " + created);
      }

    } catch (IllegalArgumentException iae) {
      System.out.println("Invalid input.");
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("An error occurred while adding the listing.");
    }
  }

  public static boolean signup() {
    System.out.print("Enter 1 to sign up as a renter, or 2 to sign up as a host: ");
    int choice = scanner.nextInt();

    System.out.print("Email: ");
    String email = scanner.next();

    System.out.print("Password: ");
    String password = scanner.next();

    try {
      String creditcard = null;
      if (choice == 1) {
        scanner.nextLine();
        System.out.print("Credit Card (xxxx xxxx xxxx xxxx): ");

        creditcard = scanner.nextLine();

        if (Renter.exists(dao, email, password, creditcard)) return true;
      } else if (choice == 2) {
        if (Host.exists(dao, email, password)) return true;
      } else {
        System.out.println("Invalid option");
        return false;
      }

      System.out.print("First name: ");
      String firstname = scanner.nextLine();

      System.out.print("Last name: ");
      String lastname = scanner.nextLine();

      System.out.print("DOB (YYYY-MM-DD): ");
      String dob = scanner.nextLine();

      System.out.print("SIN: ");
      String sin = scanner.nextLine();

      System.out.print("Occupation: ");
      String occupation = scanner.nextLine();

      System.out.print("Address: ");
      String address = scanner.nextLine();

      System.out.print("City: ");
      String city = scanner.nextLine();

      System.out.print("Province: ");
      String province = scanner.nextLine();

      System.out.print("Country: ");
      String country = scanner.nextLine();

      System.out.print("Postal code: ");
      String postalcode = scanner.next();

      if (choice == 1) {
        return Renter.signup(dao, sin, firstname, lastname, email, password, dob, occupation, address, city, province, country, postalcode, creditcard);
      } else if (choice == 2) {
        return Host.signup(dao, sin, firstname, lastname, email, password, dob, occupation, address, city, province, country, postalcode);
      }
    } catch (IllegalArgumentException iae) {
      System.out.println("Invalid input. Please enter correctly formatted data.");
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("An error occurred while signing up.");
    }
    return false;
  }
  public static boolean login() {
    System.out.print("Enter 1 to login as a renter, or 2 to login as a host: ");
    String choice = scanner.next();

    if (!choice.equals("1") && !choice.equals("2")) {
      System.out.println("Invalid choice.");
      return false;
    }

    System.out.print("Email: ");
    String email = scanner.next();

    System.out.print("Password: ");
    String password = scanner.next();

    try {
      if (choice.equals("1")) loggedInUser = Renter.login(dao, email, password);
      else if (choice.equals("2")) loggedInUser = Host.login(dao, email, password);
    } catch (SQLException sql) {
      sql.printStackTrace();
      System.out.println("An error occurred while logging in.");
    }
    return loggedInUser != null;
  }

  public static void commentOnUser(List<User> users) {
    if (users.size() == 0) {
      System.out.println("No users to comment on.");
      return;
    }
    if (loggedInUser.getClass() == Host.class) {
      System.out.println("Select a renter to comment on (-1 to cancel): ");
    } else {
      System.out.println("Select a host to comment on (-1 to cancel): ");
    }
    int choice = scanner.nextInt();
    if (choice == -1) return;
    if (choice < 0 || choice >= users.size()) {
      System.out.println("Invalid choice.");
      return;
    }
    User user = users.get(choice);
    System.out.print("Enter your rating (1-5): ");
    int rating = scanner.nextInt();
    if (rating < 1 || rating > 5) {
      System.out.println("Invalid rating, must be between 1 and 5.");
      return;
    }
    scanner.nextLine();
    System.out.print("Enter your comment: ");
    String comment = scanner.nextLine();
    if (loggedInUser.getClass() == Host.class) {
      try {
        System.out.println(user);
        System.out.println(loggedInUser.getUid());

        Comment.createComment(dao, comment, rating, user.getUid(), loggedInUser.getUid(), "Host");
      } catch (SQLException sql) {
        sql.printStackTrace();
        System.out.println("There was a problem creating your comment.");
      }
    } else {
      try {
        Comment.createComment(dao, comment, rating, loggedInUser.getUid(), user.getUid(), "Renter");
      } catch (SQLException sql) {
        sql.printStackTrace();
        System.out.println("There was a problem creating your comment.");
      }
    }
  }
  public static void commentOnBooking(List<Booking> bookings) throws SQLException {
    System.out.print("Select a booking you would like to comment on (-1 to exit): ");
    int input = scanner.nextInt();
    if (input < 0 || input > bookings.size()) {
      System.out.println("Invalid Booking");
      return;
    }
    Booking booking = bookings.get(input);
    if (booking.getComment() != null) {
      System.out.println("Selected booking has already been commented on.");
      return;
    }
    scanner.nextLine();
    System.out.print("Comment: ");
    String comment = scanner.nextLine();
    System.out.print("Rating (1-5): ");
    int rating = scanner.nextInt();
    dao.commentOnBooking(booking.getBid(), comment, rating);
    System.out.println("Booking commented on successfully.");
  }
  public static void cancelBooking(List<Booking> bookings) throws SQLException {
    System.out.print("Select a booking you would like to cancel (-1 to exit): ");
    int input = scanner.nextInt();

    if (input == -1) return;

    if (input < 0 || input > bookings.size()) {
      System.out.println("Invalid Booking.");
      return;
    }
    Booking booking = bookings.get(input);
    dao.updateAvailabilityStatus(booking.getLid(), booking.getStartDate(),booking.getEndDate(), "Available");
    dao.cancelBooking(booking.getBid());
    System.out.println("Booking cancelled successfully.");
  }

  public static void main(String[] args) {
    try {
      dao = new DAO("mybnb", "root", "password");
      scanner = new Scanner(System.in);
      boolean isLoggedIn = false;

      System.out.println("\n\n\n===========================");
      System.out.println("|    Welcome to MyBnB!    |");
      System.out.println("===========================\n\n\n");


      while (true) {
        if (isLoggedIn) {
          if (loggedInUser.getClass() == Renter.class) {
            System.out.println("============ Renter Menu ============");
            System.out.println("1: View or book listings");
            System.out.println("2: View or cancel upcoming bookings");
            System.out.println("3: View or comment on past bookings");
            System.out.println("4: View or comment on cancelled bookings");
            System.out.println("5: Comment on a host");
            System.out.println("6: Delete account");
            System.out.println("7: Log out");

            System.out.print("Enter input: ");
            isLoggedIn = renterInput(scanner.nextInt());
          } else {
            System.out.println("============ Host Menu ============");
            System.out.println("1: Create a listing");
            System.out.println("2: View or update listings");
            System.out.println("3: View or cancel bookings");
            System.out.println("4: View or comment on past bookings");
            System.out.println("5: View cancelled bookings");
            System.out.println("6: Comment on a renter");
            System.out.println("7: Delete account");
            System.out.println("8: Log out");

            System.out.print("Enter input: ");
            isLoggedIn = hostInput(scanner.nextInt());
          }
        } else {
          System.out.println("============ Main Menu ============");
          System.out.println("1: Sign up");
          System.out.println("2: Log in");
          System.out.println("3: View reports");
          System.out.println("4: Exit");

          System.out.print("Enter input: ");
          int choice = scanner.nextInt();

          if (choice == 4) {
            System.out.println("Exiting. Bye!");
            break;
          }
          isLoggedIn = regularInput(choice);
        }
      }
      dao.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}