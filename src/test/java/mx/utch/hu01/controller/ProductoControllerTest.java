package mx.utch.hu01.controller;

import jakarta.validation.ConstraintViolationException;
import mx.utch.hu01.exception.DatoDuplicadoException;
import mx.utch.hu01.exception.RecursoNoEncontradoException;
import mx.utch.hu01.model.Producto;
import mx.utch.hu01.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    private final Producto producto =
            new Producto("1", "Mouse", "MOU-001", new BigDecimal("349.90"), 20);

    @Test
    void creaProducto() throws Exception {
        when(service.crear(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"sku\":\"MOU-001\",\"precio\":349.90,\"existencia\":20}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("MOU-001"));
    }

    @Test
    void listaProductos() throws Exception {
        when(service.listar()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Mouse"));
    }

    @Test
    void obtieneProductoPorId() throws Exception {
        when(service.obtenerPorId("1")).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void actualizaProducto() throws Exception {
        when(service.actualizar(eq("1"), any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"sku\":\"MOU-001\",\"precio\":349.90,\"existencia\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("MOU-001"));
    }

    @Test
    void eliminaProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void responde404CuandoNoExisteElProducto() throws Exception {
        when(service.obtenerPorId("404")).thenThrow(new RecursoNoEncontradoException("No existe"));

        mockMvc.perform(get("/api/productos/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe"));
    }

    @Test
    void responde409CuandoElSkuEstaDuplicado() throws Exception {
        when(service.crear(any(Producto.class))).thenThrow(new DatoDuplicadoException("SKU duplicado"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"sku\":\"MOU-001\",\"precio\":349.90,\"existencia\":20}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SKU duplicado"));
    }

    @Test
    void responde400CuandoFallaLaValidacion() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"MOU-001\",\"precio\":349.90,\"existencia\":20}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void responde400CuandoElJsonEstaMalFormado() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "La solicitud contiene datos inválidos o mal formados"));
    }

    @Test
    void responde400CuandoHayUnaViolacionDeRestriccion() throws Exception {
        when(service.listar()).thenThrow(new ConstraintViolationException("inválido", Set.of()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void responde500AnteUnErrorNoControlado() throws Exception {
        when(service.listar()).thenThrow(new IllegalStateException("error"));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(
                        "Ocurrió un error interno al procesar la solicitud"));
    }
}
