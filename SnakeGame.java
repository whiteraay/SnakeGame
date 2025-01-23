import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class SnakeGame extends Application {
    Scanner in=new Scanner(System.in);
    final int size = 800;
    final int SQUARE_SIZE = 40; //SIZE OF SQUARES (SNAKE`S BODY)
    final int up = 1;
    final int right = 2;
    final int down = 3;
    final int left = 4;


    int initialDelay = 160;
    int length = 3;
    int  dir = right;
    int  food_x;
    int  food_y;
    int  score = 0;

    Canvas canvas;
    GraphicsContext object;
    int x[] = new int[size * size];
    int y[] = new int[size * size];

    int delay = initialDelay; // Кідіріс 

    private int highestScore = 0;
    Thread game;
    boolean inlost = false;

    public void start(Stage primaryStage) {
        Scanner in=new Scanner(System.in);
        System.out.println("STARTING THE GAME  ?!  [go]");
        String answer=in.nextLine();
        String go="go";
        if(answer.equals(go)){
            System.out.println("Look to the instruction and start the game.");
        }
        else{
            System.out.println("Ok start the game.");
        }
        
        primaryStage.setTitle("Snake Game");

        Button botton_start = new Button("Start Game");
        botton_start.setOnAction(e -> initializeGame(primaryStage));
        TextArea text_instruction = new TextArea("Insructions:\n1)Use arrow keys to move the snake  [W-up,A-left,S-down,D-right].\n2)Eat the food to grow.\n3)Don't bump yourself!\n4)You may RESTART the game by pressing space!");
        text_instruction.setEditable(false);
        text_instruction.setWrapText(true);

        HBox box_instruction = new HBox(60,text_instruction);
        box_instruction.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setTop(botton_start);
        root.setCenter(box_instruction);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    private void initializeGame(Stage primaryStage) {
        Pane root = new Pane();
        canvas = new Canvas(size, size);
        object = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, size, size);
        scene.setOnKeyPressed(e -> {
        
            KeyCode key = e.getCode();
            if (key == KeyCode.W && dir != down) dir = up;
            if (key == KeyCode.S && dir != up) dir = down;
            if (key == KeyCode.A && dir != right) dir = left;
            if (key == KeyCode.D && dir != left) dir = right;
            if (key == KeyCode.SPACE) restartGame(); // Restart on SPACE pres
        });
        primaryStage.setScene(scene);
        primaryStage.show();

        startGame();
    }
    
    private void draw(GraphicsContext object) {
       object.clearRect(0, 0, size, size);
       object.setStroke(Color.BLACK);
       object.setLineWidth(2);
       object.strokeRect(0, 0, size, size);

        if (!inlost) 
        {
            object.setFill(Color.RED); // Snake's head color
            object.setStroke(Color.WHITE); // Snake's head border color
            object.fillRect(x[0], y[0], SQUARE_SIZE, SQUARE_SIZE);
            object.strokeRect(x[0], y[0], SQUARE_SIZE, SQUARE_SIZE);

            object.setStroke(Color.WHITE); // Snake's body border color
            object.setFill(Color.BURLYWOOD); // Snake's body color   BURLYWOOD
            for (int i = 1; i < length; i++) 
            {
                object.fillRect(x[i], y[i], SQUARE_SIZE, SQUARE_SIZE);
                object.strokeRect(x[i], y[i], SQUARE_SIZE, SQUARE_SIZE);
            }

            object.setStroke(Color.LIME);
            object.strokeRect(food_x, food_y, SQUARE_SIZE, SQUARE_SIZE);  //FOOD COLOR 
            object.setFill(Color.VIOLET);
            object.fillRect(food_x, food_y, SQUARE_SIZE, SQUARE_SIZE);
        }
        if(inlost==true) 
        {
            object.setFill(Color.BLACK);
            object.setFont(Font.font("Poppins", FontWeight.BOLD, 24)); //SIZE OF CHARACTERS

            object.fillText("Game Over!!", size / 3 , size / 2.5);
            object.fillText("Score: " + score + " Length: " + length, size / 3, size / 2.5-35);
            object.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
            object.fillText("You may  RESTART by pressing << SPACE >>", size / 3 , size / 2.5-100);
            game.stop();

        }
         //HIGHEST SCORE
        if (score > highestScore)
        {
            highestScore = score;
            System.out.println(length);   //LENGTH OF SNAKE 
        }
        object.setFill(Color.PURPLE);
        object.setFont(Font.font("Arcade_gamer", FontWeight.BOLD, 30)); 
        object.fillText("Length: " + length + "    Score: " + score + "    Highest Score: " + highestScore, 70, 40);
    
        if(inlost==true)
        {
            restartGame();
        }


        object.setStroke(Color.BURLYWOOD);
        object.setLineWidth(0.25);
        for (int i = 0; i < size; i += 20) 
        {
            object.strokeLine(i, 0, i, size);
            object.strokeLine(0, i, size, i);
        }
    }


    private void startGame() {
        length = 3;
        score = 0;
        delay = initialDelay;
        for (int i = 1; i < length; i++) 
        {
            x[i] = 50 - i * SQUARE_SIZE;
            y[i] = 50;
        }
        locateFood();
        game = new Thread(() -> {
            while (true) {
                if (!inlost) {
                    checkFood();
                    checkCollision();
                    move();
                }
                draw(object);
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                }
            }
        });
        game.start();
    }

    private void restartGame() {
        System.out.println(score);
        System.out.println("Highest score :"+highestScore);
        inlost = false;
        length = 3;
        score = 0;
        delay = initialDelay;

        for (int i = 1; i < length; i++) {
            x[i] = 50 - i * SQUARE_SIZE;
            y[i] = 50;
        }

        locateFood();

        game = new Thread(() -> {
            while (true) {
                if (!inlost) {
                    checkFood();
                    checkCollision();
                    move();
                }
                draw(object);
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                }
            }
        });
        game.start();
    }


    private void locateFood() {
        food_x = ((int) (Math.random() * (size / SQUARE_SIZE))) * SQUARE_SIZE;
        food_y = ((int) (Math.random() * (size / SQUARE_SIZE))) * SQUARE_SIZE;
    }


    private void checkFood() {
        if (x[0] == food_x && y[0] == food_y) {
            length++;
            score++;
            locateFood();
            delay = Math.max(delay - 10, 30);
        }
    }



    private void checkCollision() {
        for (int i = 1; i < length; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                inlost = true;
                System.out.println(i);
            }
        }
    }

    private void move() {
        for (int i = length - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (dir) {
            case up:
                y[0] -= SQUARE_SIZE;
                break;
            case down:
                y[0] += SQUARE_SIZE;
                break;
            case right:
                x[0] += SQUARE_SIZE;
                break;
            case left:
                x[0] -= SQUARE_SIZE;
                break;
        }

        if (x[0] >= size) {
            x[0] = 0;
        } else if (x[0] < 0) {
            x[0] = size - SQUARE_SIZE;
        }

        if (y[0] >= size) {
            y[0] = 0;
        } else if (y[0] < 0) {
            y[0] = size - SQUARE_SIZE;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
