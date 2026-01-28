import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("=== Finding Dish by ID ===");
        Dish dish = dataRetriever.findDishById(1);
        System.out.println(dish);
        System.out.println("Dish Ingredients: " + dish.getDishIngredients());
        System.out.println("Dish Cost: " + dish.getDishCost());
        System.out.println("Gross Margin: " + dish.getGrossMargin());

        System.out.println("\n=== Creating New Ingredients ===");
        List<Ingredient> newIngredients = dataRetriever.createIngredients(List.of(
                new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0),
                new Ingredient(null, "Oignon", CategoryEnum.VEGETABLE, 500.0)
        ));
        System.out.println("Created ingredients: " + newIngredients);

        System.out.println("\n=== Creating Dish with DishIngredients ===");
        List<DishIngredient> dishIngredients = new ArrayList<>();
        dishIngredients.add(new DishIngredient(newIngredients.get(0), 0.2)); // 0.2 kg fromage
        dishIngredients.add(new DishIngredient(newIngredients.get(1), 0.15)); // 0.15 kg oignon

        Dish newDish = dataRetriever.saveDishWithDishIngredients(
                "Salade fromagère",
                DishTypeEnum.STARTER,
                1500.0,
                3000.0,
                dishIngredients
        );
        System.out.println("Saved dish: " + newDish);
        System.out.println("Dish ingredients: " + newDish.getDishIngredients());
        System.out.println("Dish Cost: " + newDish.getDishCost());
        System.out.println("Gross Margin: " + newDish.getGrossMargin());
    }
}
