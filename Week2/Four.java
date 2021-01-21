import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;

class Four {
  public static void main(String[] args){
    // global list of <word,frequency> pairs
    ArrayList<Map.Entry<String,Integer>> word_freqs = new ArrayList<Map.Entry<String,Integer>>();
    // stop words list
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
      // analyze text file line by line
      File text_file = new File(args[0]);
      myReader = new Scanner(text_file);
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine().toLowerCase();
        line = line.concat("*"); // to mark the end of the line
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
              // ignore stop words
              if (!stop_words.contains(word) && word.length() > 1){
                int pair_index = 0;
                for (Map.Entry<String,Integer> entry: word_freqs){
                  // why does this not get triggered??
                  if (word.equals(entry.getKey())){
                    entry.setValue(entry.getValue()+1);
                    found = true;
                    break;
                  }
                  pair_index++;
                }
                if (!found){
                  // make hashmap to create Map.Entry value
                  HashMap<String, Integer> entries = new HashMap<String, Integer>();
                  entries.put(word,1);
                  Map.Entry<String,Integer> newEntry = null;
                  for(Map.Entry<String, Integer> e: entries.entrySet()) 
                  {
                      newEntry = e;
                  }
                  word_freqs.add(newEntry);
                }
                else if (word_freqs.size() > 1){
                  // reorder if needed
                  for (int j = pair_index; j >= 0; j--){
                    if (word_freqs.get(pair_index).getValue() > word_freqs.get(j).getValue()){
                      // swap
                      Map.Entry<String,Integer> temp = word_freqs.get(pair_index);
                      word_freqs.set(pair_index, word_freqs.get(j));
                      word_freqs.set(j,temp);
                      pair_index = j;
                    }
                  }
                } 
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
    // print the first 25 words from word_freqs
    System.out.println("----TOP 25 WORDS----");
    int i = 1;
    for (Map.Entry<String, Integer> entry : word_freqs) {
      if (i++ > 25) {
        break;
      }
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
  }    

}