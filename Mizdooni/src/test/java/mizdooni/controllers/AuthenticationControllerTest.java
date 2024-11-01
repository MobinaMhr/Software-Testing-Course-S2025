package mizdooni.controllers;
import mizdooni.model.User;
import mizdooni.service.*;
import mizdooni.database.*;
import mizdooni.response.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

    @Mock
    private User user1;
    @Mock
    private User user2;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUser_LoggedIn() {//TODO: should implement getters for response ?

        when(userService.getCurrentUser()).thenReturn(user1);

        Response response = authenticationController.user();
        assertEquals("current user", response.getMessage());
        assertEquals(user1, response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }

    @Test
    void testUser_NotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.user());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    void testLogin_MissingParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testLogin_Successful() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "pass");
        ;
        when(userService.login("user", "pass")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user1);

        Response response = authenticationController.login(params);
        assertEquals("login successful", response.getMessage());
        assertEquals(user1, response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }


    @Test
    void testLogin_InvalidCredentials() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "wrongpass");

        when(userService.login("user", "wrongpass")).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
    void testLogout_Successful() {
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();
        assertEquals("logout successful", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }

    @Test
    void testLogout_NotLoggedIn() {
        when(userService.logout()).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.logout());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    void testValidateAvailableUsername() {
        String username = "newuser";

        when(userService.usernameExists(username)).thenReturn(false);

        Response response = authenticationController.validateUsername(username);
        assertEquals("username is available", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }

    @Test
    void testValidateExistsUsername() {
        String username = "existinguser";

        when(userService.usernameExists(username)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateUsername(username));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @Test
    void testValidateAvailableEmail() {
        String email = "new@example.com";

        when(userService.emailExists(email)).thenReturn(false);

        Response response = authenticationController.validateEmail(email);
        assertEquals("email not registered", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }

    @Test
    void testValidateExistingEmail() {
        String email = "existing@example.com";

        when(userService.emailExists(email)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateEmail(email));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }

    @Test
    void testSignupByMissingParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "user");

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupByBadParams(){
        Map<String, Object> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "pass");
        params.put("email", "user@example.com");
        params.put("role", "client");
        params.put("address", "kargar");

        ResponseException responseException = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ControllerUtils.PARAMS_BAD_TYPE, responseException.getMessage());

    }


    @Test
    void testSignupByEmptyParam(){
        Map<String, Object> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "");
        params.put("email", "user@example.com");
        params.put("role", "client");
        Map<String, String> addr = Map.of(
            "country" , "Iran",
                "city", "Tehran"
        );
        params.put("address", addr);

        ResponseException responseException = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, responseException.getMessage());

    }



}
