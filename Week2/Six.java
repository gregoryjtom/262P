import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.regex.*;

class Six {
  public static void main(String[] args) {
    Six s = new Six();
    s.printTop25(s.sortFrequencies(s.countWordsInFile(args[0],s.loadStopWords())));
  }

  private ArrayList<String> loadStopWords() {
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

  private HashMap<String, Integer> countWordsInFile(String filepath, ArrayList<String> stop_words) {
    // make HashMap for frequencies
    HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
    try {
      // analyze text file line by line
      File text_file = new File(filepath);
      Scanner myReader = new Scanner(text_file);
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine().toLowerCase();
        // search for words
        Pattern pattern = Pattern.compile("[a-z]{2,}");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
          String w = matcher.group();
          if (!stop_words.contains(w)) {
            if (frequencies.containsKey(w)) {
              frequencies.put(w, frequencies.get(w) + 1);
            } else {
              frequencies.put(w, 1);
            }
          }
        }
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return frequencies;
  }

  private ArrayList<Map.Entry<String,Integer>> sortFrequencies(HashMap<String,Integer> frequencies){
    // create ArrayList to store sorted list
    ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

    // put all HashMap entries into freq_list
    for(Map.Entry<String, Integer> e: frequencies.entrySet()) 
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

  private void printTop25(ArrayList<Map.Entry<String,Integer>> freq_list){
    // print 25 most frequent entries
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