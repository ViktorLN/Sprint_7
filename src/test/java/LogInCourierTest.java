import main.CreateCourier;
import main.LogInCourier;
import io.qameta.allure.junit4.DisplayName;
import auxiliary.Id;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.RestAssured;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;

public class LogInCourierTest {

    @Before
    public void connect(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        // Создаем курьера
        given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier");

    }

    @DisplayName("Courier Authorization Check")
    @Test
    public void checkLogin(){
        LogInCourier logInCourier = new LogInCourier("ViktorL","12345");
        given()
                .header("Content-type", "application/json")
                .and().body(logInCourier)
                .when().post("/api/v1/courier/login")
                .then().statusCode(200);
    }

    @DisplayName("Validation of required fields")
    @Test
    public void checkAllRequiredFields(){
        LogInCourier logInCourierWithoutLogin = new LogInCourier(null,"12345");
        LogInCourier logInCourierWithOutPassword = new LogInCourier("ViktorL",null);

        given()
                .header("Content-type", "application/json")
                .and().body(logInCourierWithoutLogin)
                .when().post("/api/v1/courier/login")
                .then().statusCode(400);

        given()
                .header("Content-type", "application/json")
                .and().body(logInCourierWithOutPassword)
                .when().post("/api/v1/courier/login")
                .then().statusCode(400);
    }

    @DisplayName("Checking an error with an incorrect login-password")
    @Test
    public void checkErrorWhenWrongCreeds(){
        LogInCourier logInCourierWithWrongPassword = new LogInCourier("ViktorL","0000");

        given()
                .header("Content-type", "application/json")
                .and().body(logInCourierWithWrongPassword)
                .when().post("/api/v1/courier/login")
                .then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Error checking if there are no required fields")
    @Test
    public void checkErrorWhenMissRequiredFields(){
        LogInCourier logInCourierWithoutLogin = new LogInCourier("","12345");
        LogInCourier logInCourierWithOutPassword = new LogInCourier("ViktorL","");

        given()
                .header("Content-type", "application/json")
                .and().body(logInCourierWithoutLogin)
                .when().post("/api/v1/courier/login")
                .then().body("message", equalTo("Недостаточно данных для входа"));

        given()
                .header("Content-type", "application/json")
                .and().body(logInCourierWithOutPassword)
                .when().post("/api/v1/courier/login")
                .then().body("message", equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Checking for an error when authorizing a non-existent user")
    @Test
    public void checkErrorWhenLoginNonExistentData(){
        given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("Boris","123456"))
                .when().post("/api/v1/courier/login")
                .then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Checking ID on a successful request")
    @Test
    public void checkBodyContainId(){
        LogInCourier logInCourier = new LogInCourier("ViktorL","12345");

        Id id  =
                given()
                        .header("Content-type", "application/json")
                        .body(logInCourier)
                        .post("/api/v1/courier/login")
                        .body().as(Id.class);

        MatcherAssert.assertThat(id.getId(),notNullValue());
    }

    @After
    public void deleteUser(){
        LogInCourier logInCourier = new LogInCourier("ViktorL","12345");

        Id id  =
                given()
                        .header("Content-type", "application/json")
                        .body(logInCourier)
                        .post("/api/v1/courier/login")
                        .body().as(Id.class);

        given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + id.getId());
    }
}
