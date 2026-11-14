package inventory;

import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class FileOperations{
    public static class SaleItem {
        public String productId;
        public String productName;
        public int quantity;
        public double unitPrice;
        public double totalPrice;

        public SaleItem(String productId, String productName, int quantity,
                double unitPrice, double totalPrice){
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
        }
    }

    public static void saveProductsData() throws IOException{
        File directory = new File("data");
        if(!directory.exists()){
            directory.mkdirs();
        }

        try(FileWriter file = new FileWriter("data/products.csv")){
            for(Product p: Inventory.availableProducts){
                file.write(p.getProductId() + "," + p.name + ","
                            + p.category + "," + p.brand + "," + p.getPrice() + ","
                            + p.getQuantity() + "," + p.reorderLevel + "\n");
            }
        }
    }

    public static void loadProductsData() throws IOException{
        File file = new File("data/products.csv");
        if(!file.exists()){
            return;
        }

        try(Scanner sc = new Scanner(file)){
            int lineNumber = 0;
            while(sc.hasNextLine()){
                String line = sc.nextLine().trim();
                lineNumber++;
                if(line.isEmpty()){
                    continue;
                }

                String[] s = line.split(",", -1);
                try{
                    if(s.length != 7){
                        throw new IllegalArgumentException("Expected 7 fields.");
                    }

                    Product p = new Product(s[0], s[1], s[2], s[3],
                            Double.parseDouble(s[4]), Integer.parseInt(s[5]),
                            Integer.parseInt(s[6]));
                    if(!Inventory.addProduct(p)){
                        System.out.println("Skipping duplicate product ID on line " + lineNumber + ".");
                    }
                } catch(IllegalArgumentException | InvalidPriceException e){
                    System.out.println("Skipping invalid product data on line " + lineNumber
                            + ": " + e.getMessage());
                }
            }
        }
    }

    public static String getNextSaleId(){
        File salesFile = new File("data/sales.csv");
        int highestId = 0;

        if(salesFile.exists()){
            try(Scanner sc = new Scanner(salesFile)){
                while(sc.hasNextLine()){
                    String line = sc.nextLine().trim();
                    if(line.isEmpty()){
                        continue;
                    }

                    String[] fields = line.split(",");
                    if(fields.length > 0 && fields[0].startsWith("S")){
                        try{
                            int saleNumber = Integer.parseInt(fields[0].substring(1));
                            if(saleNumber > highestId){
                                highestId = saleNumber;
                            }
                        } catch(NumberFormatException ignored){}
                    }
                }
            } catch(IOException e){
                System.out.println("Unable to read sales file.");
            }
        }

        return "S" + String.format("%03d", highestId + 1);
    }

    public static void recordSale(String saleId, List<SaleItem> items) throws IOException{
        File directory = new File("data");
        if(!directory.exists()){
            directory.mkdirs();
        }

        File salesFile = new File(directory, "sales.csv");
        long originalLength = salesFile.exists() ? salesFile.length() : 0;
        StringBuilder saleData = new StringBuilder();
        for(SaleItem item: items){
            saleData.append(saleId).append(",").append(item.productId).append(",")
                    .append(item.productName).append(",").append(item.quantity).append(",")
                    .append(item.unitPrice).append(",").append(item.totalPrice).append("\n");
        }

        try(FileWriter file = new FileWriter(salesFile, true)){
            file.write(saleData.toString());
        } catch(IOException e){
            try(RandomAccessFile rollback = new RandomAccessFile(salesFile, "rw")){
                rollback.setLength(originalLength);
            } catch(IOException rollbackError){
                e.addSuppressed(rollbackError);
            }
            throw e;
        }
    }

    public static void displaySales(){
        File salesFile = new File("data/sales.csv");
        if(!salesFile.exists() || salesFile.length() == 0){
            System.out.println("No sales recorded yet.");
            return;
        }

        System.out.println("\n===== Sales History =====");
        String currentSaleId = "";
        double currentSaleTotal = 0.0;

        try(Scanner sc = new Scanner(salesFile)){
            while(sc.hasNextLine()){
                String line = sc.nextLine().trim();
                if(line.isEmpty()){
                    continue;
                }

                String[] fields = line.split(",");
                if(fields.length >= 6){
                    String saleId = fields[0];
                    String productId = fields[1];
                    String productName = fields[2];
                    String quantity = fields[3];
                    String unitPrice = fields[4];
                    String itemTotal = fields[5];

                    if(!saleId.equals(currentSaleId)){
                        if(!currentSaleId.isEmpty()){
                            System.out.println("Total Sale Amount for " + currentSaleId + ": " + currentSaleTotal);
                            System.out.println("-----------------------------------");
                        }
                        currentSaleId = saleId;
                        currentSaleTotal = 0.0;
                        System.out.println("Sale ID: " + currentSaleId);
                    }

                    currentSaleTotal += Double.parseDouble(itemTotal);
                    System.out.println("  - " + productId + " | " + productName
                            + " | Qty: " + quantity + " | Unit Price: " + unitPrice
                            + " | Item Total: " + itemTotal);
                }
            }

            if(!currentSaleId.isEmpty()){
                System.out.println("Total Sale Amount for " + currentSaleId + ": " + currentSaleTotal);
            }
        } catch(IOException e){
            System.out.println("Unable to read sales data.");
        }
    }
}
