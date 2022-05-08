import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends StellarBurgerRestClient {

    private static final String USER_PATH = "/api/auth/";

    @Step("Create new user")
    public ValidatableResponse userCreate(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "register")
                .then().log().all();
    }

    @Step("Authorization")
    public ValidatableResponse validation(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH + "login")
                .then().log().all();
    }

    @Step("Logout")
    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .when()
                .post(USER_PATH + "logout")
                .then().log().all();
    }

    @Step("Getting info about user")
    public ValidatableResponse gettingInformationUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .get(USER_PATH + "user")
                .then().log().all();
    }

    @Step("Refreshing information user with token")
    public ValidatableResponse changeInformationUserWithToken(String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_PATH + "user")
                .then().log().all();
    }

    @Step("Refreshing information user without token")
    public ValidatableResponse changeInformationUserWithoutToken(User user) {
        return given()
                .spec(getBaseSpec())
                .when()
                .body(user)
                .patch(USER_PATH + "user")
                .then().log().all();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .auth().oauth2(accessToken)
                .when()
                .delete(USER_PATH + "user")
                .then().log().all();
    }
}