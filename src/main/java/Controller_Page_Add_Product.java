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
import objects.Part;
import objects.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Page_Add_Product implements Initializable {
    public ObservableList<Part> productParts = FXCollections.observableArrayList();
    public Controller_Page_Main controller_page_main;
    private int index;
    private Boolean modifyProduct = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateCalcPrice();
        productParts.addListener(new ListChangeListener<Part>() {
            @Override
            public void onChanged(Change<? extends Part> change) {
                updateCalcPrice();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Product
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected TextField product_id = new TextField();
    @FXML
    protected TextField product_name = new TextField();
    @FXML
    protected TextField product_inv = new TextField();
    @FXML
    protected TextField product_price = new TextField();
    @FXML
    protected TextField product_min = new TextField();
    @FXML
    protected TextField product_max = new TextField();

    public void loadData(int productNumber) {
        product_id.setText(String.valueOf(productNumber));
    }

    public void loadData(Product product) {
        product_id.setText(String.valueOf(product.getId()));
        product_name.setText(String.valueOf(product.getName()));
        product_price.setText(String.valueOf(product.getPrice()));
        product_inv.setText(String.valueOf(product.getStock()));
        product_min.setText(String.valueOf(product.getMin()));
        product_max.setText(String.valueOf(product.getMax()));
        updatePartData();
        modifyProduct = true;
    }

    private double calcProductPrice() {
        double totalPrice = 0.0;
        for (Part part : productParts) {
            totalPrice += part.getPrice();
        }

        //Round
        totalPrice *= 100;
        totalPrice = Math.round(totalPrice);
        totalPrice /= 100;

        System.out.println(totalPrice);
        return totalPrice;
    }

    private void updateCalcPrice() {
        product_calcPrice.setText(Double.toString(calcProductPrice()));
    }

    private Boolean validateUserInput() {
        //id: no need, autogenerated

        //name
        if (product_name.getText().isEmpty()) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MUST_HAVE_A_NAME);
            return false;
        }

        //price
        try {
            Double.parseDouble(product_price.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_PRICE_MUST_BE_A_NUMBER);
            return false;
        }

        //stock
        try {
            Integer.parseInt(product_inv.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_INVENTORY_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }

        //min
        try {
            Integer.parseInt(product_min.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MINIMUM_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }
        if (Integer.parseInt(product_min.getText()) <= 0) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MINIMUM_MUST_BE_AT_LEAST_0);
            return false;
        }

        //max
        try {
            Integer.parseInt(product_max.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MAXIMUM_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }
        if (Integer.parseInt(product_max.getText()) <= 1) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MAX_MUST_BE_AT_LEAST_1);
            return false;
        }

        //Ensure that minimum is less than maximum
        //Can assume that values are valid as check was already done
        if (Integer.parseInt(product_min.getText()) >= Integer.parseInt(product_max.getText())) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MINIMUM_MUST_BE_LESS_THAN_MAX);
            return false;
        }

        if (productParts.isEmpty()) {
            Dialogs.showBasicDialog(Strings.ERROR_VALIDATION_ERROR, Strings.ERROR_PRODUCT_MUST_HAVE_1_PART);
            return false;
        }

        return true;
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
    protected TextField product_calcPrice = new TextField();

    @FXML
    private void searchUpdate() {
        updatePartData();
    }

    //This method is derived from the Inventory method, but is made more specific for the individual product instance
    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> partList = FXCollections.observableArrayList();
        for (Part part : productParts) {
            if (part.getName().toUpperCase().contains(partName.toUpperCase())) {
                partList.add(part);
            }
        }
        return partList;
    }

    public void updatePartData() {
        part_id.setCellValueFactory(new PropertyValueFactory<>(Strings.ID));
        part_name.setCellValueFactory(new PropertyValueFactory<>(Strings.NAME));
        part_inv.setCellValueFactory(new PropertyValueFactory<>(Strings.STOCK));
        part_price.setCellValueFactory(new PropertyValueFactory<>(Strings.PRICE));
        partTable.getItems().setAll(lookupPart(part_search.getText()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Buttons
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void button_add() {
        if (isAddPartWindowOpen()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_PARTS));
            Parent root = loader.load();

            Controller_Page_Parts controller = loader.getController();
            controller.controller_page_add_product = this;
            controller.updateData();

            addPartStage = new Stage();
            addPartStage.setScene(new Scene(root));
            addPartStage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void button_delete() {
        if (partTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog(Strings.ERROR_SELECTION_ERROR, Strings.ERROR_PLEASE_SELECT_A_PART_TO_DELETE);
            return;
        }

        Part part = partTable.getSelectionModel().getSelectedItem();
        productParts.remove(part);
        updatePartData();
    }

    @FXML
    public void button_save() {
        if (!validateUserInput()) return;

        //Gets input
        int id = Integer.parseInt(product_id.getText());
        String name = product_name.getText();
        double price = Double.parseDouble(product_price.getText());
        int stock = Integer.parseInt(product_inv.getText());
        int min = Integer.parseInt(product_min.getText());
        int max = Integer.parseInt(product_max.getText());

        Product product = new Product(id, name, price, stock, min, max);
        productParts.forEach(product::addAssociatedPart);

        if (modifyProduct) {
            Main.inventory.updateProduct(getIndex(), product);
        } else {
            Main.inventory.addProduct(product);
        }

        Stage stage = (Stage) vBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void button_cancel() {
        if (isAddPartWindowOpen()) {
            addPartStage.close();
        }
        closeWindow();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Buttons
    ///////////////////////////////////////////////////////////////////////////////////////
    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Basic window functionality and features
    ///////////////////////////////////////////////////////////////////////////////////////
    protected Stage addPartStage;

    public Boolean isAddPartWindowOpen() {
        try {
            if (addPartStage.isShowing()) {
                addPartStage.requestFocus();
                return true;
            }
        } catch (Exception exception) {
            exception.getMessage();
        }
        return false;
    }

    private void closeWindow() {
        Stage stage = (Stage) vBox.getScene().getWindow();
        Dialogs.confirmCancelDialog(stage);
    }

    @FXML
    protected VBox vBox = new VBox();

    @FXML
    private void unFocus() {
        vBox.requestFocus();
    }
}
