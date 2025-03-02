import api.CourierApiClient; // Импортируем класс для работы с API
import config.Config;
import config.Endpoints;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import config.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

@Feature("Создание курьера")
public class CourierCreateTests {

    private int courierId;
    private final String login = "test_courier_" + System.currentTimeMillis();
    private final String password = "1234";

    @Before
    @Step("Установка базового URL")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
    }

    @After
    @Step("Удаление тестового курьера после тестов")
    public void tearDown() {
        if (courierId > 0) {
            CourierApiClient.deleteCourier(courierId) // Используем метод из CourierApiClient
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private void createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        CourierApiClient.createCourier(courier) // Используем метод из CourierApiClient
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Логин курьера {0}")
    private int loginCourier(String login, String password) {
        Courier courier = new Courier(login, password, null);

        return CourierApiClient.loginCourier(courier) // Используем метод из CourierApiClient
                .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    @Story("Успешное создание курьера")
    @Description("Проверка, что API позволяет создать курьера и он может войти в систему")
    @Severity(SeverityLevel.BLOCKER)
    public void createCourierSuccess() {
        step("Создание курьера");
        createCourier(login, password, "Test User");
        step("Логин курьера");
        courierId = loginCourier(login, password);
    }

    @Test
    @Story("Создание курьера без логина")
    @Description("Проверка, что API не позволяет создать курьера без логина")
    @Severity(SeverityLevel.CRITICAL)
    public void createCourierWithoutLogin() {
        step("Отправка запроса на создание курьера без логина");
        Courier courier = new Courier(null, "1234", "Test"); // Логин отсутствует

        CourierApiClient.createCourier(courier) // Используем метод из CourierApiClient
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Создание курьера с существующим логином")
    @Description("Проверка, что API не позволяет создать курьера с дублирующим логином")
    @Severity(SeverityLevel.CRITICAL)
    public void createCourierDuplicateLogin() {
        step("Создание первого курьера");
        createCourier(login, password, "Test User");
        courierId = loginCourier(login, password);

        step("Попытка создать второго курьера с таким же логином");
        Courier duplicateCourier = new Courier(login, password, "Another");

        CourierApiClient.createCourier(duplicateCourier) // Используем метод из CourierApiClient
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("{0}")
    private void step(String message) {
        // Логирование шагов в Allure
    }
}