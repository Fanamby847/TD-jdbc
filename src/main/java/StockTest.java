import java.time.Instant;

public class StockTest {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();
        Instant testTime = Instant.parse("2024-01-06T12:00:00Z");

        int[] ids = {1, 2, 3, 4, 5};
        String[] names = {"Laitue", "Tomate", "Poulet", "Chocolat", "Beurre"};
        double[] expected = {4.8, 3.85, 9.0, 2.7, 2.3};

        System.out.println("Testing Stock Values at " + testTime);

        for (int i = 0; i < ids.length; i++) {
            testStock(retriever, ids[i], names[i], expected[i], testTime);
        }

        System.out.println("Test completed");
    }

    static void testStock(DataRetriever retriever, int id, String name, double expectedValue, Instant time) {
        try {
            Double actualValue = retriever.getStockValueAt(id, time);
            
            boolean isCorrect = Math.abs(actualValue - expectedValue) < 0.01;
            String result = isCorrect ? "✓" : "✗";
            
            System.out.printf("%s: Expected=%.2f, Actual=%.2f, Match=%s%n",
                    name, expectedValue, actualValue, result);
        } catch (Exception e) {
            System.out.printf("%s: ERROR - %s%n", name, e.getMessage());
        }
    }
}
