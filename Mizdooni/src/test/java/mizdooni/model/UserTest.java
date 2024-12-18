package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {
    private Address address;
    private User user;
    private User clientUser;
    private User managerUser;

    Restaurant restaurant1;
    Restaurant restaurant2;
    Restaurant restaurant3;
    Restaurant restaurant4;

    Table table1;
    Table table2;
    Table table3;

    @BeforeEach
    public void setUp() {
        address = new Address("Iran", "Tehran", "Kargar");

        restaurant1 = new Restaurant("restaurant1", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant1 description.", address, "imageLink");

        restaurant2 = new Restaurant("restaurant2", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant2 description.", address, "imageLink");

        restaurant3 = new Restaurant("restaurant3", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant3 description.", address, "imageLink");

        restaurant4 = new Restaurant("restaurant4", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant4 description.", address, "imageLink");

        table1 = new Table(1, restaurant1.getId(), 8);
        table2 = new Table(1, restaurant1.getId(), 8);
        table3 = new Table(1, restaurant3.getId(), 8);


        user = new User("testUser", "testPassword", "test@example.com", address, User.Role.client);

        clientUser = new User("client", "cPass", "client@example.com", address, User.Role.client);
        managerUser = new User("manager", "mPass", "manager@example.com", address, User.Role.manager);
    }

    @ParameterizedTest
    @CsvSource({
            "true, testPassword",
            "false, wrongPassword"
    })
    @DisplayName("Test Checking Password")
    public void testCheckingPassword(boolean expectedResult, String password) {
        assertEquals(expectedResult, user.checkPassword(password));
    }

    @Test
    @DisplayName("Test Checking Reservation Count")
    public void testCheckingReservationCount() {
        Reservation reservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        clientUser.addReservation(reservation);
        assertEquals(1, clientUser.getReservations().size());
    }

    @Test
    @DisplayName("Test Checking Ongoing Users Reservation")
    public void testCheckingOngoingUsersReservation() throws InterruptedException {
        Reservation reservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        clientUser.addReservation(reservation);
        Thread.sleep(100);
        assertTrue(clientUser.checkReserved(restaurant1));
    }

    @Test
    @DisplayName("Test Getting Canceled Reservation")
    public void testCheckingCanceledReservation() {
        Reservation reservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        clientUser.addReservation(reservation);
        reservation.cancel();
        assertFalse(clientUser.checkReserved(restaurant1));
    }

    @ParameterizedTest
    @MethodSource("reservationProvider")
    @DisplayName("Test Reservation Scenarios \n1: getting existing reservation\n2:getting cancelled reservation\n3:getting nonexisting reservation")
    public void testReservationScenarios(Reservation reservation, boolean shouldCancel, boolean shouldAdd,
                                         Reservation expected) {
        if (shouldAdd)
            clientUser.addReservation(reservation);
        if (shouldCancel)
            reservation.cancel();
        assertEquals(expected, clientUser.getReservation(reservation.getReservationNumber()));
    }

    private Stream<Arguments> reservationProvider() {
        Reservation existingReservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        Reservation canceledReservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        Reservation nonExistingReservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());;

        return Stream.of(
                Arguments.of(existingReservation, false, true, existingReservation),

                Arguments.of(canceledReservation, true, true, null),

                Arguments.of(nonExistingReservation, false, false, null)
        );
    }

    @Test
    @DisplayName("Test Checking Multiple Ongoing Reservations")
    public void testCheckingMultipleOngoingReservations() {
        Reservation reservation1 = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        Reservation reservation2 = new Reservation(clientUser, restaurant1, table2, LocalDateTime.now());
        Reservation reservation3 = new Reservation(clientUser, restaurant3, table3, LocalDateTime.now());

        clientUser.addReservation(reservation1);
        clientUser.addReservation(reservation2);
        clientUser.addReservation(reservation3);

        Assertions.assertEquals(3, clientUser.getReservations().size());

        Assertions.assertEquals(reservation1, clientUser.getReservation(reservation1.getReservationNumber()));
        Assertions.assertEquals(reservation2, clientUser.getReservation(reservation2.getReservationNumber()));
        Assertions.assertEquals(reservation3, clientUser.getReservation(reservation3.getReservationNumber()));
    }

    @ParameterizedTest
    @CsvSource({
            "true, 0",
            "false, 20"
    })
    @DisplayName("testCheckReservedWithDifferentTimings\n Reservation Time and Add Reservation at Same Time\n Reservation Time after User Add Reservation")
    public void testCheckReservedWithDifferentTimings(boolean expected, int offset) throws InterruptedException {
        Reservation reservation1 = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now().plusDays(offset));
        clientUser.addReservation(reservation1);
        Thread.sleep(100);
        Assertions.assertEquals(expected, clientUser.checkReserved(restaurant1));
    }
}
