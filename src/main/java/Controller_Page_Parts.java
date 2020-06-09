import constants.Strings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Part;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Page_Parts implements Initializable {
    public Controller_Page_Add_Product controller_page_add_product;

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

    public void updateData() {
        part_id.setCellValueFactory(new PropertyValueFactory<>(Strings.ID));
        part_name.setCellValueFactory(new PropertyValueFactory<>(Strings.NAME));
        part_inv.setCellValueFactory(new PropertyValueFactory<>(Strings.STOCK));
        part_price.setCellValueFactory(new PropertyValueFactory<>(Strings.PRICE));
        partTable.getItems().setAll(Main.inventory.lookupPart(part_search.getText()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Search
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected TextField part_search = new TextField();

    @FXML
    private void searchUpdate() {
        updateData();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Buttons
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void button_add() {
        if (partTable.getSelectionModel().getSelectedItem() == null) {
            Dialogs.showBasicDialog("Selection Error", "Please select a part to modify.");
            return;
        }

        Part part = partTable.getSelectionModel().getSelectedItem();
        controller_page_add_product.productParts.add(part);
        controller_page_add_product.updatePartData();

        Stage stage = (Stage) vBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void button_cancel() {
        Stage stage = (Stage) vBox.getScene().getWindow();
        Dialogs.confirmCancelDialog(stage);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Window functionality
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected VBox vBox = new VBox();

    @FXML
    private void unFocus() {
        vBox.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
