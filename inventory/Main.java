package inventory;

import java.io.IOException;

class Main {
    public static void main(String[] args) {
        try {
            FileOperations.loadProductsData();
        } catch (IOException e) {
            System.out.println("No product data file found. Starting with an empty inventory.");
        }

        while (true) {
            System.out.println("\n==================================================");
            System.out.println("            INVENTORY MANAGEMENT SYSTEM");
            System.out.println("==================================================");
            System.out.println("  1.  Add Product");
            System.out.println("  2.  Remove Product");
            System.out.println("  3.  Update Product");
            System.out.println("  4.  Display Products");
            System.out.println("  5.  Show Alerts");
            System.out.println("  6.  Add Stock to Inventory");
            System.out.println("  7.  Sell Stock");
            System.out.println("  8.  Search Product");
            System.out.println("  9.  Show Sales");
            System.out.println("  10. Exit");
            System.out.println("==================================================");

            int choice = InputHandler.getInt("Select an option: ");

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    removeProduct();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    displayProducts();
                    break;
                case 5:
                    showAlerts();
                    break;
                case 6:
                    Inventory.addStockToInventory();
                    break;
                case 7:
                    sellStock();
                    break;
                case 8:
                    searchProduct();
                    break;
                case 9:
                    showSales();
                    break;
                case 10:
                    try {
                        FileOperations.saveProductsData();
                    } catch (IOException e) {
                        System.out.println("Unable to save inventory data.");
                    }
                    System.out.println("Exiting inventory system...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addProduct() {
        Product product = Product.getProduct();
        if(Inventory.addProduct(product)){
            System.out.println("Product added successfully.");
        }
        else {
            System.out.println("Product ID already exists.");
        }
    }

    private static void removeProduct() {
        String productId = InputHandler.getString("Enter Product ID to remove: ");
        Inventory.removeProduct(productId);
    }

    private static void updateProduct() {
        String productId = InputHandler.getString("Enter Product ID to update: ");
        Inventory.updateProduct(productId);
    }

    private static void displayProducts() {
        Inventory.displayAllProducts();
    }

    private static void sellStock() {
        Inventory.sellStock();
    }

    private static void searchProduct() {
        String productId = InputHandler.getString("Enter Product ID to search: ");
        Inventory.searchProduct(productId);
    }

    private static void showSales() {
        FileOperations.displaySales();
    }

    private static void showAlerts() {
        Alerts.showInventoryAlerts();
        if (Inventory.availableProducts.isEmpty()) {
            System.out.println("No products in stock.");
        }
    }
}
