package com.project.PinCode_Distance;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DbManager implements Serializable {
    private static final String SERIALIZED_FILE_PATH = "C:\\Map\\map.ser";
    private Map<Pair<String, String>, Double> distanceMap = new HashMap<>();

    public DbManager() {
        try{
            BufferedReader br = new BufferedReader(new FileReader(SERIALIZED_FILE_PATH));
            if (!(br.readLine() == null)) {
                loadFromFile();
            }
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    public void addDistance(String pincode1, String pincode2, double distance) {
        Pair<String, String> pair = new Pair<>(pincode1, pincode2);
        distanceMap.put(pair, distance);
    }

    public boolean checkExist(String pincode1 , String pincode2){
        Pair<String, String> pair1 = new Pair<>(pincode1, pincode2);
        Pair<String, String> pair2 = new Pair<>(pincode2, pincode1);
        return (distanceMap.containsKey(pair1) ||  distanceMap.containsKey(pair2));
    }

    public Double getDistance(String pincode1, String pincode2) {
        Pair<String, String> pair1 = new Pair<>(pincode1, pincode2);
        Pair<String, String> pair2 = new Pair<>(pincode2, pincode1);
        if(distanceMap.containsKey(pair1)){
            return distanceMap.get(pair1);
        }
        return distanceMap.get(pair2);


    }

    public void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DbManager loadFromFile() {
        try (FileInputStream fileIn = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (DbManager) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class Pair<T, U> implements Serializable {
        private final T first;
        private final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public U getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) obj;
            if (!first.equals(pair.first)) return false;
            return second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            int result = first.hashCode();
            result = 31 * result + second.hashCode();
            return result;
        }
    }
}

