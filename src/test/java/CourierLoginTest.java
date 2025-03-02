import config.Config;
import config.Endpoints; // Импортируем класс с константами
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import config.Courier; // Импортируем класс Courier
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Создание курьера")
public class CourierLoginTest {

    private int courierId;
    private final String login = "test_courier_" + System.currentTimeMillis();
    private final String password = "1234";

    @Before
    @Step("Создание тестового курьера перед тестами")
    public void setUp() {
        // Убрана установка baseURI, так как теперь она задается в Config
    }

    @After
    @Step("Удаление тестового курьера после тестов")
    public void tearDown() {
        if (courierId > 0) {
            given()
                    .delete(Endpoints.COURIER_DELETE + courierId) // Используем константу
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private void createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName); // Используем объект

        given()
                .contentType(ContentType.JSON)
                .body(courier) // Передаем объект
                .when()
                .post(Endpoints.COURIER_CREATE) // Используем константу
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Логин курьера {0}")
    private int loginCourier(String login, String password) {
        Courier courier = new Courier(login, password, null); // Используем объект

        return given()
                .contentType(ContentType.JSON)
                .body(courier) // Передаем объект
                .when()
                .post(Endpoints.COURIER_LOGIN) // Используем константу
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
        given()
                .contentType(ContentType.JSON)
                .body("{\"password\": \"1234\", \"firstName\": \"Test\"}")
                .when()
                .post(Endpoints.COURIER_CREATE) // Используем константу
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
        given()
                .contentType(ContentType.JSON)
                .body(new Courier(login, password, "Another")) // Используем объект
                .when()
                .post(Endpoints.COURIER_CREATE) // Используем константу
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("{0}")
    private void step(String message) {
        // Логирование шагов в Allure
    }
}
