import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.price as dish_price, dish.selling_price
                            from dish
                            where dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));
                Object sellingPrice = resultSet.getObject("selling_price");
                dish.setSellingPrice(sellingPrice == null ? null : resultSet.getDouble("selling_price"));
                dish.setDishIngredients(findDishIngredientsByDishId(id));
                return dish;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, price, name, dish_type, selling_price)
                    VALUES (?, ?, ?, ?::dish_type, ?)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        price = EXCLUDED.price,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }
                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                if (toSave.getSellingPrice() != null) {
                    ps.setDouble(5, toSave.getSellingPrice());
                } else {
                    ps.setNull(5, Types.DOUBLE);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            List<DishIngredient> dishIngredients = toSave.getDishIngredients();
            detachDishIngredients(conn, dishId);
            attachDishIngredients(conn, dishId, dishIngredients);

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, category, price)
                        VALUES (?, ?, ?::ingredient_category, ?)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }


    private void detachDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dish_ingredient WHERE dish_id = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachDishIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients)
            throws SQLException {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return;
        }

        String attachSql = """
                    INSERT INTO dish_ingredient (dish_id, ingredient_id, quantity, unit)
                    VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredient().getId());
                ps.setDouble(3, di.getQuantity());
                ps.setString(4, "piece");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> dishIngredients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select di.ingredient_id, di.quantity, di.unit,
                                   i.id, i.name, i.price, i.category
                            from dish_ingredient di
                            join ingredient i on i.id = di.ingredient_id
                            where di.dish_id = ?;
                            """);
            preparedStatement.setInt(1, dishId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));

                DishIngredient dishIngredient = new DishIngredient(
                        ingredient,
                        resultSet.getDouble("quantity")
                );
                dishIngredients.add(dishIngredient);
            }
            dbConnection.closeConnection(connection);
            return dishIngredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }

    public List<DishIngredient> findDishIngredientsByDishIdPublic(Integer dishId) {
        return findDishIngredientsByDishId(dishId);
    }

    public Dish saveDishWithDishIngredients(String dishName, DishTypeEnum dishType, Double dishPrice, Double sellingPrice, List<DishIngredient> dishIngredients) {
        Dish dish = new Dish();
        dish.setName(dishName);
        dish.setDishType(dishType);
        dish.setPrice(dishPrice);
        dish.setSellingPrice(sellingPrice);
        dish.setDishIngredients(dishIngredients);
        return saveDish(dish);
    }

    public Ingredient saveIngredient(Ingredient toSave) {
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String upsertIngredientSql = """
                    INSERT INTO ingredient (id, name, category, price)
                    VALUES (?, ?, ?::ingredient_category, ?)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        category = EXCLUDED.category,
                        price = EXCLUDED.price
                    RETURNING id
                """;

            Integer ingredientId;
            try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                }
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getCategory().name());
                ps.setDouble(4, toSave.getPrice());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    ingredientId = rs.getInt(1);
                }
            }

            List<StockMovement> stockMovements = toSave.getStockMovementList();
            if (stockMovements != null && !stockMovements.isEmpty()) {
                String stockMovementSql = """
                        INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT DO NOTHING
                    """;

                try (PreparedStatement ps = conn.prepareStatement(stockMovementSql)) {
                    for (StockMovement movement : stockMovements) {
                        if (movement.getId() != null) {
                            ps.setInt(1, movement.getId());
                        } else {
                            ps.setInt(1, getNextSerialValue(conn, "stock_movement", "id"));
                        }
                        ps.setInt(2, ingredientId);
                        ps.setDouble(3, movement.getQuantity());
                        ps.setString(4, movement.getType());
                        ps.setString(5, movement.getUnit());
                        ps.setObject(6, movement.getCreationDatetime());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return findIngredientById(ingredientId);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException(rollbackException);
            }
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    public Double getStockValueAt(Integer ingredientId, Instant instant) {
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            String sql = """
                    SELECT COALESCE(SUM(CASE WHEN type = 'IN' THEN quantity ELSE -quantity END), 0) as total_quantity
                    FROM stock_movement
                    WHERE id_ingredient = ? AND creation_datetime <= ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ingredientId);
                ps.setObject(2, instant);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("total_quantity");
                    }
                }
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    public Ingredient findIngredientById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            String sql = """
                    SELECT id, name, category, price
                    FROM ingredient
                    WHERE id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(rs.getInt("id"));
                        ingredient.setName(rs.getString("name"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                        ingredient.setPrice(rs.getDouble("price"));

                        List<StockMovement> movements = new ArrayList<>();
                        String movementSql = """
                                SELECT id, id_ingredient, quantity, type, unit, creation_datetime
                                FROM stock_movement
                                WHERE id_ingredient = ?
                                ORDER BY creation_datetime
                            """;
                        try (PreparedStatement movementPs = conn.prepareStatement(movementSql)) {
                            movementPs.setInt(1, id);
                            try (ResultSet movementRs = movementPs.executeQuery()) {
                                while (movementRs.next()) {
                                    StockMovement movement = new StockMovement();
                                    movement.setId(movementRs.getInt("id"));
                                    movement.setIdIngredient(movementRs.getInt("id_ingredient"));
                                    movement.setQuantity(movementRs.getDouble("quantity"));
                                    movement.setType(movementRs.getString("type"));
                                    movement.setUnit(movementRs.getString("unit"));
                                    movement.setCreationDatetime((Instant) movementRs.getObject("creation_datetime"));
                                    movements.add(movement);
                                }
                            }
                        }
                        ingredient.setStockMovementList(movements);
                        return ingredient;
                    }
                }
            }
            throw new RuntimeException("Ingredient not found with id: " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }}