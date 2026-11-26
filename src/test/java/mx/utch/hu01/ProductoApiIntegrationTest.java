package mx.utch.hu01;

import mx.utch.hu01.model.Producto;
import mx.utch.hu01.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductoApiIntegrationTest {

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/productos_test");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ProductoRepository repository;

    private String url() { return "http://localhost:" + port + "/api/productos"; }

    @BeforeEach
    void limpiar() { repository.deleteAll(); }

    @Test
    void flujoCrudCompletoPorHttp() {
        Producto nuevo = new Producto(null, "Mouse inalámbrico", "MOU-001", new BigDecimal("349.90"), 20);
        ResponseEntity<Producto> creado = rest.postForEntity(url(), nuevo, Producto.class);
        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        assertNotNull(creado.getBody());
        String id = creado.getBody().getId();

        ResponseEntity<Producto> consultado = rest.getForEntity(url() + "/" + id, Producto.class);
        assertEquals(HttpStatus.OK, consultado.getStatusCode());
        assertEquals("MOU-001", consultado.getBody().getSku());

        nuevo.setNombre("Mouse gamer");
        HttpEntity<Producto> solicitud = new HttpEntity<>(nuevo);
        ResponseEntity<Producto> actualizado = rest.exchange(url() + "/" + id, HttpMethod.PUT, solicitud, Producto.class);
        assertEquals(HttpStatus.OK, actualizado.getStatusCode());
        assertEquals("Mouse gamer", actualizado.getBody().getNombre());

        ResponseEntity<Void> eliminado = rest.exchange(url() + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, eliminado.getStatusCode());
        assertFalse(repository.existsById(id));
    }

    @Test
    void devuelve400CuandoFaltanCamposObligatorios() {
        Map<String, Object> invalido = Map.of("sku", "BAD-001", "precio", 50, "existencia", 2);
        ResponseEntity<Map> respuesta = rest.postForEntity(url(), invalido, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertTrue(respuesta.getBody().containsKey("message"));
    }

    @Test
    void devuelve409CuandoElSkuEstaDuplicado() {
        Producto producto = new Producto(null, "Monitor", "MON-001", new BigDecimal("2500"), 4);
        rest.postForEntity(url(), producto, Producto.class);
        ResponseEntity<Map> respuesta = rest.postForEntity(url(), producto, Map.class);
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
    }

    @Test
    void devuelve404CuandoElIdNoExiste() {
        ResponseEntity<Map> respuesta = rest.getForEntity(url() + "/id-inexistente", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
    }
}
