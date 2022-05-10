import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

@Epic("Creating new user")
@Feature("Changing user data")
public class ChangeUserDataTest {

    private User user;
    private UserClient userClient;
    private ValidatableResponse response;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        response = userClient.userCreate(user);
        userClient.validation(UserCredentials.from(user));
        accessToken = response.extract().path("accessToken").toString().substring(7);
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken, user);
    }

    //тест падает из-за бага - мэйл возвращется сервером в нижнем регистре
    @Test
    @DisplayName("Get user data")
    @Description("Getting user data (email, name)")
    public void getUserDataTest() {
        userClient.gettingInformationUser(accessToken);

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");
        String userEmail = response.extract().path("user.email");
        String userName = response.extract().path("user.name");

        assertEquals("Status code is not 200",200, statusCode);
        assertTrue("Information is not get", isSuccess);
        assertEquals("User email is not valid", user.getEmail(), userEmail);
        assertEquals("User name is not valid", user.getName(), userName);
    }

    @Test
    @DisplayName("Changing user data without auth")
    @Description("Can't change information about user with no authorization")
    public void changeInformationUserWithoutAuthorizationTest() {
        response = userClient.changeInformationUserWithoutToken(user);

        int statusCode = response.extract().statusCode();
        boolean isNotChanged = response.extract().path("success");
        String message = response.extract().path("message");

        assertEquals("Status code is not 401",401, statusCode);
        assertFalse("Information wasn't changed", isNotChanged);
        assertEquals("Error message is not valid","You should be authorised", message);
    }

    @Test
    @DisplayName("Changing user email")
    @Description("Can change information about user with authorization")
    public void changeEmailWithAuthorizationTest() {
        String newEmail = User.getRandom().getEmail();

        User newUser = User.builder()
                .email(newEmail)
                .password(user.getPassword())
                .name(user.getName())
                .build();

        userClient.changeInformationUserWithToken(accessToken, newUser);

        int statusCode = response.extract().statusCode();
        boolean isChanged= response.extract().path("success");
        String userEmail = response.extract().path("user.email");

        assertEquals("Status code is not 200", 200, statusCode);
        assertTrue("Information is not changed", isChanged);
        assertEquals("Email not changed", user.getEmail().toLowerCase(), userEmail);
    }

    @Test
    @DisplayName("Changing user password")
    @Description("Can change user password with authorization")
    public void changePasswordWithAuthorizationTest() {
        String newPassword = User.getRandom().getPassword();

        User newUser = User.builder()
                .email(user.getEmail())
                .password(newPassword)
                .name(user.getName())
                .build();

        userClient.changeInformationUserWithToken(accessToken, newUser);

        int statusCode = response.extract().statusCode();
        boolean isChanged= response.extract().path("success");
        String userName = response.extract().path("user.name");
        assertEquals("Status code is not 200", 200, statusCode);
        assertTrue("Information is not changed",isChanged);
        assertEquals("Name is not changed", user.getName(), userName);
    }

    @Test
    @DisplayName("Changing user name")
    @Description("Can change user name with authorization")
    public void changeNameWithAuthorizationTest() {
        String newName = User.getRandom().getName();

        User newUser = User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(newName)
                .build();

        userClient.changeInformationUserWithToken(accessToken, newUser);

        int statusCode = response.extract().statusCode();
        boolean isChanged= response.extract().path("success");
        String userName = response.extract().path("user.name");
        assertEquals("Status code is not 200", 200, statusCode);
        assertTrue("Information is not changed",isChanged);
        assertEquals(user.getName(), userName);
    }

    //здесь тест упадет из-за бага - система дает заменить мэйл на тот, который уже используется
    @Test
    @DisplayName("Changing user email by the same email")
    @Description("Change information about user with authorization and the same email")
    public void changeEmailByTheSameTest() {
        String exactlyUserEmail = user.getEmail();

        User newUser = User.builder()
                .email(exactlyUserEmail)
                .password(user.getPassword())
                .name(user.getName())
                .build();

        userClient.changeInformationUserWithToken(accessToken, newUser);

        int statusCode = response.extract().statusCode();
        boolean isNotChanged = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 403", 403, statusCode);
        assertFalse("Information is not changed",isNotChanged);
        assertEquals("Message is not valid", "User with such email already exists", message);
    }
}