package modulos.produto;

import org.junit.Before;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import pojo.ComponentePojo;
import pojo.ProdutoPojo;
import pojo.UsuarioPojo;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.ResponseAwareMatcher.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API Rest do módulo de Produto")
public class ProdutoTest {
    private String token;

    @Before
    public void before() {
        // Configurando os dados da API Rest da lojinha
        baseURI = "http://165.227.93.41";
        basePath = "/lojinha";

        UsuarioPojo usuario = new UsuarioPojo();
        usuario.setUsuarioLogin("admin");
        usuario.setUsuarioSenha("admin");

        // Obter o token do usúario admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(usuario)
                .when()
                .post("/v2/login")
                .then()
                .extract()
                .path("data.token");
    }
    @Test
    @DisplayName("Validar que o valor do produto igual a 0.00 nao e permitido")
    public void testValidarLimitesZeradoProibidosValorProduto() {

        ProdutoPojo produto = new ProdutoPojo();
        produto.setProdutoNome("Play Station 5");
        produto.setProdutoValor(0.00);
        List<String> cores = new ArrayList<>();
        cores.add("Preto");
        cores.add("Branco");
        produto.setProdutoCores(cores);
        produto.setProdutoUrlMock("");
        List<ComponentePojo> componentes = new ArrayList<>();
        ComponentePojo componente = new ComponentePojo();
        componente.setComponenteNome("Controle");
        componente.setComponenteQuantidade(1);
        componentes.add(componente);
        produto.setComponentes(componentes);

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(produto)
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
                .body("{\n" +
                        " \"produtoNome\": \"Play Station 5\",\n" +
                        " \"produtoValor\": 7000.01,\n" +
                        " \"produtoCores\": [\n" +
                        " \"preto\"\n" +
                        " ],\n" +
                        " \"produtoUrlMock\": \"\",\n" +
                        " \"componentes\": [\n" +
                        " {\n" +
                        " \"componenteNome\": \"Controle\",\n" +
                        " \"componenteQuantidade\": 1\n" +
                        " }\n" +
                        " ]\n" +
                        "}")
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00" ))
                .statusCode(422);
    }
}
