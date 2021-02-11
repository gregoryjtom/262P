import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Uses source code from https://github.com/crista/exercises-in-programming-style/blob/master/11-things/tf_10.java
 */

public class Seventeen {
    /*
     * The main function
     */
    public static void main(String[] args) throws IOException {
        new WordFrequencyController(args[0]).run();
        System.out.println("--------------------");
        System.out.println("Enter a class to inspect: ");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();
        System.out.println("Information about " + name + ": ");
        getInfo(name);
    }
    private static void getInfo(String name){
        Class cls = null;
        try{
            cls = Class.forName(name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (cls != null){
            System.out.println("Fields:");
            Field[] fields = cls.getDeclaredFields();
            for (Field f: fields){
                System.out.println("Found field: " + f.getName());
            }
            System.out.println("Methods:");
            Method[] methods = cls.getDeclaredMethods();
            for (Method m: methods){
                System.out.println("Found method: " + m.getName());
            }
            System.out.println("Superclass:");
            Class superCls = cls.getSuperclass();
            if (superCls != null) {
                System.out.println("Found superclass: " + superCls.getName());
            }
            System.out.println("Interfaces:");
            Class[] interfaces = cls.getInterfaces();
            for (Class iface: interfaces){
                System.out.println("Found interface: " + iface.getName());
            }
        }
        else{
            System.out.println("Could not find class.");
        }
    }
}

/*
 * The classes
 */

abstract class TFExercise {
    public String getInfo() {
        return this.getClass().getName();
    }
}

class WordFrequencyController extends TFExercise {
    private DataStorageManager storageManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFreqManager;

    public WordFrequencyController(String pathToFile) throws IOException {
        this.storageManager = new DataStorageManager(pathToFile);
        this.stopWordManager = new StopWordManager();
        this.wordFreqManager = new WordFrequencyManager();
    }

    public void run() {
        // initialize classes
        Class storageCls = storageManager.getClass();
        Class stopCls = stopWordManager.getClass();
        Class freqCls = wordFreqManager.getClass();
        Class freqPair = null;
        try {
            freqPair = Class.forName("WordFrequencyPair");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method getWords = null;
        Method isStopWord = null;
        Method incCount = null;
        Method sorted = null;
        Method getWord = null;
        Method getFreq = null;

        try{
            // initialize methods
            getWords = storageCls.getDeclaredMethod("getWords");
            isStopWord = stopCls.getDeclaredMethod("isStopWord",String.class);
            incCount = freqCls.getDeclaredMethod("incrementCount", String.class);
            sorted = freqCls.getDeclaredMethod("sorted");
            getWord = freqPair.getDeclaredMethod("getWord");
            getFreq = freqPair.getDeclaredMethod("getFrequency");

            for (String word: (List<String>) getWords.invoke(storageManager)){
                if (!(boolean)isStopWord.invoke(stopWordManager,word)){
                    incCount.invoke(wordFreqManager,word);
                }
            }

            int numWordsPrinted = 0;
            for (WordFrequencyPair pair : (List<WordFrequencyPair>) sorted.invoke(wordFreqManager)) {
                System.out.println(getWord.invoke(pair) + " - " + getFreq.invoke(pair));

                numWordsPrinted++;
                if (numWordsPrinted >= 25) {
                    break;
                }
            }
        } catch (Exception e){
            System.out.println("Method not found.");
        }
    }
}

/** Models the contents of the file. */
class DataStorageManager extends TFExercise {
    private List<String> words;

    public DataStorageManager(String pathToFile) throws IOException {
        this.words = new ArrayList<String>();

        Scanner f = new Scanner(new File(pathToFile), "UTF-8");
        try {
            f.useDelimiter("[\\W_]+");
            while (f.hasNext()) {
                this.words.add(f.next().toLowerCase());
            }
        } finally {
            f.close();
        }
    }

    public List<String> getWords() {
        return this.words;
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.words.getClass().getName();
    }
}

/** Models the stop word filter. */
class StopWordManager extends TFExercise {
    private Set<String> stopWords;

    public StopWordManager() throws IOException {
        this.stopWords = new HashSet<String>();

        Scanner f = new Scanner(new File("../stop_words.txt"), "UTF-8");
        try {
            f.useDelimiter(",");
            while (f.hasNext()) {
                this.stopWords.add(f.next());
            }
        } finally {
            f.close();
        }

        // Add single-letter words
        for (char c = 'a'; c <= 'z'; c++) {
            this.stopWords.add("" + c);
        }
    }

    public boolean isStopWord(String word) {
        return this.stopWords.contains(word);
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.stopWords.getClass().getName();
    }
}

/** Keeps the word frequency data. */
class WordFrequencyManager extends TFExercise {
    private Map<String, MutableInteger> wordFreqs;

    public WordFrequencyManager() {
        this.wordFreqs = new HashMap<String, MutableInteger>();
    }

    public void incrementCount(String word) {
        MutableInteger count = this.wordFreqs.get(word);
        if (count == null) {
            this.wordFreqs.put(word, new MutableInteger(1));
        } else {
            count.setValue(count.getValue() + 1);
        }
    }

    public List<WordFrequencyPair> sorted() {
        List<WordFrequencyPair> pairs = new ArrayList<WordFrequencyPair>();
        for (Map.Entry<String, MutableInteger> entry : wordFreqs.entrySet()) {
            pairs.add(new WordFrequencyPair(entry.getKey(), entry.getValue().getValue()));
        }
        Collections.sort(pairs);
        Collections.reverse(pairs);
        return pairs;
    }

    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.wordFreqs.getClass().getName();
    }
}

class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

class WordFrequencyPair implements Comparable<WordFrequencyPair> {
    private String word;
    private int frequency;

    public WordFrequencyPair(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    public int compareTo(WordFrequencyPair other) {
        return this.frequency - other.frequency;
    }
}
