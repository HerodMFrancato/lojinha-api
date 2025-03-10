package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import org.junit.Before;
import org.junit.Test;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import pojo.UsuarioPojo;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API Rest do módulo de Produto")
public class ProdutoTest {
    private String token;

    @Before
    public void before() {
        // Configurando os dados da API Rest da lojinha
        baseURI = "http://165.227.93.41";
        basePath = "/lojinha";

        // Obter o token do usúario admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(UsuarioDataFactory.criarusuarioAdministrador())
                .when()
                .post("/v2/login")
                .then()
                .extract()
                .path("data.token");
    }
    @Test
    @DisplayName("Validar que o valor do produto igual a 0.00 nao e permitido")
    public void testValidarLimitesZeradoProibidosValorProduto() {

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorA(0.00))
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00" ))
                .statusCode(422);
    }
    @Test
    @DisplayName("Validar que o valor do produto igual a 7000.01 nao e permitido")
    public void testValidarLimitesSeteMilProibidosValorProduto() {
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorA(7.000,01))
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00" ))
                .statusCode(422);
    }
}
