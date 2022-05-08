import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient extends StellarBurgerRestClient {

    private static final String ORDER_PATH = "/api/orders";

    @Step("Creating order with authorization")
    public ValidatableResponse orderCreateWithAuthorization(String accessToken, Order order) {
        return given()
                .auth().oauth2(accessToken)
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Creating order without authorization")
    public ValidatableResponse orderCreateWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting orders one user with authorization")
    public ValidatableResponse gettingOrderUserWithAuthorization(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting orders one user without authorization")
    public ValidatableResponse gettingOrderUserWithoutAuthorization() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting all orders")
    public ValidatableResponse gettingAllOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH + "/all")
                .then().log().all();
    }

    @Step("Getting all ingredients")
    public ValidatableResponse gettingAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get("/api/ingredients")
                .then().log().all();
    }
}