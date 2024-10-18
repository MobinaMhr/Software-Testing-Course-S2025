package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class TableTest {
    private Restaurant restaurant;
    private Table table;
    private Reservation reservation1;
    private Reservation reservation2;
    private User manager;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        Address address = new Address("Iran", "Tehran", "Kargar");
        manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address,
                User.Role.manager);
        user1 = new User("mmd", "password", "mmd@example.com", address, User.Role.client);
        user2 = new User("mobina", "password", "mobina@example.com", address, User.Role.client);
        restaurant = new Restaurant("Baradaran Akbari bejoz Davood", manager, "Kababi", LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");
        table = new Table(1, restaurant.getId(), 4);
        reservation1 = new Reservation(user1, restaurant, table, LocalDateTime.now());
        reservation2 = new Reservation(user2, restaurant, table, LocalDateTime.now().plusHours(1));
    }

    @Test
    @DisplayName("Test Correct Construction of Table")
    public void testCorrectConstructionOfTable(){
        assertEquals(1, table.getTableNumber());
        assertEquals(0, table.getReservations().size());
        assertEquals(4, table.getSeatsNumber());
    }

    @Test
    @DisplayName("Test Adding Single Reservation")
    public void testAddingSingleReservation(){
        table.addReservation(reservation1);
        assertEquals(1, table.getReservations().size());
        assertEquals(reservation1, table.getReservations().getFirst());
    }

    @Test
    @DisplayName("Test Add Multiple Reservations")
    public void testAddMultipleReservations(){
        table.addReservation(reservation1);
        table.addReservation(reservation2);
        assertEquals(2, table.getReservations().size());
        assertEquals(reservation1, table.getReservations().getFirst());
        assertEquals(reservation2, table.getReservations().get(1));
    }

    @Test
    @DisplayName("Test Reserved on Existing Reservation")
    public void testReservedOnExistingReservation(){
        table.addReservation(reservation1);
        assertTrue(table.isReserved(reservation1.getDateTime()));
    }

    @Test
    @DisplayName("Test Reserved on Not Existing Reservation")
    public void testReservedOnNotExistingReservation(){
        table.addReservation(reservation1);
        assertFalse(table.isReserved(LocalDateTime.now().plusDays(10)));
    }

    @Test
    @DisplayName("Test Not Reserved on Cancelled Reservation")
    public void testNotReservedOnCancelledReservation(){
        table.addReservation(reservation1);
        reservation1.cancel();
        assertFalse(table.isReserved(reservation1.getDateTime()));
    }
}
