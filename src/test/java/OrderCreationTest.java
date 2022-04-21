import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.samokat.OrderCreationData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest {


    private final String blackColor;
    private final String greyColor;

    public OrderCreationTest(String blackColor, String greyColor){
        this.blackColor = blackColor;
        this.greyColor = greyColor;
    }

    @Parameterized.Parameters
    public static Object[][] getColorsData() {
        return new Object[][] {
                {"BLACK", ""},
                {"", "GREY"},
                {"BLACK", "GREY"},
                {"", ""},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void OrderCreation(){
        String firstName = RandomStringUtils.randomAlphabetic(10);
        String lastName = RandomStringUtils.randomAlphabetic(10);
        String[] colors = {blackColor, greyColor};
        OrderCreationData orderCreationData = new OrderCreationData(firstName, lastName, "Konoha, 143 apt.", 5, "8-800-355-35-35", 3, "2022-06-06", "Comment" ,colors);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(orderCreationData)
                .when()
                .post("/api/v1/orders");
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

}
