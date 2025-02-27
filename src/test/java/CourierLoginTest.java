import config.Config;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Авторизация курьера")
public class CourierLoginTest {

    private final String login = "Maks";
    private final String password = "1234";
    private Integer courierId;

    @Before
    @Step("Создание тестового курьера перед тестами")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;

        String createCourierBody = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"Test\"}", login, password);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(createCourierBody)
                .when()
                .post("/api/v1/courier");

        response.then().statusCode(anyOf(is(201), is(409)));
    }

    @After
    @Step("Удаление тестового курьера после тестов")
    public void tearDown() {
        if (courierId != null) {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"id\": " + courierId + "}")
                    .when()
                    .delete("/api/v1/courier")
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
    }

    @Test
    @Story("Логин с неверным паролем")
    @Description("Проверка, что API возвращает 404 при попытке входа с неверным паролем")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithIncorrectPassword() {
        step("Отправка запроса на логин с неправильным паролем");
        String requestBody = "{\"login\": \"ninja\", \"password\": \"wrong_password\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");

        response.prettyPrint();

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Story("Логин без пароля")
    @Description("Проверка, что API возвращает 400 при попытке входа без пароля")
    @Severity(SeverityLevel.NORMAL)
    public void loginWithoutPassword() {
        step("Отправка запроса на логин без пароля");
        String requestBody = "{\"login\": \"ninja\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Story("Логин без логина")
    @Description("Проверка, что API возвращает 400 при попытке входа без логина")
    @Severity(SeverityLevel.NORMAL)
    public void loginWithoutLogin() {
        step("Отправка запроса на логин без логина");
        String requestBody = "{\"password\": \"1234\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Story("Логин несуществующего пользователя")
    @Description("Проверка, что API возвращает 404 при попытке входа несуществующего пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithNonExistentUser() {
        step("Отправка запроса на логин несуществующего пользователя");
        String requestBody = "{\"login\": \"non_existent_user\", \"password\": \"1234\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Story("Успешный логин")
    @Description("Проверка, что API возвращает id при успешном логине")
    @Severity(SeverityLevel.BLOCKER)
    public void successfulLoginReturnsId() {
        step("Отправка запроса на успешный логин");
        String loginBody = String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when()
                .post("/api/v1/courier/login");

        response.prettyPrint();

        courierId = response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Step("{0}")
    private void step(String message) {
    }
}