/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermediaplayer;

import java.awt.Toolkit;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 *
 * @author SULLY
 */
public class MediaControl extends BorderPane {

    /*CONSTANT Labels for all Buttons*/
    private final String PLAY_BUTTON_TEXT = ">";
    private final String PAUSE_BUTTON_TEXT = "||";
    private final String MUTE_BUTTON_TEXT = "   Mute   ";
    private final String UN_MUTE_BUTTON_TEXT = "Un-Mute";
    private final String FAST_FORWARD_BUTTON_TEXT = ">>";
    private final String REWIND_BUTTON_TEXT = "<<";
    private final String SKIP_BUTTON_TEXT = ">|";
    private final String BACK_BUTTON_TEXT = "|<";
    private final String VOLUME_LABEL = "Vol: ";
    private final String NEW_VIDEO_LABEL = "[]";
    private final String SPACER_LABEL = "   ";
    private final String TIME_LABEL = "Time: ";

    private MediaPlayer mp;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private boolean playListOn = false;
    private MenuBar menuBar;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Label volumeLabel;
    private Label spacer;
    private Slider volumeSlider;
    private HBox mediaBar;
    private Label timeLabel;
    private Button backButton;
    private Button rewindButton;
    private Button playButton;
    private Button fastForwardButton;
    private Button skipButton;
    private Button muteButton;
    private Button newVideoButton;

    private PlayList playList;

    public MediaControl(final MediaPlayer mp, final PlayList playList) {
        this.mp = mp;
        this.playList = playList;
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView(mp);

        Pane mvPane = new Pane();
        mediaView.relocate(100, 30);
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        mvPane.autosize();

        setCenter(mvPane);

        createMediaBarControlls();
        //Create media Bar
        menuBar = createMenuBar();
        setTop(menuBar);

    }

