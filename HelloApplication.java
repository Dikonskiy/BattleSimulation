package com.example.projectfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int OBJECT_SIZE = 10;
    private static final int TOTAL_OBJECTS = 1 + (int) (Math.random()*40); // Total number of circles
    private static final double SPEED = 0.5; // Speed of the objects

    private List<GameObject> objects;


    @Override
    public void start(Stage primaryStage) {
        objects = new ArrayList<>();
        Random random = new Random();

        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Battle Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        int objectsPerColor = TOTAL_OBJECTS / 3; // Equal quantity of each color

        // Creating and adding objects to the scene
        for (int i = 0; i < objectsPerColor; i++) {
            Circle greenCircle = createCircleWithColor(Color.GREEN, random);
            Circle redCircle = createCircleWithColor(Color.RED, random);
            Circle blueCircle = createCircleWithColor(Color.BLUE, random);

            objects.add(new GameObject(greenCircle, SPEED));
            objects.add(new GameObject(redCircle, SPEED));
            objects.add(new GameObject(blueCircle, SPEED));

            root.getChildren().addAll(greenCircle, redCircle, blueCircle);
        }


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Moving objects
                for (GameObject object : objects) {
                    object.move();
                }

                // Handling collisions and color changes
                for (int i = 0; i < objects.size(); i++) {
                    GameObject obj1 = objects.get(i);
                    for (int j = i + 1; j < objects.size(); j++) {
                        GameObject obj2 = objects.get(j);

                        if (obj1.collidesWith(obj2)) {
                            obj1.handleCollision(obj2);
                            obj2.handleCollision(obj1);
                            obj1.repel(obj2);
                            obj2.repel(obj1);
                        }
                    }
                }
            }
        };
        timer.start();
    }

    private Circle createCircleWithColor(Color color, Random random) {
        Circle circle = new Circle(OBJECT_SIZE);
        circle.setFill(color);
        circle.setTranslateX(random.nextDouble() * (WIDTH - OBJECT_SIZE));
        circle.setTranslateY(random.nextDouble() * (HEIGHT - OBJECT_SIZE));
        return circle;
    }

    public static void main(String[] args) {
        launch(args);
    }

    class GameObject {
        private Circle object;
        private Point2D velocity;

        public GameObject(Circle object, double speed) {
            this.object = object;
            Random random = new Random();
            double angle = random.nextDouble() * 2 * Math.PI;
            this.velocity = new Point2D(Math.cos(angle), Math.sin(angle)).normalize().multiply(speed);
        }

        public void move() {
            object.setTranslateX(object.getTranslateX() + velocity.getX());
            object.setTranslateY(object.getTranslateY() + velocity.getY());

            // Handling bouncing off the window borders
            if (object.getTranslateX() <= 0 || object.getTranslateX() + OBJECT_SIZE >= WIDTH) {
                velocity = new Point2D(-velocity.getX(), velocity.getY());
            }
            if (object.getTranslateY() <= 0 || object.getTranslateY() + OBJECT_SIZE >= HEIGHT) {
                velocity = new Point2D(velocity.getX(), -velocity.getY());
            }
        }

        public boolean collidesWith(GameObject other) {
            return object.getBoundsInParent().intersects(other.object.getBoundsInParent());
        }

        public void handleCollision(GameObject other) {
            if (this.object.getFill() == Color.RED && other.object.getFill() == Color.GREEN) {
                this.object.setFill(Color.GREEN);
            } else if (this.object.getFill() == Color.GREEN && other.object.getFill() == Color.BLUE) {
                this.object.setFill(Color.BLUE);
            } else if (this.object.getFill() == Color.BLUE && other.object.getFill() == Color.RED) {
                this.object.setFill(Color.RED);
            }
        }

        public void repel(GameObject other) {
            Point2D repulsion = object.getBoundsInParent().getCenterX() > other.object.getBoundsInParent().getCenterX()
                    ? new Point2D(1, 0) : new Point2D(-1, 0);
            velocity = velocity.add(repulsion);
        }
    }
}
