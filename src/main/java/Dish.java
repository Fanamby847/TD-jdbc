import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private Double sellingPrice;
    private List<Ingredient> ingredients;
    private List<DishIngredient> dishIngredients;

    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.sellingPrice = sellingPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public Double getDishCost() {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return 0.0;
        }
        return dishIngredients.stream()
                .mapToDouble(di -> di.getIngredient().getPrice() * di.getQuantity())
                .sum();
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Selling price is null for dish: " + name);
        }
        return sellingPrice - getDishCost();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", price=" + price +
                ", sellingPrice=" + sellingPrice +
                '}';
    }
}
