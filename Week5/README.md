To run Seventeen:

1. Go to shell, and navigate to Week5 folder: `cd Week5`
2. Compile java file of interest: `javac Seventeen.java`
3. Run java file: `java Seventeen ../pride-and-prejudice.txt`

To run Twenty:

1. Go to shell, and navigate to Week5 folder: `cd Week5/Twenty`
2. Execute jar file `java -jar framework.jar ../../pride-and-prejudice.txt`

Note: the two ways to run are by modifying `config.properties` to have one of the following configurations:

1. ```
pathToJar=app1.jar  
nameOfWordClass=Words1
nameOfFreqClass=Frequencies1

2. ```
pathToJar=app2.jar
nameOfWordClass=Words2
nameOfFreqClass=Frequencies2


Sources:
- Reading files: https://www.w3schools.com/java/java_files_read.asp
- Sorting hashmap: https://www.educative.io/edpresso/how-to-sort-a-java-hashmap-by-value
- For Seventeen code:
https://github.com/crista/exercises-in-programming-style/blob/master/11-things/tf_10.java
- config:
https://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
