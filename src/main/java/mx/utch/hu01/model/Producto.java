package mx.utch.hu01.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "productos")
public class Producto {
    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 30, message = "El SKU no debe superar 30 caracteres")
    @Indexed(unique = true)
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    private BigDecimal precio;

    @NotNull(message = "La existencia es obligatoria")
    @jakarta.validation.constraints.Min(value = 0, message = "La existencia no puede ser negativa")
    private Integer existencia;

    public Producto() {}

    public Producto(String id, String nombre, String sku, BigDecimal precio, Integer existencia) {
        this.id = id;
        this.nombre = nombre;
        this.sku = sku;
        this.precio = precio;
        this.existencia = existencia;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }
}
