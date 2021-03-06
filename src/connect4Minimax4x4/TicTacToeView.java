package connect4Minimax4x4;

// Original code from https://gist.github.com/jewelsea/5115901
// Since modified by
//author: Gary Kalmanovich; rights reserved

/**
 * Copyright 2013 John Smith
 *
 * This file is part of Jewelsea Tic-Tac-Toe.
 *
 * Jewelsea Tic-Tac-Toe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jewelsea Tic-Tac-Toe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jewelsea Tic-Tac-Toe.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact details: http://jewelsea.wordpress.com
 *
 * icon image license => creative commons with attribution:
 *   http://creativecommons.org/licenses/by/3.0/
 * icon image creator attribution:
 *   http://www.doublejdesign.co.uk/products-page/icons/origami-colour-pencil
 */

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class TicTacToeView implements InterfaceView{
    private InterfaceControl controller;
    private GameManager      gameManager;
    
    TicTacToeView(InterfaceControl controller) {
        if ( controller instanceof TicTacToeControl ) {
            this.controller = (TicTacToeControl) controller;
        } else 
            System.out.println("Error: TicTacToeView does not recognize this type of InterfaceControl");
    }

    @Override public int nC() { return 3; }
    @Override public int nR() { return 3; }

    @Override 
    public void start(Stage stage) {
        // Note: this method does not override Application::start()
        // It could if the class implemented Application. However, it does not.

        gameManager = new GameManager(controller);

        Scene scene = gameManager.getGameScene();
        scene.getStylesheets().add(
                getResource(
                        "tictactoe-blueskin.css"
                        )
                );

        stage.setTitle("Tic-Tac-Toe");
        stage.getIcons().add(SquareSkin.crossImage);
        stage.setScene(scene);
        stage.show();

        controller.setView(this);
        controller.onMove();
    }

    @Override 
    public void performMove( int i0C, int i0R, int i1C, int i1R, int iPlayer ) {
        gameManager.getGame().getBoard().getSquare(i1C,i1R).pressed();
    }

    GameManager getGameManager() { // This breaks the interface for TicTacToeControl
        return gameManager;
    }

    private String getResource(String resourceName) {
        return getClass().getResource("").toExternalForm()+"../../src/connect4MinimaxIndividual4x4/"+resourceName;
        //return getClass().getResource(resourceName).toExternalForm();
    }

}

class GameManager {
    private Scene gameScene;
    private Game  game;
    private InterfaceControl controller;

    GameManager(InterfaceControl controller) {
        this.controller        = controller;
        newGame();
    }

    InterfaceControl getController() { return controller; }

    public void newGame() {
        game = new Game(this, controller);

        if (gameScene == null) {
            gameScene = new Scene(game.getSkin());
        } else {
            gameScene.setRoot(game.getSkin());
        }
        if (controller.getView()!=null ) controller.onMove(); // On the very first one, not yet initialized
    }

    public void quit() {
        gameScene.getWindow().hide();
    }

    public Game getGame() {
        return game;
    }

    public Scene getGameScene() {
        return gameScene;
    }
}

class GameControls extends HBox {
    GameControls(final GameManager gameManager, final Game game) {
        getStyleClass().add("game-controls");

        visibleProperty().bind(game.gameOverProperty());

        Label playAgainLabel = new Label("Play Again?");
        playAgainLabel.getStyleClass().add("info");

        Button playAgainButton = new Button("Yes");
        playAgainButton.getStyleClass().add("play-again");
        playAgainButton.setDefaultButton(true);
        playAgainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                gameManager.newGame();
            }
        });

        Button exitButton = new Button("No");
        playAgainButton.getStyleClass().add("exit");
        exitButton.setCancelButton(true);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameManager.quit();
            }
        });

        getChildren().setAll(
                playAgainLabel,
                playAgainButton,
                exitButton
                );
    }
}

class StatusIndicator extends HBox {
    private final ImageView playerToken = new ImageView();
    private final Label     playerLabel = new Label("Current Player: ");

