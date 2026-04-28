package generateInfoFiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Main {

    /**
     * Main method that reads the generated files and produces the sales reports.
     * 1. Reads the salesmen info file.
     * 2. Calculates total sales per vendor.
     * 3. Generates a sales report ordered from highest to lowest.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        List<String[]> salesmen = getSalesmen();
        if (!salesmen.isEmpty()) {
            List<String[]> salesmenWithTotals = calculateTotals(salesmen);
            generateReport(salesmenWithTotals);
            generateProductsReport(salesmen);
        } else {
            System.out.println("No salesmen found. Make sure SalesmanInfoFile.csv exists.");
        }
    }

    /**
     * Reads the salesmen info file and returns a list with each vendor's data.
     *
     * @return List of String arrays with format [documentType, id, name]
     */
    public static List<String[]> getSalesmen() {
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

        return salesmen;
    }

    /**
     * Loads product prices from the products file.
     *
     * @return HashMap with product ID as key and price as value
     */
    public static HashMap<Integer, Integer> loadProducts() {
        HashMap<Integer, Integer> products = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Products.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                int id = Integer.parseInt(data[0].trim());
                int price = Integer.parseInt(data[2].trim());
                products.put(id, price);
            }
            System.out.println("Products file read successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the products file\n" + e.toString());
        }

        return products;
    }

    /**
     * Calculates total sales for each vendor by reading their sales files.
     *
     * @param salesmen list of vendors with their data
     * @return List of String arrays with format [documentType, id, name, totalSales]
     */
    public static List<String[]> calculateTotals(List<String[]> salesmen) {
        HashMap<Integer, Integer> products = loadProducts();
        List<String[]> salesmenWithTotals = new ArrayList<>();

        for (String[] salesman : salesmen) {
            String id = salesman[1];
            String name = salesman[2];
            String documentType = salesman[0];
            int totalSales = 0;

            try (BufferedReader br = new BufferedReader(new FileReader("Sales_" + id + ".csv"))) {
                String line;
                boolean firstLine = true;

                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(";");
                    int productId = Integer.parseInt(data[0].trim());
                    int quantity = Integer.parseInt(data[1].trim());
                    Integer price = products.get(productId);
                    if (price == null) {
                        System.out.println("Product ID " + productId + " not found, skipping.");
                        continue;
                    }
                    totalSales += price * quantity;
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("An error occurred while reading sales file for " + name + "\n" + e.toString());
            }

            System.out.println("Total sales for " + name + ": $" + totalSales);
            salesmenWithTotals.add(new String[]{documentType, id, name, String.valueOf(totalSales)});
        }

        return salesmenWithTotals;
    }

    /**
     * Generates a CSV sales report ordered from highest to lowest total sales.
     *
     * @param salesmenWithTotals list of vendors including their total sales
     */
    public static void generateReport(List<String[]> salesmenWithTotals) {

        Collections.sort(salesmenWithTotals, new Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                return Integer.parseInt(b[3]) - Integer.parseInt(a[3]);
            }
        });

        try (PrintWriter pw = new PrintWriter("SalesReport.csv")) {
            for (String[] salesman : salesmenWithTotals) {
                pw.println(salesman[1] + ";" + salesman[2] + ";" + salesman[3]);
            }
            System.out.println("Sales report generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while generating the sales report\n" + e.toString());
        }
    }

public static void generateProductsReport(List<String[]> salesmen) {

    HashMap<Integer, Integer> productTotals = new HashMap<>();
    HashMap<Integer, String> productNames = new HashMap<>();

    // 🔹 Read products (name + id)
    try (BufferedReader br = new BufferedReader(new FileReader("Products.csv"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            int id = Integer.parseInt(data[0].trim());
            String name = data[1].trim();

            productNames.put(id, name);
            productTotals.put(id, 0);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 🔹 Read sells from every salesmen 
    for (String[] salesman : salesmen) {

        String id = salesman[1];

        try (BufferedReader br = new BufferedReader(new FileReader("Sales_" + id + ".csv"))) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(";");
                int productId = Integer.parseInt(data[0].trim());
                int quantity = Integer.parseInt(data[1].trim());

                productTotals.put(productId,
                        productTotals.getOrDefault(productId, 0) + quantity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Convert to list for ordering
    List<int[]> productList = new ArrayList<>();

    for (Integer id : productTotals.keySet()) {
        productList.add(new int[]{id, productTotals.get(id)});
    }

    // 🔹 Ordering DESC
    Collections.sort(productList, new Comparator<int[]>() {
        public int compare(int[] a, int[] b) {
            return b[1] - a[1];
        }
    });

    // 🔹 Creating file
    try (PrintWriter pw = new PrintWriter("ProductsReport.csv")) {

        pw.println("Producto;CantidadVendida");

        for (int[] p : productList) {
            String name = productNames.get(p[0]);
            pw.println(name + ";" + p[1]);
        }

        System.out.println("Products report generated successfully!");

    } catch (IOException e) {
        e.printStackTrace();
    }
}

}