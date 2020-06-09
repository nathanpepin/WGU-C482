import constants.FxmlLocations;
import constants.Strings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.InHouse;
import objects.Outsourced;
import objects.Part;
import objects.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Page_Main implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateData();

        Main.inventory.getAllProducts().addListener((ListChangeListener<Product>) change -> updateData());
        Main.inventory.getAllParts().addListener((ListChangeListener<Part>) change -> updateData());
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Window control: ensures that user is not adding more than one part or product at a time
    ///////////////////////////////////////////////////////////////////////////////////////
    protected Stage secondWindow;

    private Boolean isSecondWindowOpen() {
        try {
            if (secondWindow.isShowing()) {
                secondWindow.requestFocus();
                return true;
            }
        } catch (Exception exception) {
            exception.getMessage();
        }
        return false;
    }

    private void openSecondWindow(Parent root) {
        if (isSecondWindowOpen()) return;

        secondWindow = new Stage();
        secondWindow.setScene(new Scene(root));
        secondWindow.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Part
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected TableView<Part> partTable = new TableView<>();
    @FXML
    protected TableColumn<Part, String> part_id = new TableColumn<>(Strings.PART_ID);
    @FXML
    protected TableColumn<Part, String> part_name = new TableColumn<>(Strings.PART_NAME);
    @FXML
    protected TableColumn<Part, String> part_inv = new TableColumn<>(Strings.PART_INV);
    @FXML
    protected TableColumn<Part, String> part_price = new TableColumn<>(Strings.PART_PRICE);

    @FXML
    protected TextField part_search = new TextField();

    @FXML
    private void createPage_addPart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_ADD_PART_FXML));
            Parent root = loader.load();

            Controller_Page_Add_Part addPartController = loader.getController();
            addPartController.controller_page_main = this;
            addPartController.loadData(Main.inventory.getGenPartNumber());

            openSecondWindow(root);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @FXML
    private void createPage_modifyPart() {
        if (partTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog(Strings.ERROR_SELECTION_ERROR, Strings.ERROR_PLEASE_SELECT_A_PART_TO_MODIFY);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_ADD_PART_FXML));
            Parent root = loader.load();

            Controller_Page_Add_Part controller = loader.getController();
            controller.controller_page_main = this;

            Part part = partTable.getSelectionModel().getSelectedItem();
            int index = partTable.getSelectionModel().getSelectedIndex();
            controller.setIndex(index);

            if (part instanceof InHouse) {
                controller.loadData((InHouse) part);
            } else {
                controller.loadData((Outsourced) part);
            }

            openSecondWindow(root);
        } catch (IOException err) {
            System.out.println(err.toString());
        }
    }

    @FXML
    private void createDialogDeletePart() {
        if (partTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog(Strings.ERROR_SELECTION_ERROR, Strings.ERROR_PLEASE_SELECT_A_PART_TO_DELETE);
        }

        Part part = partTable.getSelectionModel().getSelectedItem();
        Dialogs.deletePartDialog(part);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Product
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected TableView<Product> productTable = new TableView<>();
    @FXML
    protected TableColumn<Product, String> product_id = new TableColumn<>(Strings.PRODUCT_ID);
    @FXML
    protected TableColumn<Product, String> product_name = new TableColumn<>(Strings.PRODUCT_NAME);
    @FXML
    protected TableColumn<Product, String> product_inv = new TableColumn<>(Strings.PRODUCT_INV);
    @FXML
    protected TableColumn<Product, String> product_price = new TableColumn<>(Strings.PRODUCT_PRICE);

    @FXML
    protected TextField product_search = new TextField();

    @FXML
    private void createPage_addProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_ADD_PRODUCT_FXML));
            Parent root = loader.load();

            Controller_Page_Add_Product controller = loader.getController();
            controller.controller_page_main = this;
            controller.loadData(Main.inventory.genGenProductNumber());

            openSecondWindow(root);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @FXML
    private void createPage_modifyProduct() {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog(Strings.ERROR_SELECTION_ERROR, Strings.ERROR_PLEASE_SELECT_A_PRODUCT_TO_MODIFY);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_ADD_PRODUCT_FXML));
            Parent root = loader.load();

            Controller_Page_Add_Product controller = loader.getController();
            controller.controller_page_main = this;

            Product product = productTable.getSelectionModel().getSelectedItem();

            //Given that the parts details may change, it is important to cross reference via ID to the real table instead of the main
            //It would make more sense the product to hold an array of part_id's, but that's not what the UML model calls for, so this is the work around
            ObservableList<Part> updatedParts = FXCollections.observableArrayList();
            for (Part predatedPart : product.getAllAssociatedParts()) {
                updatedParts.add(Main.inventory.lookupPart(predatedPart.getId()));
            }
            controller.productParts = updatedParts;
            controller.loadData(product);

            int index = productTable.getSelectionModel().getSelectedIndex();
            controller.setIndex(index);

            openSecondWindow(root);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void createDialogDeleteProduct() {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog(Strings.ERROR_SELECTION_ERROR, Strings.ERROR_PLEASE_SELECT_A_PRODUCT_TO_DELETE);
            return;
        }

        Product product = productTable.getSelectionModel().getSelectedItem();
        Dialogs.deleteProductDialog(product);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Data binding
    ///////////////////////////////////////////////////////////////////////////////////////
    private void updateData() {
        updatePartData();
        updateProductData();
    }

    @FXML
    private void updatePartData() {
        part_id.setCellValueFactory(new PropertyValueFactory<>(Strings.ID));
        part_name.setCellValueFactory(new PropertyValueFactory<>(Strings.NAME));
        part_inv.setCellValueFactory(new PropertyValueFactory<>(Strings.STOCK));
        part_price.setCellValueFactory(new PropertyValueFactory<>(Strings.PRICE));
        partTable.getItems().setAll(Main.inventory.lookupPart(part_search.getText()));
    }

    @FXML
    private void updateProductData() {
        product_id.setCellValueFactory(new PropertyValueFactory<>(Strings.ID));
        product_name.setCellValueFactory(new PropertyValueFactory<>(Strings.NAME));
        product_inv.setCellValueFactory(new PropertyValueFactory<>(Strings.STOCK));
        product_price.setCellValueFactory(new PropertyValueFactory<>(Strings.PRICE));
        productTable.getItems().setAll(Main.inventory.lookupProduct(product_search.getText()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Basic window functionality and features
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void event_exit() {
        Dialogs.exitDialog();
    }

    @FXML
    protected VBox vBox = new VBox();

    @FXML
    private void unFocus() {
        vBox.requestFocus();
    }
}
