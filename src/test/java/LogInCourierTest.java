import io.restassured.response.Response;
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
        Response createCourierRequest = CreateCourier.createCourierRequest(createCourier);
    }

    @DisplayName("Courier Authorization Check")
    @Test
    public void checkLogin(){
        LogInCourier logInCourier = new LogInCourier("ViktorL","12345");
        Response logInCourierRequest = LogInCourier.logInRequest(logInCourier);
        logInCourierRequest.then().statusCode(200);
    }

    @DisplayName("Validation of required fields")
    @Test
    public void checkAllRequiredFields(){
        LogInCourier logInCourierWithoutLogin = new LogInCourier(null,"12345");
        LogInCourier logInCourierWithOutPassword = new LogInCourier("ViktorL",null);

        Response logInCourierWithoutLoginRequest = LogInCourier.logInRequest(logInCourierWithoutLogin);
        logInCourierWithoutLoginRequest.then().statusCode(400);

        Response logInCourierWithOutPasswordRequest = LogInCourier.logInRequest(logInCourierWithOutPassword);
        logInCourierWithOutPasswordRequest.then().statusCode(400);
    }

    @DisplayName("Checking an error with an incorrect login-password")
    @Test
    public void checkErrorWhenWrongCreeds(){
        LogInCourier logInCourierWithWrongPassword = new LogInCourier("ViktorL","0000");

        Response logInCourierWithWrongPasswordRequest = LogInCourier.logInRequest
                (logInCourierWithWrongPassword);
        logInCourierWithWrongPasswordRequest.then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Error checking if there are no required fields")
    @Test
    public void checkErrorWhenMissRequiredFields(){
        LogInCourier logInCourierWithoutLogin = new LogInCourier("","12345");
        LogInCourier logInCourierWithOutPassword = new LogInCourier("ViktorL","");

        Response logInCourierWithoutLoginRequest = LogInCourier.logInRequest(logInCourierWithoutLogin);
        logInCourierWithoutLoginRequest.then().body("message", equalTo("Недостаточно данных для входа"));

        Response logInCourierWithOutPasswordRequest = LogInCourier.logInRequest(logInCourierWithOutPassword);
        logInCourierWithOutPasswordRequest.then().body("message", equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Checking for an error when authorizing a non-existent user")
    @Test
    public void checkErrorWhenLoginNonExistentData(){
        Response logInCourierRequest = LogInCourier.logInRequest
                (new LogInCourier("Boris","123456"));
        logInCourierRequest.then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Checking ID on a successful request")
    @Test
    public void checkBodyContainId(){
        LogInCourier logInCourier = new LogInCourier("ViktorL","12345");
        Response logInCourierRequest = LogInCourier.logInRequest(logInCourier);
        Id id  = logInCourierRequest.body().as(Id.class);

        MatcherAssert.assertThat(id.getId(),notNullValue());
    }

    @After
    public void deleteUser(){
        Response logInCourierWithoutLoginPasswordRequest = LogInCourier.logInRequest
                (new LogInCourier("ViktorL","12345"));

        Id id  = logInCourierWithoutLoginPasswordRequest.body().as(Id.class);

        given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + id.getId());
    }
}
