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

import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class StoreApiTest {

    private Order order;

    public void setOrder() {
        this.order = getOrder();
    }

    public Order getOrder() {
        Order order = new Order();
        int orderId = new Random().nextInt(10) + 1;
        int petId = new Random().nextInt(10) + 1;
        order.setId(orderId);
        order.setPetId(petId);
        return order;
    }

    @BeforeClass
    public void prepare() {
        setOrder();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void checkObjectSave() {
        given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);
        Order actual =
                given()
                        .pathParam("orderId", order.getId())
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);
        Assert.assertEquals(actual.getId(), order.getId());
    }

    @Test
    public void testDelete() {
        given()
                .pathParam("orderId", order.getId())
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        given()
                .pathParam("orderId", order.getId())
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testStoreInventory() {
        Map inventory = given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Map.class);
        Assert.assertTrue(inventory.containsKey("sold"), "Inventory не содержит статус sold" );
    }
}