import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

@Epic("Creating new user role")
@Feature("Registration of user")
public class UserCreateTest {

    private UserClient userClient;
    private String accessToken;
    private User user;

    @Before
    public void setup() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deletingUser(accessToken, user);
        }
    }

    @Test
    @DisplayName("Creating user")
    @Description("Creating new user with valid credentials")
    public void creatingUserTest() {
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);

        boolean isCreated = response.extract().path("success");
        int statusCode = response.extract().statusCode();
        assertTrue("User not be created", isCreated);
        assertEquals("Incorrect status code", 200, statusCode);
    }

    @Test
    @DisplayName("Delete user")
    @Description("Deleting user")
    public void deletingUserTest() {
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);

        accessToken = response.extract().path("accessToken").toString().substring(7);
        ValidatableResponse deleteResponse = userClient.deletingUser(accessToken, user);

        int statusCode = deleteResponse.extract().statusCode();
        boolean isDeleted = deleteResponse.extract().path("success");
        String message = deleteResponse.extract().path("message");
        String userEmail = response.extract().path("user.email");
        String userName = response.extract().path("user.name");

        assertEquals("Status code is not 202",202, statusCode);
        assertTrue("User wasn't deleted",isDeleted);
        assertEquals("Error message is not valid","User successfully removed", message);
        assertEquals("User email is not valid",user.getEmail().toLowerCase(), userEmail);
        assertEquals("User name is not valid",user.getName(), userName);
    }

    @Test
    @DisplayName("Creating anoughter user")
    @Description("Can't create user twice")
    public void secondUserTest() {
        user = User.getRandom();
        userClient.userCreate(user);

        ValidatableResponse secondUser = userClient.userCreate(user);

        boolean isNotCreated = secondUser.extract().path("success");
        String message = secondUser.extract().path("message");
        int statusCode = secondUser.extract().statusCode();
        assertFalse("User created twice",isNotCreated);
        assertEquals("Error message is not valid","User already exists", message);
        assertEquals("Status code is not 403",403, statusCode);
    }

    @Test
    @DisplayName("Creating user with no email")
    @Description("Can't create user with no email")
    public void creatingWithoutEmailTest() {
        user = User.builder()
                .password(RandomStringUtils.randomAlphabetic(8))
                .name(RandomStringUtils.randomAlphabetic(8))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        assertFalse("User created", isNotCreated);
        assertEquals("Error message is not valid","Email, password and name are required fields", message);
        assertEquals("Status code is not 403",403, statusCode);
    }

    @Test
    @DisplayName("Creating user with no password")
    @Description("Can't create user with no password")
    public void creatingWithoutPasswordTest() {
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(10) + "@testdata.com")
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        assertFalse("User created", isNotCreated);
        assertEquals("Error message is not valid","Email, password and name are required fields", message);
        assertEquals("Status code is not 403",403, statusCode);
    }

    @Test
    @DisplayName("Creating user wint no name")
    @Description("Can't create user with no name")
    public void creatingWithoutNameTest() {
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(10) + "@testdata.com")
                .password(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        assertFalse("User created",isNotCreated);
        assertEquals("Error message is not valid","Email, password and name are required fields", message);
        assertEquals("Status code is not 403",403, statusCode);
    }
}