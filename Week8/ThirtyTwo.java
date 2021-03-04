import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ThirtyTwo {
    public static void main(String[] args){
        List<ArrayList<Object[]>> splits = partition(args[0],200).stream().map(new splitWords()).collect(Collectors.toList());
        HashMap<String,ArrayList<Object[]>> splitsPerWord = regroup(splits);
        List<Map.Entry<String,Integer>> wordFreqs = sortFrequencies(splitsPerWord.entrySet().stream().map(new countWords()).collect(Collectors.toList()));
        // print 25 most frequent entries
        System.out.println("----TOP 25 WORDS----");
        int i = 1;
        for (Map.Entry<String, Integer> entry : wordFreqs) {
            if (i++ > 25) {
                break;
            }
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static ArrayList<String> partition(String filepath, int nLines){
        ArrayList<String> lineGroups = new ArrayList<>();
        StringBuilder currentLineGroup = new StringBuilder();

        int i = 0;
        try {
            // analyze text file line by line
            File text_file = new File(filepath);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                currentLineGroup.append(line).append("\n");
                if (++i % nLines == 0){
                    lineGroups.add(currentLineGroup.toString());
                    currentLineGroup = new StringBuilder();
                }
            }
            if (!currentLineGroup.toString().equals("")){
                lineGroups.add(currentLineGroup.toString());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return lineGroups;
    }

    static class splitWords implements Function<String,ArrayList<Object[]>> {
        @Override
        public ArrayList<Object[]> apply(String s) {
            ArrayList<Object[]> result = new ArrayList<>();
            ArrayList<String> words = removeStopWords(scan(s));
            for (String w: words){
                result.add(new Object[]{w,1});
            }
            return result;
        }

        public ArrayList<String> scan(String data) {
            ArrayList<String> words = new ArrayList<>();
            Pattern pattern = Pattern.compile("[a-z]{2,}");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                String w = matcher.group();
                words.add(w);
            }
            return words;
        }

        public ArrayList<String> removeStopWords(ArrayList<String> wordList){
            // load stop words
            ArrayList<String> stop_words = new ArrayList<>();
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
                Collections.addAll(stop_words, split_stop_words);
                myReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            ArrayList<String> filteredWords = new ArrayList<>();
            for (String word: wordList) {
                if (!stop_words.contains(word)){
                    filteredWords.add(word);
                }
            }
            return filteredWords;
        }
    }

    public static HashMap<String,ArrayList<Object[]>> regroup(List<ArrayList<Object[]>> pairsList){
        HashMap<String,ArrayList<Object[]>> resultMap = new HashMap<>();

        for (ArrayList<Object[]> pairs: pairsList){
            for (Object[] p: pairs){
                if (resultMap.containsKey(p[0])){
                    resultMap.get(p[0]).add(p);
                }
                else{
                    resultMap.put((String) p[0],new ArrayList<>(List.<Object[]>of(p)));
                }
            }
        }
        return resultMap;
    }

    static class countWords implements Function<Map.Entry<String,ArrayList<Object[]>>,Map.Entry<String,Integer>>{
        @Override
        public Map.Entry<String, Integer> apply(Map.Entry<String, ArrayList<Object[]>> mapping) {
            HashMap<String,Integer> result = new HashMap<>();
            Object[] reduceResult = mapping.getValue().stream().reduce(new Object[]{"",0},(a,b) -> new Object[]{"",(int) a[1] + (int) b[1]});
            result.put(mapping.getKey(),(int) reduceResult[1]);
            Map.Entry<String,Integer> entry = null;
            for (Map.Entry<String,Integer> e: result.entrySet()){
                entry = e;
            }
            return entry;
        }
    }

    private static List<Map.Entry<String,Integer>> sortFrequencies(List<Map.Entry<String,Integer>> freq_list){
        // create comparator to use to sort list
        Comparator<Map.Entry<String, Integer>> myComparator = (e1, e2) -> {

            Integer int1 = e1.getValue();
            Integer int2 = e2.getValue();
            return int2.compareTo(int1);
        };

        // sort and return
        freq_list.sort(myComparator);
        return freq_list;
    }

}
