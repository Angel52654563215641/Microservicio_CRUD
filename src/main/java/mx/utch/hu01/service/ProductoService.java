package mx.utch.hu01.service;

import mx.utch.hu01.exception.DatoDuplicadoException;
import mx.utch.hu01.exception.RecursoNoEncontradoException;
import mx.utch.hu01.model.Producto;
import mx.utch.hu01.repository.ProductoRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Producto crear(Producto producto) {
        if (repository.existsBySku(producto.getSku())) {
            throw new DatoDuplicadoException("Ya existe un producto con el SKU: " + producto.getSku());
        }
        try {
            return repository.save(producto);
        } catch (DuplicateKeyException ex) {
            throw new DatoDuplicadoException("El SKU ya está registrado");
        }
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto obtenerPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con id: " + id));
    }

    public Producto actualizar(String id, Producto datos) {
        Producto actual = obtenerPorId(id);
        if (repository.existsBySkuAndIdNot(datos.getSku(), id)) {
            throw new DatoDuplicadoException("Ya existe otro producto con el SKU: " + datos.getSku());
        }
        actual.setNombre(datos.getNombre());
        actual.setSku(datos.getSku());
        actual.setPrecio(datos.getPrecio());
        actual.setExistencia(datos.getExistencia());
        try {
            return repository.save(actual);
        } catch (DuplicateKeyException ex) {
            throw new DatoDuplicadoException("El SKU ya está registrado");
        }
    }

    public void eliminar(String id) {
        Producto producto = obtenerPorId(id);
        repository.delete(producto);
    }
}
