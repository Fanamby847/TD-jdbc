import java.time.Instant;

public class StockTest {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();
        Instant testTime = Instant.parse("2024-01-06T12:00:00Z");

        
        int[] ingredientIds = {1, 2, 3, 4, 5};
        String[] ingredientNames = {"Laitue", "Tomate", "Poulet", "Chocolat", "Beurre"};
        double[] expectedStocks = {4.8, 3.85, 9.0, 2.7, 2.3};

        System.out.println("Testing Stock Values at " + testTime);

        for (int i = 0; i < ingredientIds.length; i++) {
            try {
                Double stockValue = retriever.getStockValueAt(ingredientIds[i], testTime);
                System.out.printf("%s: Expected=%.2f, Actual=%.2f, Match=%s%n",
                        ingredientNames[i],
                        expectedStocks[i],
                        stockValue,
                        Math.abs(stockValue - expectedStocks[i]) < 0.01 ? "✓" : "✗");
            } catch (Exception e) {
                System.out.printf("%s: ERROR - %s%n", ingredientNames[i], e.getMessage());
            }
        }

        System.out.println("================================================");
        System.out.println("Test completed");
    }
}
