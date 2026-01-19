public class DishIngredient {
    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity;
    private String unit;

    public DishIngredient(Integer id, Dish dish, Ingredient ingredient, Double quantity, String unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Double getCost() {
        return ingredient.getPrice() * quantity;
    }

    // getters
}
