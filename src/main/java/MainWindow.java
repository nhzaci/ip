import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import duke.Duke;

import java.util.concurrent.TimeUnit;

public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Duke duke;

    private Image userImage = new Image(this.getClass().getResourceAsStream("images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("images/DaDuke.png"));

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets duke used for getting responses on user input, also adds first Duke greeting on init
     * @param d contains Duke instance we are using
     */
    public void setDuke(Duke d) {
        // duke should never be set to null
        assert d != null : "Duke is set to null in MainWindow";
        // set instance of duke to mainwindow for response
        duke = d;
        // add greeting whenever we set duke
        dialogContainer.getChildren().add(DialogBox.getDukeDialog(duke.getGreeting(), dukeImage));
    }

    /**
     * Creates two dialog boxes, one echoing user inpt and the other containing Duke's reply
     * and then appends them to the dialog container. Clears user input after processing
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        // duke getResponse returns response from Duke's logic
        String response = duke.getResponse(input);
        // add response and input to DialogBox
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        // clear user input
        userInput.clear();
    }
}