    private Button createNewVideoButton() {
        Button newVideoButton = new Button(NEW_VIDEO_LABEL);
        newVideoButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                destroyMedia();
                addNewMedia();
                mediaBar.getChildren().clear();
                createMediaBarControlls();
                updateValues();
            }

        });
        return newVideoButton;
    }

    private Button createPlayButton() {
        Button playButton = new Button(PAUSE_BUTTON_TEXT);
//        Image playImage =  new Image("play.png");
//        BackgroundRepeat x = null, y = null;
//        BackgroundPosition z = null;
//        BackgroundSize s = null;
//        BackgroundImage bI= new BackgroundImage(playImage,x,y,z, s);
//        Background playButtonBackground = new Background(bI);
//        playButton.setBackground(playButtonBackground);
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
                }
            }
        });
        return playButton;
    }

    private Button createFFButton() {
        Button fastForward = new Button(FAST_FORWARD_BUTTON_TEXT);

        fastForward.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }

                } else {

                    mp.seek(mp.getCurrentTime().multiply(1.5));
                }
            }
        });

        return fastForward;
    }

    private Button createRewindButton() {
        Button rr = new Button(REWIND_BUTTON_TEXT);

        rr.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                //rewind Here
                mp.seek(mp.getCurrentTime().divide(1.5));

            }
        });
        return rr;
    }

    private Button createBackButton() {
        Button backButton = new Button(BACK_BUTTON_TEXT);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }
                if (!playListOn) {
                    mp.seek(mp.getStartTime());
                } else {
                    destroyMedia();
                    mp = new MediaPlayer(playList.prevInList());
                    mp.setAutoPlay(true);
                    mediaView.setMediaPlayer(mp);
                    mediaBar.getChildren().clear();
                    createMediaBarControlls();
                    updateValues();
                }

            }
        });
        return backButton;
    }

    private Button createSkipButton() {
        Button skipButton = new Button(SKIP_BUTTON_TEXT);
        skipButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }
                if (!playListOn) {
                    mp.seek(mp.getTotalDuration());
                } else {
                    destroyMedia();
                    mp = new MediaPlayer(playList.nextInList());
                    mp.setAutoPlay(true);
                    mediaView.setMediaPlayer(mp);
                    mediaBar.getChildren().clear();
                    createMediaBarControlls();
                    updateValues();
                }
            }
        });
        return skipButton;
    }

    private Button createMuteButon() {
        Button muteButton = new Button(MUTE_BUTTON_TEXT);
        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                boolean isMuted = mp.isMute();
                muteButton.setText((isMuted) ? MUTE_BUTTON_TEXT : UN_MUTE_BUTTON_TEXT);
                mp.setMute(!isMuted);

            }
        });
        return muteButton;
    }

    private Slider createVolumeSlider() {
        Slider volumeSlider = new Slider();
        volumeSlider.adjustValue(volumeSlider.getMax());
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (volumeSlider.isValueChanging()) {
                    mp.setVolume(volumeSlider.getValue() / 100.0);
                    updateValues();
                }
            }
        });

        return volumeSlider;

    }

    private Slider createTimeSlider() {
        Slider timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
                updateValues();
            }
        });

        return timeSlider;
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, mp.getStopTime()));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide((double) duration.toMillis()).toMillis() * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    private Label createPlayLabel() {
        Label playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        return playTime;
    }

    private HBox createMediaBar() {
        HBox mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
        return mediaBar;
    }

    private void mpSetUp() {
        mp.currentTimeProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(javafx.beans.Observable observable) {
                updateValues();
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    playButton.setText(PAUSE_BUTTON_TEXT);
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
                playButton.setText(PLAY_BUTTON_TEXT);
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (playListOn) {
                    destroyMedia();
                    mp = new MediaPlayer(playList.nextInList());
                    mp.setAutoPlay(true);
                    mediaView.setMediaPlayer(mp);
                    mediaBar.getChildren().clear();
                    createMediaBarControlls();
                    updateValues();
                } else if (!repeat) {
                    playButton.setText(PLAY_BUTTON_TEXT);
                    stopRequested = true;
                    atEndOfMedia = true;

                }

            }
        });
    }

    private void destroyMedia() {
        mp.stop();
    }

    private void addNewMedia() {
        File file = new File(fileChooser());
        //The location of your file
        Media media = new Media(pathMaker(file));
        mp = new MediaPlayer(media);
        mp.setAutoPlay(true);
        mediaView.setMediaPlayer(mp);
    }

    public static String pathMaker(File file) {
        return file.toURI().toString();
    }

    public static String fileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(null);
        String pathName = fileChooser.getSelectedFile().getAbsolutePath();

        return pathName;
    }

    private void createMediaBarControlls() {

        mediaBar = createMediaBar();

        newVideoButton = createNewVideoButton();

        backButton = createBackButton();

        rewindButton = createRewindButton();

        playButton = createPlayButton();

        //Create fastForward
        fastForwardButton = createFFButton();

        skipButton = createSkipButton();

        muteButton = createMuteButon();
        // Create spacer
        spacer = new Label(SPACER_LABEL);

        // Create Time label
        timeLabel = new Label(TIME_LABEL);

        // Create time slider
        timeSlider = createTimeSlider();

        // Create Play label
        playTime = createPlayLabel();

        // Create the volume label
        volumeLabel = new Label(VOLUME_LABEL);

        // Create Volume slider
        volumeSlider = createVolumeSlider();

        //sets up all mp stuff needed to help update screen approapiately 
        mpSetUp();

        mediaBar.getChildren().addAll(backButton, rewindButton, playButton, fastForwardButton, skipButton,
                newVideoButton, spacer, timeLabel, playTime, timeSlider, volumeLabel, muteButton, volumeSlider);
        setBottom(mediaBar);

    }

    private MenuBar createMenuBar() {
        MenuBar menubar = new MenuBar();

        //Menu name
        Menu filemenu = new Menu("File");
        Menu playListmenu = new Menu("Play List");

        MenuItem closer = createClosingOption();

        MenuItem startPlaylist = createStartPlayList();

        MenuItem addToPlaylist = createAddToPlaylist();

        MenuItem addMultiToPlayList = createAddMultiToPlayList();

        MenuItem stopPlaylist = createStopPlayList();

        MenuItem createPlaylist = createPlayList();

        MenuItem loadPlayList = createLoadPlayList();

        MenuItem loadRandomPlayList = createloadRandomPlayList();

        playListmenu.getItems().addAll(startPlaylist, stopPlaylist, addToPlaylist, addMultiToPlayList, createPlaylist, loadPlayList, loadRandomPlayList);
        filemenu.getItems().addAll(closer);

        menubar.getMenus().addAll(filemenu, playListmenu);
        return menubar;
    }

    private MenuItem createAddToPlaylist() {
        MenuItem addToPlaylist = new MenuItem("Add To PlayList");
        addToPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                mp.pause();
                playList.addToList();
                mp.play();
            }
        });
        return addToPlaylist;
    }

    private MenuItem createStartPlayList() {
        MenuItem startPlaylist = new MenuItem("Start The PlayList");
        startPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                playListOn = true;
                destroyMedia();
                mp = new MediaPlayer(playList.nextInList());
                mp.setAutoPlay(true);
                mediaView.setMediaPlayer(mp);
                mediaBar.getChildren().clear();
                createMediaBarControlls();
                updateValues();
            }
        });
        return startPlaylist;
    }

    private MenuItem createStopPlayList() {
        MenuItem stopPlaylist = new MenuItem("Stop The PlayList");
        stopPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                playListOn = false;
                mp.stop();
            }
        });
        return stopPlaylist;
    }

    private MenuItem createPlayList() {
        MenuItem createPlaylist = new MenuItem("Create PlayList");
        createPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                mp.pause();
                if (!playList.createNewPlayList()) {
                    System.out.println("PlayList Not Made!!!");
                }
                mp.play();
            }
        });
        return createPlaylist;
    }

    private MenuItem createLoadPlayList() {
        MenuItem loadPlayList = new MenuItem("Load PlayList");
        loadPlayList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                mp.pause();
                playList.loadPlayList();
                mp.play();

            }
        });
        return loadPlayList;
    }

    private MenuItem createloadRandomPlayList() {
        MenuItem loadRandomPlayList = new MenuItem("Shuffle Load PlayList");
        loadRandomPlayList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                mp.pause();
                playList.randomLoadPlayList();
                mp.play();

            }
        });
        return loadRandomPlayList;
    }

    private MenuItem createClosingOption() {
        MenuItem closer = new MenuItem("Close");
        closer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        return closer;
    }

    private MenuItem createAddMultiToPlayList() {
        MenuItem addMultiToPlaylist = new MenuItem("Add Multiple Files To PlayList");
        addMultiToPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                mp.pause();
                playList.addMultipleToList();
                mp.play();
            }
        });
        return addMultiToPlaylist;
    }
}
