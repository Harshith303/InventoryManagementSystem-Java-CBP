package inventory;

import java.util.List;
import java.util.ArrayList;

class Inventory{
    static List<Product> availableProducts = new ArrayList<Product>();

    public static void displayAllProducts(){
        if(availableProducts.isEmpty()){
            System.out.println("No products available.");
            return;
        }

        System.out.println("\n===== All Products =====");
        for(Product p: availableProducts){
            p.displayProduct();
            System.out.println("------------------------------");
        }
    }

    public static Product findProductById(String id){
        for(Product p: availableProducts){
            if(p.getProductId().equalsIgnoreCase(id)){
                return p;
            }
        }
        return null;
    }

    public static void searchProduct(String id){
        Product product = findProductById(id);
        if(product == null){
            System.out.println("No Such Product Exists");
            return;
        }

        System.out.println("\n===== Product Details =====");
        product.displayProduct();
    }

    public static void sellStock(String id, int quantity){
        Product product = findProductById(id);
        if(product == null){
            System.out.println("No Such Product Exists");
            return;
        }

        if(quantity <= 0){
            System.out.println("Invalid quantity.");
            return;
        }

        if(product.getQuantity() < quantity){
            System.out.println("Insufficient stock. Available: " + product.getQuantity());
            return;
        }

        String saleId = FileOperations.getNextSaleId();
        List<FileOperations.SaleItem> saleItems = new ArrayList<FileOperations.SaleItem>();
        double totalPrice = quantity * product.getPrice();

        if(product.sellStock(quantity)){
            saleItems.add(new FileOperations.SaleItem(product.getProductId(), product.name,
                    quantity, product.getPrice(), totalPrice));
            try{
                FileOperations.recordSale(saleId, saleItems);
                System.out.println("Sale recorded successfully. Sale ID: " + saleId);
                System.out.println("Total Amount: " + totalPrice);
            } catch(java.io.IOException e){
                if(!product.addStock(quantity)){
                    System.out.println("Unable to restore stock for product " + product.getProductId() + ".");
                }
                reportSaleSaveFailure(e);
            }
        }
    }

    public static void sellStock(){
        String saleId = FileOperations.getNextSaleId();
        List<FileOperations.SaleItem> saleItems = new ArrayList<FileOperations.SaleItem>();
        double totalSaleAmount = 0.0;
        int itemCount = 0;

        while(true){
            String productId = InputHandler.getString("\nEnter Product ID to sell (C000 to finish): ");
            if(productId.equalsIgnoreCase("C000")){
                break;
            }

            Product product = findProductById(productId);
            if(product == null){
                System.out.println("No Such Product Exists");
                continue;
            }

            int quantity = InputHandler.getPositiveInt("Enter quantity to sell: ");

            if(product.getQuantity() < quantity){
                System.out.println("Insufficient stock. Available: " + product.getQuantity());
                continue;
            }

            double itemTotal = quantity * product.getPrice();
            totalSaleAmount += itemTotal;
            itemCount++;

            if(product.sellStock(quantity)){
                saleItems.add(new FileOperations.SaleItem(product.getProductId(), product.name,
                        quantity, product.getPrice(), itemTotal));
                System.out.println("Added to sale: " + product.name + " x " + quantity);
            }
        }

        if(itemCount == 0){
            System.out.println("No products were sold.");
            return;
        }

        try{
            FileOperations.recordSale(saleId, saleItems);
            System.out.println("Sale completed successfully. Sale ID: " + saleId);
            System.out.println("Total Sale Amount: " + totalSaleAmount);
        } catch(java.io.IOException e){
            for(FileOperations.SaleItem item: saleItems){
                Product product = findProductById(item.productId);
                if(product != null && !product.addStock(item.quantity)){
                    System.out.println("Unable to restore stock for product " + item.productId + ".");
                }
            }
            reportSaleSaveFailure(e);
        }
    }

    private static void reportSaleSaveFailure(java.io.IOException e){
        System.out.println("Unable to save sale record: " + e.getMessage());
        for(Throwable rollbackError: e.getSuppressed()){
            System.out.println("Unable to roll back the sales file: " + rollbackError.getMessage());
        }
    }

    public static boolean addProduct(Product p){
        if(p == null){
            return false;
        }
        if(findProductById(p.getProductId()) != null){
            return false;
        }

        availableProducts.add(p);
        Alerts.showAlert(p);
        return true;
    }

    public static void removeProduct(String id){
        Product productToRemove = null;
        for(Product p: availableProducts){
            if(p.getProductId().equalsIgnoreCase(id)){
                productToRemove = p;
                break;
            }
        }

        if(productToRemove != null){
            availableProducts.remove(productToRemove);
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("No Such Product Exists");
        }
    }

    public static void updateProduct(String id){
        for(Product p: availableProducts){
            if(p.getProductId().equalsIgnoreCase(id)){
                System.out.println("\n1.Product Name");
                System.out.println("2.Category");
                System.out.println("3.Brand");
                System.out.println("4.Price");
                System.out.println("5.ReorderLevel");
                System.out.println("6.Exit");

                while(true){
                    int choice = InputHandler.getInt("\nEnter Choice to update: ");


                    switch(choice){
                        case 1:
                            String pn = InputHandler.getString("\nProduct Name: ");
                            p.name = pn;
                            return;

                        case 2:
                            String c = InputHandler.getString("\nCategory: ");
                            p.category = c;
                            return;

                        case 3:
                            String b = InputHandler.getString("\nBrand: ");
                            p.brand = b;
                            return;

                        case 4:
                            double price = InputHandler.getPositiveDouble("\nPrice: ");
                            p.setPrice(price);
                            return;

                        case 5:
                            int rl = InputHandler.getNonNegativeInt("\nReorderLevel: ");
                            p.reorderLevel = rl;
                            Alerts.showAlert(p);
                            return;

                        case 6:
                            return;

                        default:
                            System.out.println("\nEnter a Valid Option");
                    }
                }
            }
        }

        System.out.println("No Such Product Exists");
    }

    public static void addStockToInventory(){
        System.out.println("\nChecking stock alerts before adding inventory...\n");
        Alerts.showInventoryAlerts();

        while(true){
            String id = InputHandler.getString("\nEnter ID of product to add the stock(C000 to exit): ");
            if(id.equalsIgnoreCase("C000")){
                break;
            }

            Product foundProduct = null;
            for(Product p: availableProducts){
                if(p.getProductId().equalsIgnoreCase(id)){
                    foundProduct = p;
                    break;
                }
            }

            if(foundProduct == null){
                System.out.println("Product DoesNot Exist");
                continue;
            }

            int q = InputHandler.getPositiveInt("Enter the Quantity: ");
            if(foundProduct.addStock(q)){
                System.out.println("Stock Added Successfully\n");
            } else {
                System.out.println("Unable to add stock; the resulting quantity would be too large.");
            }
        }
    }

}
