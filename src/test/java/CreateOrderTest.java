import auxiliary.Track;
import io.restassured.response.Response;
import main.CreateOrder;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private String [] color;
    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object [][]getOrderColor() {
        return new Object[][] {
                {new String[] {"BLACK"}},
                {new String[] {"GREY"}},
                {new String[] {"BLACK","GREY"}},
                {null},
        };
    }

    @DisplayName("Content check track in response")
    @Test
    public void checkBodyContainTrack(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        CreateOrder createOrder = new CreateOrder("Anton","Ivanov",
                "Pushkin street","Pushkin","252585","30",
                    "2023-04-04","No comment", color);

        Response createOrderRequest = CreateOrder.createOrderRequest(createOrder);
        Track track = createOrderRequest.body().as(Track.class);

        MatcherAssert.assertThat(track.getTrack(), notNullValue());
    }
}

