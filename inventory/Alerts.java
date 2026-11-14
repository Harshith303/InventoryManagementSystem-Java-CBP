package inventory;

class Alerts{
    public static void showAlert(Product product){
        if(product == null){
            return;
        }

        if(product.isBelowReorderLevel()){
            System.out.println("ALERT: " + product.name + " (" + product.getProductId()
                    + ") quantity " + product.getQuantity()
                    + " is below reorder level " + product.reorderLevel + ".");
        }
    }

    public static void showInventoryAlerts(){
        boolean hasAlerts = false;
        for(Product product: Inventory.availableProducts){
            if(product != null && product.isBelowReorderLevel()){
                showAlert(product);
                hasAlerts = true;
            }
        }
        if(!hasAlerts){
            System.out.println("No stock alerts.");
        }
    }
}
