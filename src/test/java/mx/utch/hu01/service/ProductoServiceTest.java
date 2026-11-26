package mx.utch.hu01.service;

import mx.utch.hu01.exception.DatoDuplicadoException;
import mx.utch.hu01.exception.RecursoNoEncontradoException;
import mx.utch.hu01.model.Producto;
import mx.utch.hu01.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock private ProductoRepository repository;
    @InjectMocks private ProductoService service;
    private Producto producto;

    @BeforeEach
    void preparar() {
        producto = new Producto("1", "Teclado mecánico", "TEC-001", new BigDecimal("899.00"), 12);
    }

    @Test
    void crearProductoCorrectamente() {
        when(repository.existsBySku("TEC-001")).thenReturn(false);
        when(repository.save(producto)).thenReturn(producto);
        assertEquals("TEC-001", service.crear(producto).getSku());
        verify(repository).save(producto);
    }

    @Test
    void noPermiteSkuDuplicadoAlCrear() {
        when(repository.existsBySku("TEC-001")).thenReturn(true);
        assertThrows(DatoDuplicadoException.class, () -> service.crear(producto));
        verify(repository, never()).save(any());
    }

    @Test
    void listaProductos() {
        when(repository.findAll()).thenReturn(List.of(producto));
        assertEquals(1, service.listar().size());
    }

    @Test
    void obtieneProductoPorId() {
        when(repository.findById("1")).thenReturn(Optional.of(producto));
        assertEquals("Teclado mecánico", service.obtenerPorId("1").getNombre());
    }

    @Test
    void lanzaErrorSiNoExisteProducto() {
        when(repository.findById("404")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId("404"));
    }

    @Test
    void actualizaProducto() {
        when(repository.findById("1")).thenReturn(Optional.of(producto));
        when(repository.existsBySkuAndIdNot("TEC-002", "1")).thenReturn(false);
        Producto datos = new Producto(null, "Teclado RGB", "TEC-002", new BigDecimal("999.00"), 8);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        Producto actualizado = service.actualizar("1", datos);
        assertEquals("Teclado RGB", actualizado.getNombre());
        assertEquals("TEC-002", actualizado.getSku());
    }

    @Test
    void noActualizaConSkuDeOtroProducto() {
        when(repository.findById("1")).thenReturn(Optional.of(producto));
        when(repository.existsBySkuAndIdNot("TEC-002", "1")).thenReturn(true);
        Producto datos = new Producto(null, "Otro", "TEC-002", new BigDecimal("100.00"), 1);
        assertThrows(DatoDuplicadoException.class, () -> service.actualizar("1", datos));
    }

    @Test
    void eliminaProductoExistente() {
        when(repository.findById("1")).thenReturn(Optional.of(producto));
        service.eliminar("1");
        verify(repository).delete(producto);
    }
}
