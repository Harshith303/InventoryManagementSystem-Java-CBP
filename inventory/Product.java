package inventory;

class InvalidPriceException extends RuntimeException{
    public InvalidPriceException(String message){
        super(message);
    }
}

class Product{
    private String productId;
    String name;
    String category;
    String brand;
    private double price;
    private int quantity;
    int reorderLevel;

    Product(String ProductId, String name, String category, String brand,
        double price, int quantity, int reorderLevel){
            if(ProductId == null || ProductId.trim().isEmpty()){
                throw new IllegalArgumentException("Product ID cannot be empty.");
            }
            if(quantity < 0 || reorderLevel < 0){
                throw new IllegalArgumentException("Quantity and reorder level cannot be negative.");
            }
            if(price <= 0 || !Double.isFinite(price)){
                throw new InvalidPriceException("Invalid Price");
            }
            this.productId = ProductId.trim();
            this.name = name;
            this.category = category;
            this.brand = brand;
            this.price = price;
            this.quantity = quantity;
            this.reorderLevel = reorderLevel;
        }

    public String getProductId(){
        return this.productId;
    }

    public void setPrice(double price){
        if(price > 0 && Double.isFinite(price))this.price = price;
        else throw new InvalidPriceException("Invalid Price");
    }

    public double getPrice(){
        return this.price;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public boolean isBelowReorderLevel(){
        return this.quantity < this.reorderLevel;
    }

    public boolean addStock(int amount){
        if(amount <= 0 || amount > Integer.MAX_VALUE - this.quantity){
            return false;
        }

        this.quantity += amount;
        Alerts.showAlert(this);
        return true;
    }

    public boolean reduceStock(int amount){
        if(amount > 0 && this.quantity >= amount){
            this.quantity -= amount;
            Alerts.showAlert(this);
            return true;
        }
        return false;
    }

    public boolean sellStock(int amount){
        return reduceStock(amount);
    }

    public static Product getProduct(){

        String productId;
        while(true){
            productId = InputHandler.getString("Product ID: ").trim();
            if(productId.isEmpty()){
                System.out.println("Product ID cannot be empty.");
                continue;
            }
            if(Inventory.findProductById(productId) == null){
                break;
            }
            System.out.println("Product ID already exists. Enter a different ID.");
        }
        String name = InputHandler.getString("Product Name: ");
        String category = InputHandler.getString("Category: ");
        String brand = InputHandler.getString("Brand: ");
        double price = InputHandler.getPositiveDouble("Price: ");
        int quantity = InputHandler.getNonNegativeInt("Quantity: ");
        int reorderLevel = InputHandler.getNonNegativeInt("ReorderLevel: ");

        return new Product(productId, name, category, brand, price, quantity, reorderLevel);

    }

    public void displayProduct(){
        System.out.println("Product ID: " + productId);
        System.out.println("Name: " + name);
        System.out.println("Category: " + category);
        System.out.println("Brand: " + brand);
        System.out.println("Price: " + price);
        System.out.println("Quantity: " + quantity);
        System.out.println("Reorder Level: " + reorderLevel);
        System.out.println("Status: " + (isBelowReorderLevel() ? "LOW STOCK" : "AVAILABLE"));
    }

    public String toString(){
        return (productId + " " + name);
    }

}
