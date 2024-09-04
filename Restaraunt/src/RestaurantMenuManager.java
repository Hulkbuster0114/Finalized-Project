import java.sql.*;
import java.util.Scanner;

public class RestaurantMenuManager {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/restaurant";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            System.out.println("Connected to the database!");
            Scanner scanner = new Scanner(System.in);

            // Menu for user
            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Insert new menu item");
                System.out.println("2. Retrieve menu items");
                System.out.println("3. Update a menu item");
                System.out.println("4. Delete a menu item");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        insertMenuItem(conn, scanner);
                        break;
                    case 2:
                        retrieveMenuItems(conn);
                        break;
                    case 3:
                        updateMenuItem(conn, scanner);
                        break;
                    case 4:
                        deleteMenuItem(conn, scanner);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertMenuItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        String sql = "INSERT INTO MenuItems (item_name, description, category) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int itemId = rs.getInt(1);
                        System.out.println("Inserted new menu item with ID: " + itemId);
                    }
                }
            }
        }
    }

    private static void retrieveMenuItems(Connection conn) throws SQLException {
        String sql = "SELECT * FROM MenuOverview";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("Item: %s, Description: %s, Category: %s, Calories: %d, Fat: %.2f, Protein: %.2f, Carbohydrates: %.2f, Alcohol: %.2f, Cost: %.2f, Price: %.2f%n",
                        rs.getString("item_name"), rs.getString("description"), rs.getString("category"),
                        rs.getInt("calories"), rs.getBigDecimal("fat"), rs.getBigDecimal("protein"),
                        rs.getBigDecimal("carbohydrates"), rs.getBigDecimal("alcohol"), rs.getBigDecimal("cost"), rs.getBigDecimal("price"));
            }
        }
    }

    private static void updateMenuItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter item ID to update: ");
        int itemId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter new description: ");
        String description = scanner.nextLine();
        System.out.print("Enter new category: ");
        String category = scanner.nextLine();

        String sql = "UPDATE MenuItems SET item_name = ?, description = ?, category = ? WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setInt(4, itemId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated menu item with ID: " + itemId);
            } else {
                System.out.println("Menu item with ID: " + itemId + " not found.");
            }
        }
    }

    private static void deleteMenuItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter item ID to delete: ");
        int itemId = scanner.nextInt();

        String sql = "DELETE FROM MenuItems WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Deleted menu item with ID: " + itemId);
            } else {
                System.out.println("Menu item with ID: " + itemId + " not found.");
            }
        }
    }
}