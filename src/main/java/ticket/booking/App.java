package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {

        System.out.println("Running Train Booking System");

        Scanner sc = new Scanner(System.in);
        int option = 0;

        UserBookingService userBookingService;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("There is something wrong while loading data");
            return;
        }

        Train trainSelectedForBooking = null;

        while (option != 7) {

            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = sc.nextInt();

            switch (option) {

                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = sc.next();

                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = sc.next();

                    User userToSignup = new User(
                            nameToSignUp,
                            passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    userBookingService.signUp(userToSignup);
                    break;


                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = sc.next();

                    System.out.println("Enter the password to Login");
                    String passwordToLogin = sc.next();

                    User userToLogin = new User(
                            nameToLogin,
                            passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    try {
                        userBookingService = new UserBookingService(userToLogin);
                    } catch (IOException ex) {
                        System.out.println("Login failed");
                        return;
                    }

                    break;


                case 3:
                    System.out.println("Fetching your bookings");
                    userBookingService.fetchBookings();
                    break;


                case 4:
                    System.out.println("Type your source station");
                    String source = sc.next();

                    System.out.println("Type your destination station");
                    String dest = sc.next();

                    List<Train> trains = userBookingService.getTrains(source, dest);

                    int index = 1;

                    for (Train t : trains) {

                        System.out.println(index + " Train id : " + t.getTrainId());

                        for (Map.Entry<String, String> entry :
                                t.getStationTimes().entrySet()) {

                            System.out.println(
                                    "Station " + entry.getKey()
                                            + " time: "
                                            + entry.getValue()
                            );
                        }

                        index++;
                    }


                    System.out.println("Select a train number");
                    int trainIndex = sc.nextInt() - 1;

                    if (trainIndex >= 0 && trainIndex < trains.size()) {
                        trainSelectedForBooking = trains.get(trainIndex);
                    } else {
                        System.out.println("Invalid train selection");
                    }

                    break;

                case 5:

                    if (trainSelectedForBooking == null) {
                        System.out.println("Please select a train first");
                        break;
                    }

                    System.out.println("Select a seat out of these seats");

                    List<List<Integer>> seats =
                            userBookingService.fetchSeats(trainSelectedForBooking);

                    for (List<Integer> row : seats) {

                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }

                        System.out.println();
                    }
                    System.out.println("Enter the row");
                    int row = sc.nextInt();

                    System.out.println("Enter the column");
                    int col = sc.nextInt();
                    System.out.println("Booking your seat....");

                    Boolean booked =
                            userBookingService.bookTrainSeat(
                                    trainSelectedForBooking,
                                    row,
                                    col
                            );
                    if (booked) {
                        System.out.println("Booked! Enjoy your journey");
                    } else {
                        System.out.println("Can't book this seat");
                    }
                    break;
                case 6:

                    System.out.println("Cancel booking feature");
                    break;
                case 7:

                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
        sc.close();
    }
}