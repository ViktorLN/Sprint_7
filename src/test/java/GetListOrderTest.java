import io.restassured.response.Response;
import main.GetListOrder;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import auxiliary.Orders;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;

public class GetListOrderTest {

    @DisplayName("Check getting list of orders")
    @Test
    public void checkBodyContainListOfOrder(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        Response getListOrderRequest = GetListOrder.getListOrderRequest();
        GetListOrder getListOrder = getListOrderRequest.body().as(GetListOrder.class);
        List<Orders> orders = new ArrayList<>(getListOrder.getOrders());

        MatcherAssert.assertThat(orders,notNullValue());
    }
}