    StatusIndicator(Game game) {
        getStyleClass().add("status-indicator");

        bindIndicatorFieldsToGame(game);

        playerToken.setFitHeight(32);
        playerToken.setPreserveRatio(true);

        playerLabel.getStyleClass().add("info");

        getChildren().addAll(playerLabel, playerToken);
    }

    private void bindIndicatorFieldsToGame(Game game) {
        playerToken.imageProperty().bind(
                Bindings.when(
                        game.currentPlayerProperty().isEqualTo(Square.State.NOUGHT)
                        )
                        .then(SquareSkin.noughtImage)
                        .otherwise(
                                Bindings.when(
                                        game.currentPlayerProperty().isEqualTo(Square.State.CROSS)
                                        )
                                        .then(SquareSkin.crossImage)
                                        .otherwise((Image) null)
                                )
                );

        playerLabel.textProperty().bind(
                Bindings.when(
                        game.gameOverProperty().not()
                        )
                        .then("Current Player: ")
                        .otherwise(
                                Bindings.when(
                                        game.winnerProperty().isEqualTo(Square.State.EMPTY)
                                        )
                                        .then("Draw")
                                        .otherwise("Winning Player: ")
                                )
                );
    }
}

class Game {
    private InterfaceControl controller;
    private GameSkin skin;
    private Board board = new Board(this);
    private WinningStrategy winningStrategy = new WinningStrategy(board);

    private ReadOnlyObjectWrapper<Square.State> currentPlayer = new ReadOnlyObjectWrapper<>(Square.State.CROSS);
    public ReadOnlyObjectProperty<Square.State> currentPlayerProperty() {
        return currentPlayer.getReadOnlyProperty();
    }
    public Square.State getCurrentPlayer() {
        return currentPlayer.get();
    }

    private ReadOnlyObjectWrapper<Square.State> winner = new ReadOnlyObjectWrapper<>(Square.State.EMPTY);
    public ReadOnlyObjectProperty<Square.State> winnerProperty() {
        return winner.getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper drawn = new ReadOnlyBooleanWrapper(false);
    public ReadOnlyBooleanProperty drawnProperty() {
        return drawn.getReadOnlyProperty();
    }
    public boolean isDrawn() {
        return drawn.get();
    }

    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false);
    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }
    public boolean isGameOver() {
        return gameOver.get();
    }

    public Game(GameManager gameManager, InterfaceControl controller) {
        this.controller                                 = controller;
        gameOver.bind(
                winnerProperty().isNotEqualTo(Square.State.EMPTY)
                .or(drawnProperty())
                );

        skin = new GameSkin(gameManager, this);
    }

    public Board getBoard() {
        return board;
    }

    public void nextTurn() {
        if (isGameOver()) return;

        switch (currentPlayer.get()) {
        case EMPTY:
        case NOUGHT: currentPlayer.set(Square.State.CROSS);  break;
        case CROSS:  currentPlayer.set(Square.State.NOUGHT); break;
        }
        controller.onMove();
    }

    private void checkForWinner() {
        winner.set(winningStrategy.getWinner());
        drawn.set(winningStrategy.isDrawn());

        if (isDrawn()) {
            currentPlayer.set(Square.State.EMPTY);
        }
    }

    public void boardUpdated() {
        checkForWinner();
    }

    public Parent getSkin() {
        return skin;
    }
}

class GameSkin extends VBox {
    GameSkin(GameManager gameManager, Game game) {
        getChildren().addAll(
                new StrategyChoice(gameManager.getController(), Color.AZURE),
                new Separator(),
                game.getBoard().getSkin(),
                new StatusIndicator(game),
                new GameControls(gameManager, game)
                );
    }
}

class WinningStrategy {
    private final Board board;

    private static final int NOUGHT_WON = 3;
    private static final int CROSS_WON  = 30;

    private static final Map<Square.State, Integer> values = new HashMap<>();
    static {
        values.put(Square.State.EMPTY,  0);
        values.put(Square.State.NOUGHT, 1);
        values.put(Square.State.CROSS,  10);
    }

