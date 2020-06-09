import constants.Strings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.InHouse;
import objects.Outsourced;
import objects.Part;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Page_Add_Part implements Initializable {
    private Boolean modifyPart = false;
    public Controller_Page_Main controller_page_main;
    private int index;

    ///////////////////////////////////////////////////////////////////////////////////////
    //Data loading and binding
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    protected TextField part_id = new TextField();
    @FXML
    protected TextField part_name = new TextField();
    @FXML
    protected TextField part_inventory = new TextField();
    @FXML
    protected TextField part_price = new TextField();
    @FXML
    protected TextField part_minimum = new TextField();
    @FXML
    protected TextField part_maximum = new TextField();
    @FXML
    protected TextField part_machineCompany = new TextField();
    //There is either a machine ID or a company name depending on the type and this will set it to the proper identifier
    @FXML
    protected Label label_machineCompany = new Label();
    @FXML
    protected Label label_addPart = new Label();

    //This method assumes that if only a part number is being loaded that a part is being added,
    //whereas the overloaded methods assume that a part is being modified
    public void loadData(int partNumber) {
        part_id.setText(String.valueOf(partNumber));
        label_addPart.setText(Strings.ADD_PART);
        radio_update();
    }

    public void loadData(InHouse part) {
        loadGenData(part);
        part_machineCompany.setText(String.valueOf(part.getMachineId()));
        label_machineCompany.setText(Strings.MACHINE_ID);
        inHouse = true;
        radio_update();
    }

    public void loadData(Outsourced part) {
        loadGenData(part);
        part_machineCompany.setText(String.valueOf(part.getCompanyName()));
        label_machineCompany.setText(Strings.COMPANY_NAME);
        inHouse = false;
        radio_update();
    }

    private void loadGenData(Part part) {
        part_id.setText(String.valueOf(part.getId()));
        part_name.setText(String.valueOf(part.getName()));
        part_price.setText(String.valueOf(part.getPrice()));
        part_inventory.setText(String.valueOf(part.getStock()));
        part_minimum.setText(String.valueOf(part.getMin()));
        part_maximum.setText(String.valueOf(part.getMax()));
        modifyPart = true;
        label_addPart.setText(Strings.MODIFY_PART);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Validation
    ///////////////////////////////////////////////////////////////////////////////////////
    private Boolean validateUserInput() {
        String errorTitle = Strings.ERROR_VALIDATION_ERROR;
        //id

        //name
        if (part_name.getText().isEmpty()) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MUST_HAVE_A_NAME);
            return false;
        }

        //price
        try {
            Double.parseDouble(part_price.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_PRICE_MUST_BE_A_NUMBER);
            return false;
        }

        //stock
        try {
            Integer.parseInt(part_inventory.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_INVENTORY_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }

        //min
        try {
            Integer.parseInt(part_minimum.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MINIMUM_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }
        if (Integer.parseInt(part_minimum.getText()) <= 0) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MINIMUM_MUST_BE_AT_LEAST_0);
            return false;
        }

        //max
        try {
            Integer.parseInt(part_maximum.getText());
        } catch (Exception exception) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MAXIMUM_MUST_BE_A_WHOLE_NUMBER);
            return false;
        }
        if (Integer.parseInt(part_maximum.getText()) <= 1) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MAX_MUST_BE_AT_LEAST_1);
            return false;
        }

        //Ensure that minimum is less than maximum
        //Can assume that values are valid as check was already done
        if (Integer.parseInt(part_minimum.getText()) >= Integer.parseInt(part_maximum.getText())) {
            Dialogs.showBasicDialog(errorTitle, Strings.ERROR_PART_MINIMUM_MUST_BE_LESS_THAN_MAX);
            return false;
        }

        //machineCompany
        //Only need to check InHouse because outsourced is a string
        if (inHouse) {
            try {
                Integer.parseInt(part_machineCompany.getText());
            } catch (Exception exception) {
                Dialogs.showBasicDialog(errorTitle, Strings.ERROR_MACHINE_ID_MUST_BE_A_WHOLE_NUMBER);
                return false;
            }
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Buttons
    ///////////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void button_save() {
        if (!validateUserInput()) return;

        //Gets input
        int id = Integer.parseInt(part_id.getText());
        String name = part_name.getText();
        double price = Double.parseDouble(part_price.getText());
        int stock = Integer.parseInt(part_inventory.getText());
        int min = Integer.parseInt(part_inventory.getText());
        int max = Integer.parseInt(part_maximum.getText());

        //Creates part object
        Part part;
        if (radioButton_inHouse.isSelected()) {
            int machineId = Integer.parseInt(part_machineCompany.getText());
            part = new InHouse(id, name, price, stock, min, max, machineId);
        } else {
            String companyName = part_machineCompany.getText();
            part = new Outsourced(id, name, price, stock, min, max, companyName);
        }

        if (modifyPart) {
            Main.inventory.updatePart(getIndex(), part);
        } else {
            Main.inventory.addPart(part);
        }

        Stage stage = (Stage) vBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void button_cancel() {
        closeWindow();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Radio buttons
    ///////////////////////////////////////////////////////////////////////////////////////
    private Boolean inHouse = true;
    @FXML
    protected RadioButton radioButton_inHouse = new RadioButton();
    @FXML
    protected RadioButton radioButton_outsourced = new RadioButton();

    @FXML
    private void radio_inHouse() {
        inHouse = true;
        radio_update();
    }

    @FXML
    private void radio_outsourced() {
        inHouse = false;
        radio_update();
    }

    public void radio_update() {
        radioButton_inHouse.setSelected(inHouse);
        radioButton_outsourced.setSelected(!inHouse);

        if (inHouse) {
            label_machineCompany.setText(Strings.MACHINE_ID);
            part_machineCompany.setPromptText(Strings.ENTER_THE_MACHINE_ID);
        } else {
            label_machineCompany.setText(Strings.COMPANY_NAME);
            part_machineCompany.setPromptText(Strings.ENTER_THE_COMPANY_MANUFACTURER);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Bulk getters and setters
    ///////////////////////////////////////////////////////////////////////////////////////
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Basic window functionality and features
    ///////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
