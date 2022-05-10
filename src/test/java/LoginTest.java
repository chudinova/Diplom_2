import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

@Feature("Login")
public class LoginTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken, user);
    }

    @Test
    @DisplayName("Login user validation test")
    @Description("Can login with valid email and password")
    public void validationTest() {
        ValidatableResponse response = userClient.validation(UserCredentials.from(user));

        int statusCode = response.extract().statusCode();
        boolean isValidated = response.extract().path("success");
        assertEquals("Status code is not 200",200, statusCode);
        assertTrue("User didn't create", isValidated);
    }

    @Test
    @DisplayName("Login with not valid email")
    @Description("Can't login with incorrect email")
    public void validationWithWrongEmailTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(RandomStringUtils.randomAlphabetic(8) + "@mail.ru")
                .password(user.getPassword())
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 401",401, statusCode);
        assertFalse("User wasn't validated",isNotValidated);
        assertEquals("Error message is not valid","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Login with not valid password")
    @Description("Can't login with incorrect password")
    public void validationWithWrongPasswordTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(RandomStringUtils.randomAlphabetic(8))
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 401",401, statusCode);
        assertFalse("User wasn't validated",isNotValidated);
        assertEquals("Error message is not valid","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Login without email")
    @Description("Can't login without email")
    public void validationWithoutEmailTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(null)
                .password(user.getPassword())
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 401",401, statusCode);
        assertFalse("User wasn't validated",isNotValidated);
        assertEquals("Error message is not valid","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Login without password")
    @Description("Can't login without password")
    public void validationWithoutPasswordTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(null)
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 401",401, statusCode);
        assertFalse("User wasn't validated",isNotValidated);
        assertEquals("Error message is not valid","email or password are incorrect", message);
    }
}