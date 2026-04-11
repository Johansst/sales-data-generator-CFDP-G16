package generateInfoFiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    
    private static final int NUMBER_SALESMAN = 8, NUMBER_PRODUCTS = 20; 

    /**
     * Main method that generates all input files needed for the sales report program.
     * 1. Generates a file with random vendor information.
     * 2. Generates a file with random product information.
     * 3. Reads the vendors file and generates a sales file for each vendor.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        createSalesmanInfoFile(NUMBER_SALESMAN);
        createProductsFile(NUMBER_PRODUCTS);
        generateSalesFilesFromVendors(NUMBER_SALESMAN);
    }

    /**
     * Reads the salesmen info file and generates a sales file for each vendor found.
     */
    public static void generateSalesFilesFromVendors(int numberSalesman) {
        List<String[]> salesmen = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("SalesmanInfoFile.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String documentType = data[0].trim();
                String id = data[1].trim();
                String name = data[2].trim() + " " + data[3].trim();
                salesmen.add(new String[]{documentType, id, name});
            }
            System.out.println("Salesmen file read successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the salesmen file\n" + e.toString());
        }

        for (String[] salesman : salesmen) {
            createSalesMenFile(numberSalesman, salesman[2], Long.parseLong(salesman[1]));
        }
    }

    /**
     * Generates a full random name with two names and two last names.
     *
     * @return String with format "Name1 Name2;Lastname1 Lastname2"
     */
    public static String generateRandomName() {
        String names = getRandomElementFromArray(NAMES) + " " + getRandomElementFromArray(NAMES);
        String lastNames = getRandomElementFromArray(LAST_NAMES) + " " + getRandomElementFromArray(LAST_NAMES);
        return names + ";" + lastNames;
    }

    /**
     * Picks a random element from an array sent through parameter.
     *
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
     * @return long random number
     */
    public static long generateRandomDocument() {
        return (long)(Math.random() * 10000000);
    }

    /**
     * Gets a unique document number by calling generateRandomDocument
     * and verifying it does not already exist in generatedDocuments.
     *
     * @return long unique document number
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
     * Creates a CSV file with random vendor information.
     *
     * @param salesmanCount number of vendors to generate
     */
    public static void createSalesmanInfoFile(int salesmanCount) {
        try (PrintWriter pw = new PrintWriter("SalesmanInfoFile.csv")) {
            for (int i = 0; i < salesmanCount; i++) {
                String documentType = getRandomElementFromArray(DOCUMENT_TYPES);
                long id = getUniqueDocument();
                String fullName = generateRandomName();
                pw.println(documentType + ";" + id + ";" + fullName);
            }
            System.out.println("Salesman info file generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the salesman info file\n" + e.toString());
        }
    }

    /**
     * Creates a CSV file with random product information.
     *
     * @param productsCount number of products to generate
     */
    public static void createProductsFile(int productsCount) {
        try (PrintWriter pw = new PrintWriter("Products.csv")) {
            for (int i = 1; i <= productsCount; i++) {
                String productName = getRandomElementFromArray(PRODUCT_NAMES);
                int price = (int)(Math.random() * 50000) + 5000;
                pw.println(i + ";" + productName + ";" + price);
            }
            System.out.println("Products file generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the products file\n" + e.toString());
        }
    }

    /**
     * Creates a CSV file with random sales for a given vendor.
     * The first line contains the vendor's document type and ID.
     * Each subsequent line contains a product ID and quantity sold.
     *
     * @param randomSalesCount number of sales to generate
     * @param name             vendor's full name
     * @param id               vendor's document number
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        try (PrintWriter pw = new PrintWriter("Sales_" + id + ".csv")) {
            pw.println(getRandomElementFromArray(DOCUMENT_TYPES) + ";" + id);
            for (int i = 0; i < randomSalesCount; i++) {
                int productId = (int)(Math.random() * 10) + 1;
                int quantity = (int)(Math.random() * 5) + 1;
                pw.println(productId + ";" + quantity + ";");
            }
            System.out.println("Sales file for " + name + " generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating sales file for " + name + "\n" + e.toString());
        }
    }
}