    public WinningStrategy(Board board) {
        this.board = board;
    }

    public Square.State getWinner() {
        for (int i = 0; i < 3; i++) {
            int score = 0;
            for (int j = 0; j < 3; j++) {
                score += valueOf(i, j);
            }
            if (isWinning(score)) {
                return winner(score);
            }
        }

        for (int i = 0; i < 3; i++) {
            int score = 0;
            for (int j = 0; j < 3; j++) {
                score += valueOf(j, i);
            }
            if (isWinning(score)) {
                return winner(score);
            }
        }

        int score = 0;
        score += valueOf(0, 0);
        score += valueOf(1, 1);
        score += valueOf(2, 2);
        if (isWinning(score)) {
            return winner(score);
        }

        score = 0;
        score += valueOf(2, 0);
        score += valueOf(1, 1);
        score += valueOf(0, 2);
        if (isWinning(score)) {
            return winner(score);
        }

        return Square.State.EMPTY;
    }

    public boolean isDrawn() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getSquare(i, j).getState() == Square.State.EMPTY) {
                    return false;
                }
            }
        }

        return getWinner() == Square.State.EMPTY;
    }

    private Integer valueOf(int i, int j) {
        return values.get(board.getSquare(i, j).getState());
    }

    private boolean isWinning(int score) {
        return score == NOUGHT_WON || score == CROSS_WON;
    }

    private Square.State winner(int score) {
        if (score == NOUGHT_WON) return Square.State.NOUGHT;
        if (score == CROSS_WON)  return Square.State.CROSS;

        return Square.State.EMPTY;
    }
}

class Board {
    private final BoardSkin skin;

    private final Square[][] squares = new Square[3][3];

    public Board(Game game) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[i][j] = new Square(game);
            }
        }

        skin = new BoardSkin(this);
    }

    public Square getSquare(int i, int j) {
        return squares[i][j];
    }

    public Node getSkin() {
        return skin;
    }
}

class BoardSkin extends GridPane {
    BoardSkin(Board board) {
        getStyleClass().add("board");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                add(board.getSquare(i, j).getSkin(), i, j);
            }
        }
    }
}

class Square {
    enum State { EMPTY, NOUGHT, CROSS }

    private final SquareSkin skin;

    private ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(State.EMPTY);
    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }
    public State getState() {
        return state.get();
    }

    private final Game game;

    public Square(Game game) {
        this.game = game;

        skin = new SquareSkin(this);
    }

    public void pressed() {
        if (!game.isGameOver() && state.get() == State.EMPTY) {
            state.set(game.getCurrentPlayer());
            game.boardUpdated();
            game.nextTurn();
        }
    }

    public Node getSkin() {
        return skin;
    }
}

class SquareSkin extends StackPane {
    static final Image noughtImage = new Image(
            //"http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/green-cd-icon.png"
            SquareSkin.class.getResource("").toExternalForm()+"../../src/connect4MinimaxIndividual4x4/green-cd-icon.png"
            );
    static final Image crossImage = new Image(
            //"http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/blue-cross-icon.png"
            SquareSkin.class.getResource("").toExternalForm()+"../../src/connect4MinimaxIndividual4x4/blue-cross-icon.png"
            );

    private final ImageView imageView = new ImageView();

    SquareSkin(final Square square) {
        getStyleClass().add("square");

        imageView.setMouseTransparent(true);

        getChildren().setAll(imageView);
        setPrefSize(crossImage.getHeight() + 20, crossImage.getHeight() + 20);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                square.pressed();
            }
        });

        square.stateProperty().addListener(new ChangeListener<Square.State>() {
            @Override public void changed(ObservableValue<? extends Square.State> observableValue, Square.State oldState, Square.State state) {
                switch (state) {
                case EMPTY:  imageView.setImage(null);        break;
                case NOUGHT: imageView.setImage(noughtImage); break;
                case CROSS:  imageView.setImage(crossImage);  break;
                }
            }
        });
    }
}