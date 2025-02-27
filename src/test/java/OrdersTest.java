import config.Config;
import io.qameta.allure.*;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Заказы")
public class OrdersTest {

    private static final String ORDERS_ENDPOINT = "/api/v1/orders";

    @Test
    @Story("Получение списка заказов")
    @Description("Проверка, что API возвращает список заказов")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetOrdersList() {
        given()
                .baseUri(Config.BASE_URL)
                .when()
                .get(ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("orders", not(empty()));
    }


    @Test
    @Story("Фильтрация заказов по метро")
    @Description("Проверка, что API фильтрует заказы по метро")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrdersByMetroStation() {
        given()
                .baseUri(Config.BASE_URL)
                .queryParam("nearestStation", "[\"1\", \"2\"]")
                .when()
                .get(ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("orders", not(empty()));
    }

    @Test
    @Story("Пагинация заказов")
    @Description("Проверка, что API корректно работает с limit и page")
    @Severity(SeverityLevel.MINOR)
    public void testGetOrdersWithPagination() {
        given()
                .baseUri(Config.BASE_URL)
                .queryParam("limit", 10)
                .queryParam("page", 0)
                .when()
                .get(ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("orders.size()", lessThanOrEqualTo(10));
    }

    @Test
    @Story("Получение заказов с несуществующим курьером")
    @Description("Проверка, что API возвращает 404 для несуществующего курьера")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetOrdersWithInvalidCourierId() {
        given()
                .baseUri(Config.BASE_URL)
                .queryParam("courierId", 9999)
                .when()
                .get(ORDERS_ENDPOINT)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором 9999 не найден"));
    }

    @Step("{0}")
    private void step(String message) {
    }
}