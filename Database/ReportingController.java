import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportingController {
    private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_85_db";
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Models.UsageItem> usageTable;
    @FXML private TableColumn<Models.UsageItem, String> usageItemCol;
    @FXML private TableColumn<Models.UsageItem, Double> usageQuantityCol;
    @FXML private Button generateUsageBtn;
    
    @FXML private TextArea xReportArea;
    @FXML private Button generateXReportBtn;
    
    @FXML private TextArea zReportArea;
    @FXML private Button generateZReportBtn;
    @FXML private Label lastZReportLabel;
    
    @FXML private DatePicker salesStartDatePicker;
    @FXML private DatePicker salesEndDatePicker;
    @FXML private TableView<Models.SalesItem> salesTable;
    @FXML private TableColumn<Models.SalesItem, String> salesItemCol;
    @FXML private TableColumn<Models.SalesItem, Integer> salesQuantityCol;
    @FXML private TableColumn<Models.SalesItem, Double> salesRevenueCol;
    @FXML private Button generateSalesReportBtn;
    
    @FXML private TextField newItemName;
    @FXML private TextField newItemPrice;
    @FXML private TextArea ingredientsArea;
    @FXML private Button addMenuItemBtn;
    @FXML private Label addItemStatus;

    @FXML
    public void initialize() {
        setupUsageTable();
        setupSalesTable();
        setupEventHandlers();
        checkLastZReport();
    }

    // Helper to ensure driver is loaded
    private Connection getConnection() throws SQLException {
        try { Class.forName("org.postgresql.Driver"); } catch (Exception e) {}
        dbSetup my = new dbSetup();
        return DriverManager.getConnection(DB_URL, my.user, my.pswd);
    }

    private void setupUsageTable() {
        usageItemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        usageQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantityUsed"));
    }

    private void setupSalesTable() {
        salesItemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        salesQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        salesRevenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
    }

    private void setupEventHandlers() {
        generateUsageBtn.setOnAction(e -> generateProductUsageChart());
        generateXReportBtn.setOnAction(e -> generateXReport());
        generateZReportBtn.setOnAction(e -> generateZReport());
        generateSalesReportBtn.setOnAction(e -> generateSalesReport());
        addMenuItemBtn.setOnAction(e -> addNewMenuItem());
    }

    private void checkLastZReport() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT MAX(report_date) FROM z_reports");
            if (rs.next() && rs.getDate(1) != null) {
                Date lastZReportDate = rs.getDate(1);
                lastZReportLabel.setText("Last Z-Report: " + lastZReportDate.toString());
                
                if (lastZReportDate.toLocalDate().equals(LocalDate.now())) {
                    generateZReportBtn.setDisable(true);
                    generateZReportBtn.setText("Z-Report Already Run Today");
                    // Load and display today's Z-report content
                    loadTodaysZReport();
                }
            } else {
                lastZReportLabel.setText("No Z-Reports found");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTodaysZReport() {
        String sql = "SELECT total_orders, total_revenue, tax_amount FROM z_reports WHERE DATE(report_date) = CURRENT_DATE ORDER BY report_date DESC LIMIT 1";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder report = new StringBuilder();
                LocalDate today = LocalDate.now();
                report.append("=== Z-REPORT FOR ").append(today.format(DateTimeFormatter.ISO_DATE)).append(" ===\n\n");
                
                int totalOrders = rs.getInt("total_orders");
                double totalRevenue = rs.getDouble("total_revenue");
                double taxAmount = rs.getDouble("tax_amount");
                
                report.append(String.format("Total Orders: %d\n", totalOrders));
                report.append(String.format("Gross Sales: $%.2f\nTax (8.25%%): $%.2f\nNet Sales: $%.2f\n", totalRevenue, taxAmount, totalRevenue - taxAmount));
                report.append("\n--- Previously Generated Z-Report ---");
                
                zReportArea.setText(report.toString());
            }
        } catch (Exception e) { 
            System.err.println("Error loading today's Z-report: " + e.getMessage());
            zReportArea.setText("Error loading today's Z-report content");
        }
    }

    private void generateProductUsageChart() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Please select both start and end dates");
            return;
        }
        
        ObservableList<Models.UsageItem> data = FXCollections.observableArrayList();
        
        String sql = "SELECT item_name, SUM(total_used) as total_used_sum FROM (SELECT i.item_name, (COUNT(oi.menu_id) * mi.quantity_used) as total_used " +
                     "FROM inventory i LEFT JOIN menu_ingredients mi ON i.id = mi.inventory_id " +
                     "LEFT JOIN order_items oi ON mi.menu_id = oi.menu_id " +
                     "LEFT JOIN orders o ON oi.order_id = o.order_id " +
                     "WHERE DATE(o.order_time) BETWEEN ? AND ? GROUP BY i.item_name, mi.quantity_used) AS subquery " +
                     "GROUP BY item_name ORDER BY total_used_sum DESC LIMIT 10";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDatePicker.getValue()));
            pstmt.setDate(2, Date.valueOf(endDatePicker.getValue()));
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                data.add(new Models.UsageItem(rs.getString("item_name"), rs.getDouble("total_used_sum")));
            }
            usageTable.setItems(data);
        } catch (Exception e) { showAlert("Error generating usage report: " + e.getMessage()); }
    }

    private void generateXReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== X-REPORT FOR ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append(" ===\n\n");
        
        // Check if Z-report has been generated today
        boolean zReportGeneratedToday = false;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM z_reports WHERE DATE(report_date) = CURRENT_DATE");
            if (rs.next() && rs.getInt(1) > 0) {
                zReportGeneratedToday = true;
            }
        } catch (Exception e) { 
            // If we can't check, assume no Z-report
        }
        
        if (zReportGeneratedToday) {
            report.append("HOURLY SALES BREAKDOWN:\n------------------------\n");
            report.append("Day closed - Z-Report already generated\n");
            report.append("All sales figures reset to $0.00\n\n");
            report.append("00:00-23:59: 0 orders, $0.00 sales\n");
        } else {
            report.append("HOURLY SALES BREAKDOWN:\n------------------------\n");
            String sql = "SELECT EXTRACT(HOUR FROM order_time) as hour, COUNT(*) as order_count, SUM(total_price) as total_sales " +
                         "FROM orders WHERE DATE(order_time) = CURRENT_DATE GROUP BY hour ORDER BY hour";
                         
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    report.append(String.format("%02d:00-%02d:59: %d orders, $%.2f sales\n", 
                                  rs.getInt("hour"), rs.getInt("hour"), rs.getInt("order_count"), rs.getDouble("total_sales")));
                }
            } catch (Exception e) { showAlert("Error generating X-Report: " + e.getMessage()); }
        }
        
        xReportArea.setText(report.toString());
    }

    private void generateZReport() {
        StringBuilder report = new StringBuilder();
        LocalDate today = LocalDate.now();
        report.append("=== Z-REPORT FOR ").append(today.format(DateTimeFormatter.ISO_DATE)).append(" ===\n\n");

        String sql = "SELECT COUNT(*) as total_orders, SUM(total_price) as total_revenue FROM orders WHERE DATE(order_time) = CURRENT_DATE";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                double totalRev = rs.getDouble("total_revenue");
                double tax = totalRev * 0.0825;
                report.append(String.format("Total Orders: %d\n", rs.getInt("total_orders")));
                report.append(String.format("Gross Sales: $%.2f\nTax (8.25%%): $%.2f\nNet Sales: $%.2f\n", totalRev, tax, totalRev - tax));
                
                try {
                    String insertSql = "INSERT INTO z_reports (report_date, total_orders, total_revenue, tax_amount) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    pstmt.setDate(1, Date.valueOf(today));
                    pstmt.setInt(2, rs.getInt("total_orders"));
                    pstmt.setDouble(3, totalRev);
                    pstmt.setDouble(4, tax);
                    pstmt.executeUpdate();
                } catch (Exception ignored) {} // Fails gracefully if z_reports table doesn't exist yet
            }
            conn.commit();
            zReportArea.setText(report.toString());
            generateZReportBtn.setDisable(true);
            generateZReportBtn.setText("Z-Report Already Run");
            lastZReportLabel.setText("Last Z-Report: " + today.toString());
        } catch (Exception e) { showAlert("Error generating Z-Report: " + e.getMessage()); }
    }

    private void generateSalesReport() {
        if (salesStartDatePicker.getValue() == null || salesEndDatePicker.getValue() == null) {
            showAlert("Please select dates"); return;
        }
        ObservableList<Models.SalesItem> data = FXCollections.observableArrayList();
        String sql = "SELECT m.item_name, COUNT(oi.menu_id) as quantity_sold, SUM(m.price) as total_revenue " +
                     "FROM menu m JOIN order_items oi ON m.id = oi.menu_id JOIN orders o ON oi.order_id = o.order_id " +
                     "WHERE DATE(o.order_time) BETWEEN ? AND ? GROUP BY m.id, m.item_name ORDER BY quantity_sold DESC";
                     
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(salesStartDatePicker.getValue()));
            pstmt.setDate(2, Date.valueOf(salesEndDatePicker.getValue()));
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                data.add(new Models.SalesItem(rs.getString("item_name"), rs.getInt("quantity_sold"), rs.getDouble("total_revenue")));
            }
            salesTable.setItems(data);
        } catch (Exception e) { showAlert("Error: " + e.getMessage()); }
    }

    private void addNewMenuItem() {
        String name = newItemName.getText().trim();
        String priceText = newItemPrice.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) { showAlert("Enter name and price"); return; }

        try (Connection conn = getConnection()) {
            double price = Double.parseDouble(priceText);
            // Re-used the safe insert query from your ManagerDashboard logic
            String sql = "INSERT INTO menu (id, item_name, price) VALUES ((SELECT COALESCE(MAX(id), 0) + 1 FROM menu), ?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            
            // Execute and clean up UI
            pstmt.executeQuery(); 
            addItemStatus.setText("Menu item added!");
            addItemStatus.setStyle("-fx-text-fill: green;");
            newItemName.clear(); newItemPrice.clear(); ingredientsArea.clear();
        } catch (Exception e) { showAlert("Error: " + e.getMessage()); }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}