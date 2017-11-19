package edu.macalester.comp124.audiofingerprinter;

import java.io.File;
import java.util.*;

/**
 * Created by hferguso on 4/19/16.
 */
public class Fingerprinter implements AudioFingerprinter {

    private SongDatabase db;
    private HashMap<String,Integer> matchMap/* = new HashMap<>()*/;
    public static final int[] RANGE = new int[] {40,80,120,180, 301};

    public Fingerprinter(SongDatabase db) {
        this.db = db;
    }

    public SongDatabase getSongDB() {
        return db;
    }
    @Override
    public List<String> recognize(byte[] audioData) {
        matchMap = new HashMap<>();
        List<String> songNames = new ArrayList<>();
        double[][]frequencyDomains = db.convertToFrequencyDomain(audioData);
        long[][] keyPoints = determineKeyPoints(frequencyDomains);
        for(long[] point: keyPoints) {
            long hashCode = hash(point);
            List<DataPoint> matcher = db.getMatchingPoints(hashCode);
            if(matcher != null) {
                for(DataPoint dp:matcher) {
                    String matchedSong = db.getSongName(dp.getSongId());
                    if (matchMap.containsKey(matchedSong)) {
                        matchMap.put(matchedSong, matchMap.get(matchedSong) + 1);
                        //System.out.println("Found: " + matchedSong + " " + matchMap.get(matchedSong) + " times.");
                    } else {
                        matchMap.put(matchedSong, 1);
                    }
                }
            }
        }
        for(String name:matchMap.keySet()){
            songNames.add(name);
        }
        Collections.sort(songNames, new Comparator<String>() {
            public int compare(String a, String b) {
                return matchMap.get(b).compareTo(matchMap.get(a));
            }
        });
        List<String> nameTimes = new ArrayList<>();
        for(String name : songNames){
            nameTimes.add(name+": with "+matchMap.get(name)+" matches.");
        }
        return nameTimes;
    }

    @Override
    public List<String> recognize(File fileIn) {
        byte[] data = db.getRawData(fileIn);
        List<String> processedData = recognize(data);
        return processedData;
    }

    @Override
    public long[][] determineKeyPoints(double[][] results) {
        int times = 0;
        long[][] keyPoints = new long[results.length][5];
        for(double[] frequencies:results){
            double freq1=0;
            double freq2=0;
            double freq3=0;
            double freq4=0;
            double freq5=0;
            double mag1=0;
            double mag2=0;
            double mag3=0;
            double mag4=0;
            double mag5=0;
            for(int freq=30;freq<300;freq++){
                double re = frequencies[2*freq];
                double im = frequencies[2*freq+1];
                double mag = Math.log(Math.sqrt(re * re + im * im) + 1);
                if(getIndex(freq)==0){
                    if (mag>mag1){
                        freq1 = freq;
                        mag1 = mag;
                    }
                } else if (getIndex(freq)==1){
                    if (mag>mag2){
                        freq2 = freq;
                        mag2 = mag;
                    }
                }else if (getIndex(freq)==2){
                    if (mag>mag3){
                        freq3 = freq;
                        mag3 = mag;
                    }
                }else if (getIndex(freq)==3){
                    if (mag>mag4){
                        freq4 = freq;
                        mag4 = mag;
                    }
                }else if (getIndex(freq)==4){
                    if (mag>mag5){
                        freq5 = freq;
                        mag5 = mag;
                    }
                }

            }
            keyPoints[times] = new long[] {(long)freq1, (long)freq2, (long)freq3, (long)freq4, (long)freq5};
            times++;
        }

        return keyPoints;
    }

    @Override
    public long hash(long[] points) {
        long hc = 0;
        int n = 0;
        for(long num: points) {
            hc += (num * Math.pow(1000,n));
            n++;
        }
        return hc;
    }

    public int getNumTimesFound(String songId) {
        return matchMap.get(songId);
    }

    //Find out in which range
    public static int getIndex(int freq) {
        int i = 0;
        while(RANGE[i] < freq){
            i++;
        }
        return i;
    }

    public void showSongLibrary(){
        ArrayList<String> temp = new ArrayList<String>();
        for(String str:matchMap.keySet()){
            temp.add(str);
        }
        Collections.sort(temp);
        Iterator<String> iterator = temp.iterator();
        while(iterator.hasNext()){
            String s = iterator.next();
            int in = matchMap.get(s);
            System.out.println(in);
        }
    }


}
