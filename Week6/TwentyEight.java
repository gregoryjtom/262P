import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TwentyEight {
        public static void main(String[] args){
        File text_file = new File(args[0]);
        Scanner reader = null;
        try {
            reader = new Scanner(text_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LineGen startGen = new LineGen(reader);
        FilterWords filter1 = new FilterWords(startGen);
        FilterNonStopWords filter2 = new FilterNonStopWords(filter1);
        CountAndSort filter3 = new CountAndSort(filter2);

        int i = 1;
        while (filter3.hasNext()){
            System.out.println("-----------------");
            System.out.println("Iteration " + i++ + ":");

            ArrayList<Map.Entry<String,Integer>> freq_list = filter3.next();
            int j = 1;
            for (Map.Entry<String, Integer> entry : freq_list) {
                if (j++ > 25) {
                    break;
                }
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        reader.close();
    }
}

class LineGen implements Iterator<String> {
    private Scanner myReader;

    public LineGen(Scanner reader){
        myReader = reader;
    }

    @Override
    public boolean hasNext() {
        return myReader.hasNextLine();
    }

    @Override
    public String next() {
        return myReader.nextLine().toLowerCase();
    }
}

class FilterWords implements Iterator<String>{
    private Iterator<String> previous;
    private Pattern pattern;
    private Matcher matcher;
    private String nextWord;

    public FilterWords(Iterator<String> p){
        previous = p;
        pattern = Pattern.compile("[a-z]{2,}");
        matcher = pattern.matcher(previous.next());
    }

    @Override
    public boolean hasNext() {
        // previous generator has more lines or current line has more words
        if (matcher.find()){
            nextWord = matcher.group();
            return true;
        }
        else if (previous.hasNext()){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String next() {
        if (!nextWord.equals("")){
            String word = nextWord;
            nextWord = "";
            return word;
        }
        else {
            boolean foundWordOnNewLine = false;
            // find next line with a word, then return next word
            while (previous.hasNext()){
                matcher = pattern.matcher(previous.next());
                if (matcher.find()){
                    foundWordOnNewLine = true;
                    break;
                }
            }
            if (foundWordOnNewLine){
                return matcher.group();
            }
            else {
                return null;
            }
        }
    }
}

class FilterNonStopWords implements Iterator<String>{
    private Iterator<String> previous;
    private ArrayList<String> stop_words = new ArrayList<>();

    public FilterNonStopWords(Iterator<String> p){
        previous = p;
        loadStopWords();
    }

    private void loadStopWords(){
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

    @Override
    public boolean hasNext() {
        return previous.hasNext();
    }

    @Override
    public String next() {
        String word;
        do {
            word = previous.next();
        } while (stop_words.contains(word) && previous.hasNext());
        return word;
    }
}

class CountAndSort implements Iterator<ArrayList<Map.Entry<String,Integer>>>{
    private Iterator<String> previous;
    private HashMap<String,Integer> frequencies = new HashMap<>();
    private int count = 1;

    public CountAndSort(Iterator<String> p){
        previous = p;
    }

    @Override
    public boolean hasNext() {
        return previous.hasNext();
    }

    @Override
    public ArrayList<Map.Entry<String, Integer>> next() {
        while (count % 5000 != 0 && previous.hasNext()){
            String word = previous.next();
            if (frequencies.containsKey(word)){
                frequencies.put(word, frequencies.get(word) + 1);
            }
            else{
                frequencies.put(word,1);
            }
            count++;
        }
        count = 1;
        return sorted(frequencies);
    }

    private ArrayList<Map.Entry<String,Integer>> sorted(HashMap<String,Integer> frequencies){
        ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();
        // put all HashMap entries into freq_list
        for(Map.Entry<String, Integer> e: frequencies.entrySet())
        {
            freq_list.add(e);
        }

        // create comparator to use to sort list
        Comparator<Map.Entry<String, Integer>> myComparator = (e1, e2) -> {
            Integer int1 = e1.getValue();
            Integer int2 = e2.getValue();
            return int2.compareTo(int1);
        };

        // sort and return
        Collections.sort(freq_list,myComparator);
        return freq_list;
    }
}