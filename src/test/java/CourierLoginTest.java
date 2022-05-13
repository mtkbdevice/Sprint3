import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.ArrayList;
import ru.yandex.samokat.ScooterRegisterCourier;
import ru.yandex.samokat.CourierLoginAndPassword;
import ru.yandex.samokat.CourierId;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;




public class CourierLoginTest {

    private Response response;
    private String login;
    private String password;
    ArrayList<String>loginAndPassword;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown(){
        if(login != loginAndPassword.get(0) && password != loginAndPassword.get(1)){
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .when()
                    .delete("/api/v1/courier/" + response.body().as(CourierId.class).getId());
        }
        else{
            String login = loginAndPassword.get(0);
            String password = loginAndPassword.get(1);
            CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
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
    public void courierLogin(){
        ScooterRegisterCourier courier = new ScooterRegisterCourier();
        loginAndPassword =  courier.registerNewCourierAndReturnLoginPassword();
        login = loginAndPassword.get(0);
        password = loginAndPassword.get(1);
        CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLoginAndPassword)
                .when()
                .post("/api/v1/courier/login");

        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    public void unableLoginWithoutLogin(){
        ScooterRegisterCourier courier = new ScooterRegisterCourier();
        loginAndPassword =  courier.registerNewCourierAndReturnLoginPassword();
        login = "";
        password = loginAndPassword.get(1);
        CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLoginAndPassword)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    public void unableLoginWithoutPassword(){
        ScooterRegisterCourier courier = new ScooterRegisterCourier();
        loginAndPassword =  courier.registerNewCourierAndReturnLoginPassword();
        login = loginAndPassword.get(0);
        password = "";
        CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLoginAndPassword)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    public void unableLoginWrongLogin(){
        String wrongRandomString = RandomStringUtils.randomAlphabetic(5);
        ScooterRegisterCourier courier = new ScooterRegisterCourier();
        loginAndPassword =  courier.registerNewCourierAndReturnLoginPassword();
        login = wrongRandomString;
        password = loginAndPassword.get(1);
        CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLoginAndPassword)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    public void unableLoginWrongPassword(){
        String wrongRandomString = RandomStringUtils.randomAlphabetic(5);
        ScooterRegisterCourier courier = new ScooterRegisterCourier();
        loginAndPassword =  courier.registerNewCourierAndReturnLoginPassword();
        login = loginAndPassword.get(0);
        password = wrongRandomString;
        CourierLoginAndPassword courierLoginAndPassword = new CourierLoginAndPassword(login, password);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLoginAndPassword)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }
}
