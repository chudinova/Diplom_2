import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.*;

@Epic("Create new order")
@Feature("Create order")
public class OrderCreateTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private ValidatableResponse response;
    private String accessToken;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
        ValidatableResponse ingredients = orderClient.gettingAllIngredients();
        firstIngredient = ingredients.extract().path("data[0]._id");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken, user);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Authorized user create order with ingredients")
    public void creatingOrderWithAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        String name = response.extract().path("name");
        boolean isCreated = response.extract().path("success");
        assertEquals("Incorrect status code", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order doesn't created", isCreated);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Not authorized user create order with ingredients")
    public void creatingOrderWithoutAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));

        response = orderClient.orderCreateWithoutAuthorization(order);

        int statusCode = response.extract().statusCode();
        String name = response.extract().path("name");
        boolean isCreated = response.extract().path("success");
        assertEquals("Incorrect status code", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order doesn't created", isCreated);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with no ingredients")
    public void creatingOrderWithoutIngredientTest() {
        Order order = new Order();

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        boolean isNotCreated = response.extract().path("success");
        String errorMessage = response.extract().path("message");
        assertEquals("Status code in not 400", 400, statusCode);
        assertFalse("Order is not created", isNotCreated);
        assertEquals("Error message is not valid", "Ingredient ids must be provided", errorMessage);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with incorrect ingredients")
    public void creatingOrderWithIncorrectHashesTest() {
        String incorrectHashes = Order.getRandomIngridient();
        Order order = new Order();
        order.setIngredients(Collections.singletonList(incorrectHashes));

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code in not 500", 500, statusCode);
    }
}