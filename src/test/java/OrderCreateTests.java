import config.Config;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.restassured.http.ContentType.JSON;

@RunWith(Parameterized.class)
@Epic("Orders API")
@Feature("Create Order")
public class OrderCreateTests {
    private final List<String> color;

    public OrderCreateTests(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Тест с цветом: {0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
    }

    @Test
    @Story("Создание заказа с разными параметрами цвета")
    @Description("Проверяет создание заказа с параметрами цвета: чёрный, серый, оба цвета или без цвета.")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderTest() {
        Map<String, Object> order = buildOrder(color);
            Response response = RestAssured.given()
                    .contentType(JSON)
                    .body(order)
                    .when()
                    .post("/api/v1/orders");

            response.then().statusCode(201);

            int track = response.jsonPath().getInt("track");
            Assert.assertTrue("Track должен присутствовать в ответе", track > 0);

            Allure.addAttachment("Response", "application/json", response.getBody().asPrettyString());

    }

    @Step("Формирование тела заказа с цветами: {0}")
    private Map<String, Object> buildOrder(List<String> color) {
        return Map.of(
                "firstName", "Naruto",
                "lastName", "Uchiha",
                "address", "Konoha, 142 apt.",
                "metroStation", 4,
                "phone", "+7 800 355 35 35",
                "rentTime", 5,
                "deliveryDate", "2020-06-06",
                "comment", "Saske, come back to Konoha",
                "color", color
        );
    }
}