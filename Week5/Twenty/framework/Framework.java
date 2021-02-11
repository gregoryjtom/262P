import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class Framework {
    public static void main(String[] args){
        Class wordsCls = null;
        Class freqsCls = null;
        URL classUrl = null;

        // get configuration
        InputStream inputStream;
        String pathToJar = "";
        String nameOfWordClass = "";
        String nameOfFreqClass = "";
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = Framework.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property values
            pathToJar = prop.getProperty("pathToJar");
            nameOfWordClass = prop.getProperty("nameOfWordClass");
            nameOfFreqClass = prop.getProperty("nameOfFreqClass");
            inputStream.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        try{
            // find classes in jar file in config
            classUrl = new URL(pathToJar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        URL[] classUrls = {classUrl};
        URLClassLoader cloader = new URLClassLoader(classUrls);
        try{
            wordsCls = cloader.loadClass(nameOfWordClass);
            freqsCls = cloader.loadClass(nameOfFreqClass);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (wordsCls != null && freqsCls != null){
            try{
                Week5Words filter = (Week5Words) wordsCls.getDeclaredConstructor().newInstance();
                Week5Frequencies counter = (Week5Frequencies) freqsCls.getDeclaredConstructor().newInstance();
                ArrayList<Map.Entry<String,Integer>> word_freqs = counter.top25(filter.extract_words(args[0]));
                System.out.println("----TOP 25 WORDS----");
                for (Map.Entry<String, Integer> entry : word_freqs) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
