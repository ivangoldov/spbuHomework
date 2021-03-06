package group144.goldov;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class Controller {
    private int size;

    private TicTacToeField field;

    private Socket client;

    private TextField gameProgress;

    private Button[][] buttons;

    private GridPane scene;

    /** Checks current user */
    public enum USER  {CLIENT, SERVER}

    private USER user;

    /** Input stream to server:
     * if it's an integer ij, where i = 1..3, j = 1..3, then opponent made turn
     * if it's -1, then opponent exited
     */
    private InputStream inputStream;

    /** Print stream to server:
     * if it's an integer ij, where i = 1..3, j = 1..3, then opponent made turn
     * if it's -1, then opponent exited
     */
    private PrintStream printStream;

    /** Check the state of the game */
    private boolean isGameGoing = true;

    public Controller(GridPane scene, USER user, TextField gameProgress) {
        this.scene = scene;
        this.user = user;
        this.field = new TicTacToeField();
        this.size = field.getSize();
        this.buttons = new Button[size][size];
        this.gameProgress = gameProgress;
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j] = (Button) scene.getChildren().get(k);
                k++;
            }
        }
    }

    /** Proceeds pressing one of the buttons on the play field */
    public void pressButton(javafx.event.ActionEvent actionEvent) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button current = buttons[i][j];
                if (actionEvent.getSource().equals(current)) {
                    if (field.turn(i, j)) {
                        current.textProperty().setValue(field.whichPlayer());
                        printStream.write(10 * (i + 1) + (j + 1));
                        printStream.flush();
                        if (field.isGameOver() == TicTacToeField.StateOfGame.PLAYING) {
                            gameProgress.setText("Wait your turn");
                            waitTurn();
                        }
                    }
                    checkGameState();
                }
            }
        }
    }

    /** Creates thread to wait for opponent's turn */
    public void waitTurn() {
        lockButtons();
        new Thread(() -> {
            int status = -1;
            while (true) {
                try {
                    if (inputStream.available() == 0) {
                        try {
                            status = inputStream.read();
                        } catch (IOException e) {
                            isGameGoing = false;
                        }
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int newStatus = status;
            Platform.runLater(() -> opponentTurn(newStatus));
        }).start();
    }

    /** Proceeds opponent's turn
     * if it's an integer ij, where i = 1..3, j = 1..3, then opponent made turn
     * if it's -1, then opponent exited
     * @param status - determines if the game ended or which turn opponent made
     */
    private void opponentTurn(int status) {
        if (status == -1) {
            Alert exitMessage = new Alert(Alert.AlertType.INFORMATION);
            exitMessage.setTitle("Game Over");
            exitMessage.setContentText("Opponent has left");
            exitMessage.showAndWait();
            isGameGoing = false;
            Platform.exit();
        } else if (status >= 11 && status <= 33) {
            field.turn((status / 10) - 1, (status % 10) - 1);
            unlockButtons();
            gameProgress.setText("Your turn");
            buttons[(status / 10) - 1][(status % 10) - 1].textProperty().setValue(field.whichPlayer());
            checkGameState();
        }
    }

    /** Checks game state */
    private void checkGameState() {
        if (field.isGameOver() == TicTacToeField.StateOfGame.XWON) {
            endGame("1st player won");
        }
        if (field.isGameOver() == TicTacToeField.StateOfGame.OWON) {
            endGame("2nd player won");
        }
        if (field.isGameOver() == TicTacToeField.StateOfGame.DRAW) {
            endGame("Draw");
        }
    }

    /** Ends the game with given result */
    private void endGame(String string) {
        lockButtons();
        gameProgress.setText("Game is over");
        Alert endMessage = new Alert(Alert.AlertType.INFORMATION);
        endMessage.setTitle("Game over");
        endMessage.setContentText(string);
        endMessage.showAndWait();

    }

    /** Checks if game's still in progress */
    public boolean isGameGoing() {
        return isGameGoing;
    }

    /** Sends exit message if user exited */
    public void sendExitMessage() {
        printStream.write(-1);
        printStream.flush();
    }

    /** Sets streams */
    public void setStreams(InputStream inputStream, PrintStream printStream) {
        this.inputStream = inputStream;
        this.printStream = printStream;
    }

    /** Locks all buttons */
    private void lockButtons() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    /** Unlocks all buttons */
    private void unlockButtons() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setDisable(false);
            }
        }
    }
}
