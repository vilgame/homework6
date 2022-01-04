package org.example.api.store;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.store.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class StoreApiTest {

    public Order getOrder() {
        Order order = new Order();
        int orderId = Integer.parseInt(System.getProperty("orderId"));
        int petId = Integer.parseInt(System.getProperty("petId"));
        order.setId(orderId);
        order.setPetId(petId);
        return order;
    }

    @BeforeClass
    public void prepare() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void checkObjectSave() throws InterruptedException {
        given()
                .body(getOrder())
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);
        Thread.sleep(10000);
        Order actual =
                given()
                        .pathParam("orderId", getOrder().getId())
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);
        Assert.assertEquals(actual.getId(), getOrder().getId());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .pathParam("orderId", getOrder().getId())
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        Thread.sleep(10000);
        given()
                .pathParam("orderId", getOrder().getId())
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }

}
