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

       Response createCourierRequest = CreateCourier.createCourierRequest(createCourier);
       Response logInCourierRequest = LogInCourier.logInRequest
               (new LogInCourier("ViktorL","12345"));
        Id id = logInCourierRequest.body().as(Id.class);
       // Проверяем наличие курьера по ID
        MatcherAssert.assertThat(id.getId(), notNullValue());
    }

    @DisplayName("Checking impossibility of creating identical couriers")
    @Test // нельзя создать двух одинаковых курьеров
    public void checkImpossibilityCreatingTwoIdenticalCouriers(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        CreateCourier createCourier2 = new CreateCourier("ViktorL","1234567","Viktor");

        // Создаем пользователя с ником ViktorL
        Response createCourierRequest = CreateCourier.createCourierRequest(createCourier);

        // Создаем ещё одного пользователя с ником ViktorL но другим паролем
        Response createSecondCourierRequest = CreateCourier.createCourierRequest(createCourier2);

        // Проверяем что второй пользователь не создался
        Response logInCourierRequest = LogInCourier.logInRequest
                (new LogInCourier("ViktorL","1234567"));
        logInCourierRequest.then().statusCode(404);
    }

    @DisplayName("Checking all required fields")
    @Test
    public void checkAllRequiredFields(){
        // Создание пользователей без обязательных полей
        CreateCourier createCourierWithoutLogin = new CreateCourier("","12345","");
        CreateCourier createCourierWithoutPassword = new CreateCourier("ViktorL","","");
        CreateCourier createCourierWithoutLoginPassword = new CreateCourier("","","");

        // Отправляем запросы на создание пользвателей
        Response createCourierRequest = CreateCourier.createCourierRequest(createCourierWithoutLogin);
        Response createSecondCourierRequest = CreateCourier.createCourierRequest(createCourierWithoutPassword);
        Response createThirdCourierRequest = CreateCourier.createCourierRequest(createCourierWithoutLoginPassword);

        // Проверяем что пользвателей нет
        Response logInCourierWithoutLoginRequest = LogInCourier.logInRequest
                (new LogInCourier("","12345"));
        logInCourierWithoutLoginRequest.then().statusCode(400);

        Response logInCourierWithoutPasswordRequest = LogInCourier.logInRequest
                (new LogInCourier("ViktorL",""));
        logInCourierWithoutPasswordRequest.then().statusCode(400);

        Response logInCourierWithoutLoginPasswordRequest = LogInCourier.logInRequest
                (new LogInCourier("",""));
        logInCourierWithoutLoginPasswordRequest.then().statusCode(400);
    }

    @DisplayName("Checking for a response code")
    @Test
    public void check201ResponseCode(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        Response createCourierRequest = CreateCourier.createCourierRequest(createCourier);
        createCourierRequest.then().statusCode(201);
    }

    @DisplayName("Checking response body")
    @Test
    public void checkBodyContainTrue(){
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        Response createCourierRequest = CreateCourier.createCourierRequest(createCourier);
        createCourierRequest.then().assertThat().body("ok",equalTo(true));
    }

    @DisplayName("Checking error body if one of fields is missing")
    @Test
    public void checkErrorWhenOneFieldIsNull(){
        String errorMessage = "Недостаточно данных для создания учетной записи";
        CreateCourier createCourierWithOutLogin = new CreateCourier("ViktorL",null,"Viktor");
        CreateCourier createCourierWithOutPassword = new CreateCourier(null,"12345","Viktor");

        Response createCourierRequest = CreateCourier.createCourierRequest(createCourierWithOutLogin);
        createCourierRequest.then().assertThat().body("message",equalTo(errorMessage));

        Response createSecondCourierRequest = CreateCourier.createCourierRequest(createCourierWithOutLogin);
        createSecondCourierRequest.then().assertThat().body("message",equalTo(errorMessage));
    }

    @DisplayName("Checking an error if login is busy")
    @Test
    public void checkErrorWhenCreateSameUser(){
        String errorMessage = "Этот логин уже используется. Попробуйте другой.";
        // Создать одного пользователя
        CreateCourier createCourier = new CreateCourier("ViktorL","12345","Viktor");
        Response createFirstCourierRequest = CreateCourier.createCourierRequest(createCourier);
        // Создать второго с таким же логином
        Response createSameFirstCourierRequest = CreateCourier.createCourierRequest(createCourier);
        // Получить ошибку
        createSameFirstCourierRequest.then().assertThat().body("message",equalTo(errorMessage));
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


