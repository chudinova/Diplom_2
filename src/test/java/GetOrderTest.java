import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

@Feature("Get orders")
public class GetOrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private ValidatableResponse response;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString();
        ValidatableResponse ingredients = orderClient.gettingAllIngredients();
        firstIngredient = ingredients.extract().path("data[1]._id");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken, user);
    }

    @Test
    @DisplayName("Getting all orders")
    @Description("Getting all orders for authorized user")
    public void getOrderOneUserWithAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));
        orderClient.orderCreateWithAuthorization(accessToken, order);

        response = orderClient.gettingOrderUserWithAuthorization(accessToken);

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");
        List<String> orders = response.extract().path("orders");
        assertEquals("Status code is not 200", 200, statusCode);
        assertTrue("Orders are not get", isSuccess);
        assertNotNull("Orders empty", orders);
    }

    @Test
    @DisplayName("Getting orders for one user")
    @Description("Getting all orders for not authorized user")
    public void getOrderOneUserWithoutAuthorization() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));
        orderClient.orderCreateWithAuthorization(accessToken, order);

        response = orderClient.gettingOrderUserWithoutAuthorization();

        int statusCode = response.extract().statusCode();
        boolean isNotGeted = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Status code is not 401", 401, statusCode);
        assertFalse("Orders are not get", isNotGeted);
        assertEquals("Error message is not valid", "You should be authorised", message);
    }
}