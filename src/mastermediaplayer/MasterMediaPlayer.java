/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermediaplayer;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JFileChooser;

/**
 *
 * @author SULLY
 */
public class MasterMediaPlayer extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("PlayList Media Player");
        Group root = new Group();
        PlayList playList = new PlayList(); 
        File file = new File(MediaControl.fileChooser());
        //The location of your file
        Media media = new Media(MediaControl.pathMaker(file));

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), 800);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setAutoPlay(true);

        MediaControl mediaControl = new MediaControl(mediaPlayer, playList);
        scene.setRoot(mediaControl);

        primaryStage.setWidth(bounds.getWidth());

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
