package br.com.wmscode.experience.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class PixAgendamentoControllerTest {

    @Test
    void listarAgendamentos_DeveRetornar200() {
        given()
        .when()
            .get("/api/v1/pix/agendamentos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornar404() {
        given()
        .when()
            .get("/api/v1/pix/agendamentos/999")
        .then()
            .statusCode(404);
    }
} 