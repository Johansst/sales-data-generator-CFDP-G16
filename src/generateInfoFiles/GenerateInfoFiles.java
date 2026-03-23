package generateInfoFiles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class GenerateInfoFiles {

    private static final String[] NAMES = {
        "Juan", "Carlos", "José", "Luis", "Miguel", 
        "Andrés", "Pedro", "Jorge", "Fernando", "Ricardo", 
        "Alfonso", "Camilo", "Bartolomé", "Jesús", "Roberto", 
        "Alex", "Pedronel", "Diego", "Harrison", "David"
    };

    private static final String[] LAST_NAMES = {
        "Gómez", "Pérez", "Rodríguez", "Martínez", "López", 
        "Hernández", "García", "Ramírez", "Torres", "Sánchez", 
        "Castillo", "Morales", "Vargas", "Rojas", "Mendoza", 
        "Silva", "Ortega", "Navarro", "Cruz", "Delgado"
    };
    
    private static final String[] DOCUMENT_TYPES = {
            "C.C.", "C.E.", "P.T."
    };
    
    private static final String[] PRODUCT_NAMES = {
    	    "Shampoo", "Jabón", "Crema", "Perfume", "Desodorante",
    	    "Gel", "Loción", "Maquillaje", "Protector solar", "Acondicionador",
    	    "Talco", "Aceite corporal", "Exfoliante", "Mascarilla facial", "Tónico",
    	    "Serum", "Espuma limpiadora", "Pasta dental", "Enjuague bucal", "Hilo dental",
    	    "Crema de manos", "Crema para pies", "Bálsamo labial", "Aftershave", "Cera para cabello",
    	    "Spray fijador", "Toallitas húmedas", "Gel antibacterial", "Crema antiarrugas", "Colonia"
    };

    private static List<Long> generatedDocuments = new ArrayList<>();
    
    
    /**
     * Main method that generates all input files needed for the sales report program.
     * 1. Generates a file with random vendor information.
     * 2. Generates a file with random product information.
     * 3. Generates a file with random sales
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
    	
    	createSalesmanInfoFile(10); 							//1. Generates vendors
        createProductsFile(10); 								//2. Generates products
        createSalesFile(10);		                            //3. Generates sells 
        
    }
    
 
    /**
     * Generates a full random name with two names and two last names.
     * 
     * @return String with format "Name1 Name2; Last name1 Last name2"
     */
    public static String generateRandomName() {
        String names = getRandomElementFromArray(NAMES) + " " + getRandomElementFromArray(NAMES);
        String lastNames = getRandomElementFromArray(LAST_NAMES) + " " + getRandomElementFromArray(LAST_NAMES);
        return names + "; " + lastNames;
    }


    /**
     * Picks a random element from an array who has been sent through parameter.
     * @param array the array to pick a random element from
     * @return a random String from the given array
     */
    public static String getRandomElementFromArray(String[] array) {
    	
        int randomNumber = (int)(Math.random() * array.length);
        return array[randomNumber];
        
    }
    
    /**
     * Generates a random document number.
     * 
     * @return long 
     */
    public static long generateRandomDocument() {
    	
        return (long)(Math.random() * 10000000);
    }
    
    /**
     * Gets a unique document number calling generateRandomDocument and saving it in generatedDocuments it it doesn't exist.
     * 
     * @return long unique number
     */
    public static long getUniqueDocument() {
        long id;
        do {
            id = generateRandomDocument();
        } while (generatedDocuments.contains(id));
        generatedDocuments.add(id);
        return id;
    }
    
    
    
    /**
     * Creates a text file with random vendors according to the number received from parameter 
     * 
     * @param salesmanCount number of vendors to generate
     */
    public static void createSalesmanInfoFile( int salesmanCount ) {
    	try (PrintWriter pw = new PrintWriter("SalesmanInfoFile.csv")) {
        	for(int i = 0; i < salesmanCount; i++) {
        		String documentType = getRandomElementFromArray(DOCUMENT_TYPES);
        		long id = getUniqueDocument();
        		String fullName = generateRandomName();
        		
                pw.println(documentType+"; "+id + "; " + fullName);
        	}
        	System.out.println("Salesman info file generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the Salesman info file\n"+e.toString());
        }

    }
    
    /**
     * Creates a text file with random products according to the number received from parameter 
     * 
     * @param productsCount number of products to generate
     */
    public static void createProductsFile(int productsCount) {
        try (PrintWriter pw = new PrintWriter("Products.csv")) {
            for (int i = 1; i <= productsCount; i++) {
                String productName = getRandomElementFromArray(PRODUCT_NAMES);
                int price = (int)(Math.random() * 50000) + 5000;

                pw.println(i + "; " + productName + "; " + price);
            }
            System.out.println("Products file generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the products file\n"+e.toString());
        }
    }
    

    public static void createSalesFile(int salesPerSalesman){
	
	    HashMap<Integer, Integer> products = new HashMap<>();
	    List<String[]> salesmen = new ArrayList<>();
	
	    // 🔹 Read products (id + price)
	    try (BufferedReader br = new BufferedReader(new FileReader("Products.csv"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(";");
	            int productId = Integer.parseInt(data[0].trim());
	            int price = Integer.parseInt(data[2].trim());
	            products.put(productId, price);
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading products file");
	        e.printStackTrace();
	    }
	
	    List<Integer> productIds = new ArrayList<>(products.keySet());
	
	    // Read salesman
	    try (BufferedReader br = new BufferedReader(new FileReader("SalesmanInfoFile.csv"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(";");
	            String id = data[1].trim();
	            String name = data[2].trim() + " " + data[3].trim();
	            salesmen.add(new String[]{id, name});
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading salesmen file");
	        e.printStackTrace();
	    }
	
	    // Validation
		if (products.isEmpty() || salesmen.isEmpty()) {
		    System.out.println("No data available to generate sales.");
		    return;
		}
	
	    
	    // Generate sales
	    try (PrintWriter pw = new PrintWriter("Sales.csv")) {
	
	        for (String[] salesman : salesmen) {
	
	            String id = salesman[0];
	            String name = salesman[1];
	
	            for (int i = 0; i < salesPerSalesman; i++) {
	
	                int productId = productIds.get((int)(Math.random() * productIds.size()));
	                int quantity = (int)(Math.random() * 5) + 1;
	
	                int price = products.get(productId);
	                int total = price * quantity;
	
	                pw.println(id + "; " + name + "; " + productId + "; " + quantity + "; " + total);
	            }
	        }
	
	        System.out.println("Sales file generated successfully!");
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
