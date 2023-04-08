package main;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LogInCourier {

    private String login;
    private String password;

    public LogInCourier() {
    }

    public LogInCourier(String login, String password) {
        this.login = login;
        this.password = password;
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

    public static Response logInRequest (LogInCourier logInCourier){
        return given()
                .header("Content-type", "application/json")
                .and().body(logInCourier)
                .when().post("/api/v1/courier/login");
    }
}
