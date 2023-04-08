package main;

import auxiliary.Orders;
import auxiliary.PageInfo;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class GetListOrder {

    // Orders

    private List <Orders> orders;

    private PageInfo pageInfo;

    // private List<AvailableStations> availableStations;

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static Response getListOrderRequest(){
        return given()
                .header("Content-type", "application/json")
                .when().get("/api/v1/orders");
    }
}
