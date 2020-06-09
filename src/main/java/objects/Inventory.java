package objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Inventory {

    private ObservableList<Part> allParts = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    private int genPartNumber = 0;

    public int getGenPartNumber() {
        return genPartNumber;
    }

    private int genProductNumber = 0;

    public int genGenProductNumber() {
        return genProductNumber;
    }

    public Inventory() {
    }

    public void generateDummyData() {
        Part part0 = new InHouse(getGenPartNumber(), "Screw AF12", .53, 2, 2, 8, 0);
        addPart(part0);
        Part part1 = new InHouse(getGenPartNumber(), "4' Screw - DF3", .54, 3, 4, 16, 1);
        addPart(part1);
        Part part2 = new InHouse(getGenPartNumber(), "9mm Lid", .55, 4, 6, 32, 2);
        addPart(part2);
        Part part3 = new InHouse(getGenPartNumber(), "Wood Glue", .56, 5, 8, 64, 3);
        addPart(part3);
        Part part4 = new Outsourced(getGenPartNumber(), "Plywood - 4x4", .535, 12, 12, 18, "Home Depot");
        addPart(part4);
        Part part5 = new Outsourced(getGenPartNumber(), "Paint - 2L - Green", .545, 13, 14, 116, "Lowes");
        addPart(part5);
        Part part6 = new Outsourced(getGenPartNumber(), "Paint - 2L - Red", .555, 14, 16, 132, "Lowes");
        addPart(part6);
        Part part7 = new Outsourced(getGenPartNumber(), "Customer Couplers - AJ2132", .565, 15, 18, 164, "AMA");
        addPart(part7);

        Product product0 = new Product(genGenProductNumber(), "Cat stair case", 0.01, 1, 1, 21);
        product0.addAssociatedPart(part0);
        product0.addAssociatedPart(part1);
        addProduct(product0);

        Product product1 = new Product(genGenProductNumber(), "Julian Knight Kitchen Appliance", 1.01, 11, 11, 121);
        product1.addAssociatedPart(part0);
        addProduct(product1);

        Product product2 = new Product(genGenProductNumber(), "WGU Project Starter Kit", 11.01, 111, 311, 3121);
        product2.addAssociatedPart(part3);
        product2.addAssociatedPart(part1);
        product2.addAssociatedPart(part6);
        product2.addAssociatedPart(part3);
        addProduct(product2);
    }

    public void addPart(Part newPart) {
        allParts.add(newPart);
        genPartNumber++;
    }

    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
        genProductNumber++;
    }

    public Part lookupPart(int partId) throws Exception {
        for (Part part : getAllParts()) {
            if (part.getId() == partId) {
                return part;
            }
        }
        throw new Exception("Critical error: Part ID not found.");
    }

    public Product lookupProduct(int productId) throws Exception {
        for (Product product : getAllProducts()) {
            if (product.getId() == productId) {
                return product;
            }
        }
        throw new Exception("Critical error: Product ID not found.");
    }

    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> partList = FXCollections.observableArrayList();
        for (Part part : getAllParts()) {
            if (part.getName().toUpperCase().contains(partName.toUpperCase())) {
                partList.add(part);
            }
        }
        return partList;
    }

    public ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> productList = FXCollections.observableArrayList();
        for (Product product : getAllProducts()) {
            if (product.getName().toUpperCase().contains(productName.toUpperCase())) {
                productList.add(product);
            }
        }
        return productList;
    }

    public void updatePart(int index, Part newPart) {
        getAllParts().set(index, newPart);
    }

    public void updateProduct(int index, Product newProduct) {
        getAllProducts().set(index, newProduct);
    }

    public Boolean deletePart(Part selectedPart) {
        for (Product product : getAllProducts()) {
            if (product.getAllAssociatedParts().contains(selectedPart)) {
                return false;
            }
        }

        getAllParts().remove(selectedPart);

        return true;
    }

    public Boolean deleteProduct(Product selectedProduct) {
        getAllProducts().remove(selectedProduct);
        return true;
    }

    public ObservableList<Part> getAllParts() {
        return allParts;
    }

    public ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
