/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermediaplayer;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Queue;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.scene.media.Media;
import javax.swing.JFileChooser;

/**
 *
 * @author SULLY
 */
public class PlayList {

//    Queue<Media> playList = new ArrayDeque<>();
    ArrayList<Media> playList;
    int currentPosition;
    int lastFilled;
    String path = "test.txt";
    Frame frame;
    public PlayList() {
        playList = new ArrayList<>();
        currentPosition = lastFilled = 0;
//        this.frame = frame;
    }

    public boolean loadPlayList() {
        playList = new ArrayList<>();
        try (BufferedReader bread = new BufferedReader(new FileReader(path))) {
            String tester = bread.readLine();
            while (tester != null && tester.length() != 0) {
                System.out.println(tester);
                Media temp = new Media(tester);
                playList.add(temp);
                tester = bread.readLine();

            }
            playList.trimToSize();
        } catch (IOException ex) {
            System.out.println("Shit went wrong yo! Reading ERRORRR");
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return true;
    }

    public boolean randomLoadPlayList() {
        playList = new ArrayList<>();
        ArrayList<Media> tempHolder = new ArrayList<>();
        boolean isDone = true;
        ArrayList<Integer> numbers = new ArrayList<>();
        int lastStored = 0;
        int ogSize;

        try (BufferedReader bread = new BufferedReader(new FileReader(path))) {
            String tester = bread.readLine();
            while (tester != null && tester.length() != 0) {
                System.out.println(tester);
                Media temp = new Media(tester);

                playList.add(temp);
                tester = bread.readLine();

            }
            playList.trimToSize();
        } catch (IOException ex) {
            System.out.println("Shit went wrong yo! Reading ERRORRR");
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        ogSize = playList.size();
        lastStored = (int) (Math.random() * ogSize);
        while (isDone) {
            if (numbers.contains(lastStored)) {
                lastStored = (int) (Math.random() * ogSize);
            } else {
                tempHolder.add(playList.get(lastStored));
                playList.remove(lastStored);
                playList.trimToSize();
                ogSize--;
                isDone = (ogSize >= tempHolder.size());
            }
        }
        playList = new ArrayList<>();
        for (int i = 0; i < tempHolder.size(); i++) {
            playList.add(tempHolder.get(i));

        }
        playList.trimToSize();
        System.out.println("DONE");
        return true;
    }

    public boolean createNewPlayList() {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < playList.size(); i++) {
                br.write((String) (((Media) playList.get(i)).getSource()));
                br.newLine();

            }
        } catch (IOException ex) {
            System.out.println("Shit went wrong yo! Writing ERROR");
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean savePlayList() {
        return true;
    }

    public Media nextInList() {
        currentPosition = (currentPosition >= playList.size()) ? 0 : currentPosition;
        return playList.get(currentPosition++);
    }

    public Media prevInList() {
        currentPosition = (currentPosition < 0) ? playList.size() - 1 : currentPosition;
        return playList.get(currentPosition--);
    }

    public boolean isPlayListFileEmpty(Queue play) {
        return play.isEmpty();
    }

    public void addToList() {
        String holder;
        File file = new File(fileChooser());
        holder = pathMaker(file);
        Media temp = new Media(holder);
        if (addToGenericPlayList(holder)) {
            playList.add(temp);
            playList.trimToSize();
        }

    }

    public void addMultipleToList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(null);
        fileChooser.setMultiSelectionEnabled(true);
        File[] fileMaster = fileChooser.getSelectedFiles();
//        File file = new File(pathName);
        for (File fileMaster1 : fileMaster) {
            String holder = pathMaker(fileMaster1);
            Media temp = new Media(holder);
            if (addToGenericPlayList(holder)) {
                playList.add(temp);
                playList.trimToSize();
            }
        }
    }

    public boolean addToGenericPlayList(String holder) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(path, true))) {
            br.append(holder);
            br.append('\n');

        } catch (IOException ex) {
            System.out.println("Shit went wrong yo! Writing ERROR");
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    private String pathMaker(File file) {
        return file.toURI().toString();
    }

    private String fileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(null);
        String pathName = fileChooser.getSelectedFile().getAbsolutePath();

        return pathName;
    }

}
