package edu.macalester.comp124.audiofingerprinter;

import java.io.File;
import java.util.List;

/**
 * Simple fingerprinter to test your audio fingerprinter implementation. Performs everything synchronously.
 * Created by bjackson on 11/15/2015.
 */
public class SimpleFingerprinter {

    public static void main(String[] args){

        //TODO: change path to a directory containing mp3s on your harddrive
        String path = "C:\\Users\\Henry\\Music\\hw7Music";
        System.out.println("Loading db...");

        SongDatabase db = new SongDatabase();
        //TODO: initialize the AudioFingerPrinter with your implementing class and pass it the song database.
        AudioFingerprinter rec = new Fingerprinter(db);
        db.setFingerprinter(rec);
        db.loadDatabase(path);
        //TODO: Change song to a song on your harddrive.
        String song = "C:\\Users\\Henry\\Music\\hw7Music\\Kosta_T_-_50_-_Moon_Valley.mp3";
        System.out.println("Recognizing...");
        File fileIn = new File(song);
        List<String> results = rec.recognize(fileIn);
        int i = 1;
        System.out.println("Found "+results.size()+" results.");
        for(String s : results){
            System.out.println(i + ": "+ s);
            i++;
        }
    }
}
