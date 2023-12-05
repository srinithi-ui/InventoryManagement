
import java.io.*;
import java.util.*;
class Product extends Thread{
    String productId;
    String productName;
    int productPrice;
    int productQuantity;
    Product(){};
    Product(String pid, String pname, int pprice, int pquant){
        productId = pid;
        productName = pname;
        productPrice = pprice;
        productQuantity = pquant;


    }

}
class DefinedException extends Exception{
    public DefinedException(String str)
    {

        super(str);
    }
}

class Inventory extends Product{
    Product product = new Product();

    List<Product> productList = new ArrayList<Product>();
    String currentContext = "";
    void read() throws FileNotFoundException, IOException{
        try {
            FileReader reader = new FileReader("src/Products.txt");
            int iterator;

            while ((iterator = reader.read()) != -1) {
                if(iterator == 10) currentContext += ',';
                else currentContext += (char) iterator;
            }
        }

        catch (FileNotFoundException exception){
            System.out.println("File not found");
        }
        System.out.println(currentContext);
        add(currentContext);
    }
    void add(String productDetails){
        String[] commaSeparation = productDetails.split(",");
        int count = 0;
        while(count < commaSeparation.length){
            Product item = new Product(commaSeparation[count], commaSeparation[count+1], Integer.parseInt(commaSeparation[count+2]),Integer.parseInt(commaSeparation[count+3]));
            count += 4;
            productList.add(item);
        }


    }
    void displayDetails(){
        for(int print = 0; print < productList.size(); print++){

            System.out.println("ProductID:"+productList.get(print).productId +"\n"+"ProductName : "+productList.get(print).productName +"\n"+"ProductPrice : "+productList.get(print).productPrice+"\n"+"ProductQuantity : "+productList.get(print).productQuantity+"\n");
            System.out.println();
        }
    }

    synchronized void update(String pid, int pquant) throws IOException, InterruptedException{
        String oldValue = "", newValue = "";
        for(int print = 0; print < productList.size(); print++) {

            if (productList.get(print).productId.equals(pid)) {
                oldValue = productList.get(print).productId +","+ productList.get(print).productName + ","+productList.get(print).productPrice+","+productList.get(print).productQuantity;

                if(productList.get(print).productQuantity < pquant){
                    currentThread().sleep(10000);
                }
                else productList.get(print).productQuantity -= pquant;

                newValue = productList.get(print).productId + ","+productList.get(print).productName + ","+productList.get(print).productPrice+","+productList.get(print).productQuantity;

                String purchased = "ProductID : " + productList.get(print).productId + " is sold";
               try{
                    FileWriter writer = new FileWriter("src/productlogs.txt", true);
                    writer.write(purchased+"\n");
                    writer.close();
                }
                catch (Exception e){
                    System.out.println(e);
                }
                System.out.println("Logs Done");
            }
        }
        updateInventory(oldValue, newValue);
        displayDetails();

    }
    void updateInventory(String oldValue, String newValue) throws IOException{
        String filePath = "src/Products.txt";
        Scanner scanner = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (scanner.hasNextLine()) {
            buffer.append(scanner.nextLine()+"\n");
        }
        String fileContents = buffer.toString();

        fileContents = fileContents.replaceAll(oldValue, newValue);

        FileWriter writer = new FileWriter(filePath);

        writer.append(fileContents);
        writer.flush();

    }
    void priceTotal(){
        int price = 0;
        for(int print = 0; print < productList.size(); print++){

           price += productList.get(print).productQuantity * productList.get(print).productPrice;
        }
        try{
            FileWriter writer = new FileWriter("src/Report.txt", true);
            writer.write("the total amount in inventory :"+price+"\n");
            writer.close();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }
    void priceSpecific(String pid){
        int price = 0;
        for(int print = 0; print < productList.size(); print++){
            if(productList.get(print).productId.equals(pid))
            price += productList.get(print).productQuantity * productList.get(print).productPrice;
        }
        System.out.println("the total amount of :"+pid+" is "+price+"\n");
        try{
            FileWriter writer = new FileWriter("src/reportSpecific.txt", true);
            writer.write("the total amount of :"+pid+" is "+price+"\n");
            writer.close();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }




}


public class Main {

    public static void main(String[] args) throws IOException,InterruptedException {


        Scanner userInput = new Scanner(System.in);
        Product productObj = new Product();
        Inventory inventoryObj = new Inventory();


        while(true) {
            System.out.println("Enter the option : \n 1.Read the products \n 2.Purchase a product \n 3.Report \n 4.Quit");
            int option = userInput.nextInt();
            switch (option) {
                case 1:


                    inventoryObj.read();
                    inventoryObj.displayDetails();
                    break;
                case 2:

                        System.out.println("Enter the product id to purchase");
                        String pid = userInput.next();
                        try{
                            if(pid.length() < 5 ){
                                throw new DefinedException("Provide id with P-ID");
                            }
                            else{
                                System.out.println("Enter the number of products to purchase");
                                int pquant = userInput.nextInt();
                                inventoryObj.update(pid, pquant);
                            }
                        }
                        catch (DefinedException de){
                            System.out.println(de);
                        }

                    break;
                case 3:
                    System.out.println("Enter 1 to get total price of inventory \nEnter 2 to get a price of a specific product");
                    int check = userInput.nextInt();
                    if(check == 1){
                        inventoryObj.priceTotal();
                    }
                    {
                        System.out.println("Enter the pid to know the price");
                         pid = userInput.next();
                        inventoryObj.priceSpecific(pid);
                    }

                    break;
                case 5:
                    break;


            }
        }
    }
}
