import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.samokat.CourierId;
import ru.yandex.samokat.CourierLoginAndPassword;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MakeCourierTest {

    private Response response;
    private String courierLogin;
    private String courierPassword;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown(){
        if(courierLogin != "" && courierPassword != ""){
            CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(courierLogin, courierPassword);
            response = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(courierLoginAndPassword)
                    .when()
                    .post("/api/v1/courier/login");
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .when()
                    .delete("/api/v1/courier/" + response.body().as(CourierId.class).getId());
        }
    }

    @Test
    public void makingCourier() {
        courierLogin = RandomStringUtils.randomAlphabetic(10);
        courierPassword = RandomStringUtils.randomAlphabetic(10);
        String courierFirstName = RandomStringUtils.randomAlphabetic(10);

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    public void unableMakeTwoEqualCouriers() {
        courierLogin = RandomStringUtils.randomAlphabetic(10);
        courierPassword = RandomStringUtils.randomAlphabetic(10);
        String courierFirstName = RandomStringUtils.randomAlphabetic(10);

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");

        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");

        response.then().assertThat().body("message", equalTo("Этот логин уже используется"))
                .and()
                .statusCode(409);
    }

    @Test
    public void unabletoMakeCoriuerWithoutLogin(){
        courierLogin = "";
        courierPassword = RandomStringUtils.randomAlphabetic(10);
        String courierFirstName = RandomStringUtils.randomAlphabetic(10);

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400 );
    }

    @Test
    public void unabletoMakeCoriuerWithoutPassword(){
        courierLogin = RandomStringUtils.randomAlphabetic(10);
        courierPassword = "";
        String courierFirstName = RandomStringUtils.randomAlphabetic(10);

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400 );
    }
}
