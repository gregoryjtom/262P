import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.regex.*;

public class Twelve {
  public static void main(String[] args) throws Exception{
    WordFrequencyController wfController = new WordFrequencyController();
    wfController.dispatch(new String[]{"init",args[0]});
    wfController.dispatch(new String[]{"run"});
  }

  private static class DataStorageManager{
    // models the contents of the file
    private ArrayList<String> data = new ArrayList<String>();

    public ArrayList<String> dispatch(String[] message) throws Exception{
      if (message[0].equals("init")){
        init(message[1]);
        return null;
      }
      else if (message[0].equals("words")){
        return words();
      }
      else{
        throw new Exception("Message not understood: " + message[0]);
      }
    }
    private void init(String path_to_file){
      // read words in file
      try {
        // analyze text file line by line
        File text_file = new File(path_to_file);
        Scanner myReader = new Scanner(text_file);
        while (myReader.hasNextLine()) {
          String line = myReader.nextLine().toLowerCase();
          // search for words
          Pattern pattern = Pattern.compile("[a-z]{2,}");
          Matcher matcher = pattern.matcher(line);
          while (matcher.find()) {
            String word = matcher.group();
            data.add(word);
          }
        }
        myReader.close();
      }
      catch (FileNotFoundException e){
        System.out.println("An error occured.");
        e.printStackTrace();
      }
    }

    private ArrayList<String> words(){
      // returns list words in storage 
      ArrayList<String> words = data;
      return words;
    }
  }

  private static class StopWordManager {
    // models the stop word filter
    private ArrayList<String> stop_words = new ArrayList<String>();

    public boolean dispatch(String[] message) throws Exception{
      if(message[0].equals("init")){
        init();
        return true;
      }
      else if (message[0].equals("is_stop_word")){
        return(is_stop_word(message[1]));
      }
      else{
        throw new Exception("Message not understood: " + message[0]);
      }
    }

    private void init(){
       try {
        // load stop words file
        File stop_words_file = new File("../stop_words.txt");
        Scanner myReader = new Scanner(stop_words_file);
        String list = "";
        // read the one line into a string and split
        while (myReader.hasNext()) {
          list = myReader.next();
        }
        String[] split_stop_words = list.split(",");
        // add each word to ArrayList
        for (int i = 0; i < split_stop_words.length; i++) {
          stop_words.add(split_stop_words[i]);
        }
        myReader.close();

      } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }

    private boolean is_stop_word(String word){
      return stop_words.contains(word);
    }
  }

  private static class WordFrequencyManager{
    // keeps the word frequency data
    private HashMap<String,Integer> word_freqs = new HashMap<String,Integer>();

    public ArrayList<Map.Entry<String,Integer>> dispatch(String[] message) throws Exception{
      if(message[0].equals("increment_count")){
        increment_count(message[1]);
        return null;
      }
      else if (message[0].equals("sorted")){
        return sorted();
      }
      else{
        throw new Exception("Message not understood: " + message[0]);
      }
    }

    private void increment_count(String word){
      if (word_freqs.containsKey(word)){
        word_freqs.put(word, word_freqs.get(word) + 1);
      }
      else{
        word_freqs.put(word,1);
      }
    }

    private ArrayList<Map.Entry<String,Integer>> sorted(){
      // create ArrayList to store sorted words
      ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

      for(Map.Entry<String, Integer> e: word_freqs.entrySet()) 
      {
          freq_list.add(e);
      }

      // create comparator to use to sort list
      Comparator<Map.Entry<String, Integer>> myComparator = new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(
                Map.Entry<String, Integer> e1,
                Map.Entry<String, Integer> e2) {

              Integer int1 = e1.getValue();
              Integer int2 = e2.getValue();
              return int2.compareTo(int1);
          }
      };

      // sort and return
      Collections.sort(freq_list,myComparator);
      return freq_list;
    }
  }

  private static class WordFrequencyController{
    // controls the messaging of different components

    private DataStorageManager storage_manager = new DataStorageManager();
    private StopWordManager stop_word_manager = new StopWordManager();
    private WordFrequencyManager word_freq_manager = new WordFrequencyManager();

    public void dispatch(String[] message) throws Exception{
      if (message[0].equals("init")){
        init(message[1]);
      }
      else if (message[0].equals("run")){
        run();
      }
      else{
        throw new Exception("Message not understood: " + message[0]);
      }
    }

    private void init(String path_to_file) throws Exception{
      storage_manager.dispatch(new String[]{"init",path_to_file});
      stop_word_manager.dispatch(new String[]{"init"});
    }

    private void run() throws Exception{
      for (String w: storage_manager.dispatch(new String[]{"words"})){
        if (!stop_word_manager.dispatch(new String[]{"is_stop_word",w})){
          word_freq_manager.dispatch(new String[]{"increment_count",w});
        }
      }

      ArrayList<Map.Entry<String,Integer>> freq_list = word_freq_manager.dispatch(new String[]{"sorted"});

      System.out.println("----TOP 25 WORDS----");
      int i = 1;
      for (Map.Entry<String, Integer> entry : freq_list) {
        if (i++ > 25) {
          break;
        }
        System.out.println(entry.getKey() + ": " + entry.getValue());
      }
    }
  }
}

