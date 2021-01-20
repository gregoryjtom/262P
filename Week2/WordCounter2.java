import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;

class WordCounter2 {
  public static void main(String[] args){
      // global list of <word,frequency> pairs
      ArrayList<Map.Entry<String,Integer>> word_freqs = new ArrayList<>();

      // TO DO: load stop words

       // make HashMap for frequencies
    HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
    try {
      // analyze text file line by line
      File text_file = new File(args[0]);
      Scanner myReader = new Scanner(text_file);
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine().toLowerCase();
        int start_char = -1;
        int i = 0;
        for (char c: line.toCharArray()){
          if (start_char == -1){
            if (Character.isLetterOrDigit(c)){
              // found start of a word
              start_char = i;
            }
          }
          else{
            if (!Character.isLetterOrDigit(c)){
              // found end of word
              boolean found = false;
              String word = line.substring(start_char,i);
              // TO DO: ignore stop words
              for (Map.Entry<String,Integer> entry: word_freqs){
                if (word == entry.getKey()){
                  entry.setValue(entry.getValue()+1);
                  found = true;
                  break;
                }
                // TO DO: keep track of the entries index in the arraylist
              }
              if (!found){
                word_freqs.add(new Map.Entry<String,Integer>(word,1));
              }
              else if (word_freqs.size() > 1){
                // TO DO: reordering functionality
              }
              start_char = -1;
            }
          }
          i++;
        }
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    // TO DO: print the first 25 words from word_freqs
  }    
  

  private static ArrayList<String> loadStopWords(){
    ArrayList<String> stop_words = new ArrayList<String>();
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
    return stop_words;
  }
}