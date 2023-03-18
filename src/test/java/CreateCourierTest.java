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
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateCourierTest {

    @Before
    public void connect(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @DisplayName("Checking creation a courier")
    @Test
    public void createCourierTest(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");

        // Создаем курьера
       given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier");

       // Проверяем наличие курьера по ID
        Id id = given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("ViktorL","12345"))
                .when().post("/api/v1/courier/login")
                .body().as(Id.class);

        MatcherAssert.assertThat(id.getId(), notNullValue());
    }

    @DisplayName("Checking impossibility of creating identical couriers")
    @Test // нельзя создать двух одинаковых курьеров
    public void checkImpossibilityCreatingTwoIdenticalCouriers(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        CreateCourier createCourier2 = new CreateCourier("ViktorL","1234567","Viktor");

        // Создаем пользователя с ником ViktorL
        given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier");

        // Создаем ещё одного пользователя с ником ViktorL но другим паролем
        given()
                .header("Content-type", "application/json")
                .and().body(createCourier2)
                .when().post("/api/v1/courier");

        // Проверяем что второй пользователь не создался
        given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("ViktorL","1234567"))
                .when().post("/api/v1/courier/login")
                .then().statusCode(404);
    }


    @DisplayName("Checking all required fields")
    @Test
    public void checkAllRequiredFields(){
        // Создание пользователей без обязательных полей
        CreateCourier createCourierWithoutLogin = new CreateCourier("","12345","");
        CreateCourier createCourierWithoutPassword = new CreateCourier("ViktorL","","");
        CreateCourier createCourierWithoutLoginPassword = new CreateCourier("","","");

        // Отправляем запросы на создание пользвателей
        given()
                .header("Content-type", "application/json")
                .and().body(createCourierWithoutLogin)
                .when().post("/api/v1/courier");
        given()
                .header("Content-type", "application/json")
                .and().body(createCourierWithoutPassword)
                .when().post("/api/v1/courier");
        given()
                .header("Content-type", "application/json")
                .and().body(createCourierWithoutLoginPassword)
                .when().post("/api/v1/courier");

        // Проверяем что пользвателей нет
        given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("","12345"))
                .when().post("/api/v1/courier/login")
                .then().statusCode(400);
        given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("ViktorL",""))
                .when().post("/api/v1/courier/login")
                .then().statusCode(400);
        given()
                .header("Content-type", "application/json")
                .and().body(new LogInCourier("",""))
                .when().post("/api/v1/courier/login")
                .then().statusCode(400);
    }

    @DisplayName("Checking for a response code")
    @Test
    public void check201ResponseCode(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
            given()
                        .header("Content-type", "application/json")
                        .and().body(createCourier)
                        .when().post("/api/v1/courier").then().statusCode(201);
    }

    @DisplayName("Checking response body")
    @Test
    public void checkBodyContainTrue(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier")
                .then().assertThat().body("ok",equalTo(true));
    }

    @DisplayName("Checking error body if one of fields is missing")
    @Test
    public void checkErrorWhenOneFieldIsNull(){
        String errorMessage = "Недостаточно данных для создания учетной записи";
        CreateCourier createCourierWithOutLogin = new CreateCourier("ViktorL",null,"Viktor");
        CreateCourier createCourierWithOutPassword = new CreateCourier("null","12345","Viktor");

        Response responseForLogin =
                given()
                        .header("Content-type", "application/json")
                        .and().body(createCourierWithOutLogin)
                        .when().post("/api/v1/courier");

        responseForLogin.then().assertThat().body("message",equalTo(errorMessage));

        Response responseForPassword =
                given()
                        .header("Content-type", "application/json")
                        .and().body(createCourierWithOutPassword)
                        .when().post("/api/v1/courier");

        responseForPassword.then().assertThat().body("message",equalTo(errorMessage));


    }

    @DisplayName("Checking an error if login is busy")
    @Test
    public void checkErrorWhenCreateSameUser(){
        String errorMessage = "Этот логин уже используется. Попробуйте другой.";
        // Создать одного пользователя
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        given()
                .header("Content-type", "application/json")
                .and().body(createCourier)
                .when().post("/api/v1/courier");

        // Создать второго с таким же логином
        Response response =
                 given()
                        .header("Content-type", "application/json")
                        .and().body(new CreateCourier("ViktorL","12345","Viktor"))
                        .when().post("/api/v1/courier");

        // Получить ошибку
        response.then().assertThat().body("message",equalTo(errorMessage));
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
