package api;

import config.Endpoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import config.Courier;

import static io.restassured.RestAssured.given;

public class CourierApiClient {

    public static Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(Endpoints.COURIER_CREATE);
    }

    public static Response loginCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(Endpoints.COURIER_LOGIN);
    }

    public static Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete(Endpoints.COURIER_DELETE + courierId);
    }
}