import java.time.Instant;
import java.util.Objects;

public class StockMovement {
    private Integer id;
    private Integer idIngredient;
    private Double quantity;
    private String type; 
    private String unit; 
    private Instant creationDatetime;

    public StockMovement() {
    }

    public StockMovement(Integer id, Integer idIngredient, Double quantity, String type, String unit, Instant creationDatetime) {
        this.id = id;
        this.idIngredient = idIngredient;
        this.quantity = quantity;
        this.type = type;
        this.unit = unit;
        this.creationDatetime = creationDatetime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(Integer idIngredient) {
        this.idIngredient = idIngredient;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(id, that.id) && Objects.equals(idIngredient, that.idIngredient) && Objects.equals(quantity, that.quantity) && Objects.equals(type, that.type) && Objects.equals(unit, that.unit) && Objects.equals(creationDatetime, that.creationDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idIngredient, quantity, type, unit, creationDatetime);
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", idIngredient=" + idIngredient +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", unit='" + unit + '\'' +
                ", creationDatetime=" + creationDatetime +
                '}';
    }
}
