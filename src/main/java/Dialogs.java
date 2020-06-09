import constants.FxmlLocations;
import constants.Strings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import objects.Part;
import objects.Product;

import java.util.ArrayList;
import java.util.Optional;

public class Dialogs {
    public static void showBasicDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void deletePartDialog(Part part) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Strings.DELETE_PART);
        alert.setHeaderText(null);
        alert.setContentText(Strings.WOULD_YOU_LIKE_TO_DELETE_THIS_PART);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            if (!Main.inventory.deletePart(part)) {
                ArrayList<String> resultList = new ArrayList<>();
                Main.inventory.getAllProducts().forEach(
                        product -> {
                            if (product.getAllAssociatedParts().contains(part)) {
                                resultList.add(product.getName());
                            }
                        }
                );

                StringBuilder errorMessage = new StringBuilder(Strings.FAILED_TO_DELETE).append("\n");
                for (String name : resultList) {
                    errorMessage.append(name).append("\n");
                }

                showBasicDialog(Strings.ERROR, errorMessage.toString());
            }
        }
    }

    public static void deleteProductDialog(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Strings.DELETE_PRODUCT);
        alert.setHeaderText(null);
        alert.setContentText(Strings.WOULD_YOU_LIKE_TO_DELETE_THIS_PRODUCT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            Main.inventory.deleteProduct(product);
        }
    }

    public static void confirmCancelDialog(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Strings.CANCEL);
        alert.setHeaderText(null);
        alert.setContentText(Strings.WOULD_YOU_LIKE_TO_CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            stage.close();
        }
    }

    public static void exitDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle( Strings.EXIT);
        alert.setHeaderText(null);
        alert.setContentText(Strings.WOULD_YOU_LIKE_TO_EXIT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            System.exit(0);
        }
    }
}
