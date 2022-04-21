import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GettingListOfOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void getListOfOrders(){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get("/v1/orders?courierId");
        response.then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }
}
