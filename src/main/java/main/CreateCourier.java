package main;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateCourier {

    private String login;
    private String password;
    private String firstName;

    public CreateCourier() {
    }

    public CreateCourier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public static Response createCourierRequest(CreateCourier createCourier){
        return given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier");
    }
}
