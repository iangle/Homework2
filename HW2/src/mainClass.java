import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class mainClass {

    public static void main(String args[]){ run(); }

    private static void run(){

        ReadFile newFile = new ReadFile("Genes_relation.data");
        ReadFile newFile2 = new ReadFile("Genes_relation.test");

        HashMap<String, ArrayList<String>> dataFile;
        HashMap<String, ArrayList<String>> testFile;
        HashMap<String,String> keyMap= getKey("keys.txt");

        ArrayList<String> localizationGuesses;
        ArrayList<String> fileOutput = new ArrayList<>();

        dataFile = preprocessData(newFile.getIncomingData(),"?",newFile.getColumnNames());
        testFile = preprocessData(newFile2.getIncomingData(),"?",newFile2.getColumnNames());

        localizationGuesses = KNN_algorithm(testFile,dataFile, newFile.getGeneIdMap(),7);

        for(int i = 0; i < localizationGuesses.size(); i++)
        {
            fileOutput.add("<" + testFile.get("GeneID").get(i) + ">" + ", " + "<" + localizationGuesses.get(i) + ">");
        }

        writeToFile(fileOutput,"output.txt");

        System.out.println("Accuracy of the algorithm: " + getAccuracy(testFile.get("GeneID"),localizationGuesses, keyMap));
    }


    /**
     *
     * @param theFile the hashmap that contains the contents of the file
     * @param missingValInd the string that determines what a missing value is
     * @param colNames the arraylist that contains the names of the columns
     * @return a hashmap that has it's missing values replace with 0's
     */
    private static HashMap<String, ArrayList<String>> preprocessData(HashMap<String, ArrayList<String>> theFile, String missingValInd, ArrayList<String> colNames){

        //go through every column
        for(int i = 0; i < colNames.size(); i++)
        {

            //starts at one because we know that the first index of the map contains the column names
            for(int j = 1; j < theFile.get(colNames.get(i)).size(); j++)
            {
                //if the string we are looking at is a missing value the replace it with a 0
                if(theFile.get(colNames.get(i)).get(j).equals(missingValInd))
                {

                    theFile.get(colNames.get(i)).remove(j);
                    theFile.get(colNames.get(i)).add(j,"0");
                }

            }
        }
        return theFile;
    }


    private static ArrayList<String> KNN_algorithm(HashMap<String, ArrayList<String>> testData, HashMap<String, ArrayList<String>> data, HashMap<String, String> GeneID, int kValue)
    {
        ArrayList<ArrayList<Integer>> similarValues = new ArrayList<>();

        ArrayList<String> localizationValues = new ArrayList<>();

        ArrayList<ArrayList<Integer>> indexValues = getSimilarValues(testData,data,kValue);

        for(int i = 0; i < testData.get("GeneID").size(); i++)
        {
                similarValues.add(indexValues.get(i));
        }

        for(int i = 0; i < similarValues.size(); i++)
        {
            String bestGuess = "";
            int prevOccurrence = 0;

            for(int j = 0; j < similarValues.get(i).size(); j++)
            {
                int numOccurrences = 0;

                for(int counter = 0; counter < kValue; counter++)
                {
                    if(counter != j)
                    {
                        if(testData.get("Localization").get(similarValues.get(i).get(j)).equals(testData.get("Localization").get(similarValues.get(i).get(counter))))
                        {
                            numOccurrences++;
                        }
                    }
                }

                if(numOccurrences >= prevOccurrence)
                {
                    bestGuess = GeneID.get(testData.get("Localization").get(similarValues.get(i).get(j)));
                }

                prevOccurrence = numOccurrences;

            }

            localizationValues.add(bestGuess);

        }


        return localizationValues;
    }

    /**
     *
     * @param fileContents an ArrayList that contains each line that is to be written to the file
     * @param fileName the name of the file we are writing to
     * code for the method from: https://www.w3schools.com/java/java_files_create.asp
     */
    private static void writeToFile(ArrayList<String> fileContents, String fileName){

        try{

            //create our fileWriter
            FileWriter myFileWriter = new FileWriter(fileName);

            //write each line of the arrayList to the file
            for(int i = 0; i < fileContents.size(); i++) {
                myFileWriter.write(fileContents.get(i));
                myFileWriter.write(System.getProperty("line.separator"));
            }

            //close the fileWriter
            myFileWriter.close();

            //print out a success statement
            System.out.println("Successfully wrote to: " + fileName);

        //catch an IOException error
        }catch (IOException e){
            System.out.println("sorry the program was unable to write to: " + fileName);
            e.printStackTrace();
        }
    }


    private static ArrayList<ArrayList<Integer>> getSimilarValues(HashMap<String, ArrayList<String>> testData, HashMap<String, ArrayList<String>> data, int kValue)
    {
        //a list of the similar localization values
        ArrayList<String> similarValues = new ArrayList<>();
        ArrayList<Double> jaccardValues = new ArrayList<>();

        ArrayList<ArrayList<Double>> jaccardList = new ArrayList<>();

        ArrayList<ArrayList<Integer>> largestNumbers = new ArrayList<>();

        for(int i = 0; i < testData.get("GeneID").size(); i++)
        {

            ArrayList<String> testRow = new ArrayList<>();
            ArrayList<String> dataRow = new ArrayList<>();

            jaccardValues.clear();

            testRow.add(testData.get("GeneID").get(i));
            testRow.add(testData.get("Essential").get(i));
            testRow.add(testData.get("Class").get(i));
            testRow.add(testData.get("Complex").get(i));
            testRow.add(testData.get("Phenotype").get(i));
            testRow.add(testData.get("Motif").get(i));
            testRow.add(testData.get("Chromosome").get(i));
            testRow.add(testData.get("Localization").get(i));

            for(int j = 0; j < testData.get("GeneID").size(); j++) {
                dataRow.add(data.get("GeneID").get(j));
                dataRow.add(data.get("Essential").get(j));
                dataRow.add(data.get("Class").get(j));
                dataRow.add(data.get("Complex").get(j));
                dataRow.add(data.get("Phenotype").get(j));
                dataRow.add(data.get("Motif").get(j));
                dataRow.add(data.get("Chromosome").get(j));
                dataRow.add(data.get("Localization").get(j));

                ArrayList<Integer> union = getUnion(testRow, dataRow);
                ArrayList<Integer> intersection = getIntersection(testRow, dataRow);

                double sum1 = 0;

                for (int k = 0; k < union.size(); k++) {
                    sum1 += Math.abs(union.get(k) * union.get(k));
                }

                double sum2 = 0;

                for (int k = 0; k < intersection.size(); k++) {
                    sum2 += Math.abs(intersection.get(k) * intersection.get(k));
                }

                sum1 = Math.sqrt(sum1);
                sum2 = Math.sqrt(sum2);

                jaccardValues.add(sum2 / sum1);
            }

            jaccardList.add(jaccardValues);


        }

        double largestNum = 0;

        int index = 0;

        ArrayList<Integer> tempList = new ArrayList<>();

        for(int i = 0; i < jaccardList.size(); i++)
        {
            for(int k = 0; k < kValue; k++)
            {
                for (int j = 0; j < jaccardList.get(i).size(); j++)
                {
                    if (jaccardList.get(i).get(j) > largestNum)
                    {
                        largestNum = jaccardList.get(i).get(j);
                        index = j;
                    }
                }

                tempList.add(index);
                jaccardList.get(i).remove(index);
            }

            largestNumbers.add(tempList);
            tempList.clear();

        }

        return largestNumbers;



        /*//gets rid of the g in front of the gene and transforms it to an integer
        char[] geneChar = currGeneID.toCharArray();
        char[] geneCharID = Arrays.copyOfRange(geneChar,1,6);
        currGeneID = String.valueOf(geneCharID);
        int testGene = Integer.parseInt(currGeneID);

        //the previous gene in the array
        int prevGene = testGene;

        //go through every gene and check for the closest ones to our test gene
        for(int i = 0; i < geneIDs.size(); i++)
        {
            //gets rid of the g in front of the gene and transforms it to an integer
            char[] tempArray = geneIDs.get(i).toCharArray();
            char[] tempChar = Arrays.copyOfRange(tempArray,1,6);
            String currID = String.valueOf(tempChar);
            int currGene = Integer.parseInt(currID);

            if(testGene >= prevGene && testGene <= currGene)
            {

                for(int j = 1; j <= kValue; j++)
                {
                    if(i-j > 0)
                    {
                        similarValues.add(String.valueOf(geneIDs.get(i - j)));
                    }else{
                        similarValues.add(String.valueOf(geneIDs.get(i)));
                    }
                }
            }

            prevGene = currGene;
        }

        return similarValues; */
    }


    /**
     *
     * @param GeneIDs the test GeneIDS
     * @param localizationGuesses the guesses for each localization
     * @param key a map containing the key values
     * @return a double the represents the accuracy
     */
    private static double getAccuracy(ArrayList<String> GeneIDs, ArrayList<String> localizationGuesses, HashMap<String,String> key)
    {
        double sum = 0;

        for(int i = 0; i < GeneIDs.size(); i++)
        {
            if(key.get(GeneIDs.get(i)).equals(localizationGuesses.get(i)))
            {
                sum++;
            }
        }

        return (sum / GeneIDs.size()) *100;
    }


    private static HashMap<String,String> getKey(String fileName)
    {

        ArrayList<String> file = new ArrayList<>();

        HashMap<String,String> temp = new HashMap<>();

        try
        {
            //get the file
            File myFile = new File(fileName);

            //open the scanner
            Scanner myScanner = new Scanner(myFile);

            //get each line of the file
            while(myScanner.hasNextLine()) {
                file.add(myScanner.nextLine());
            }

            //close the scanner
            myScanner.close();

            //catch a file not found exception if needed
        }catch(FileNotFoundException e){
            System.out.println("sorry but the program could not find the file path you specified");
            e.printStackTrace();
        }

        for(int i = 1; i < file.size(); i++)
        {
            String[] tempString = file.get(i).split(",");
            temp.put(tempString[0],tempString[1]);
        }

        return temp;
    }

    private static ArrayList<Integer> getUnion(ArrayList<String> list1, ArrayList<String> list2)
    {
        ArrayList<Integer> union = new ArrayList<>();

        for(int i = 0; i < list1.size(); i++)
        {
            union.add(1);
        }


        for(int i = 0; i < list2.size(); i++)
        {
            if(!list1.contains(list2.get(i)))
                union.add(1);
        }

        return union;


    }


    private static ArrayList<Integer> getIntersection(ArrayList<String> list1, ArrayList<String> list2)
    {

        ArrayList<Integer> intersection = new ArrayList<>();

        for(int i = 0; i < list2.size(); i++)
        {
            if(list1.contains(list2.get(i)))
                intersection.add(1);
        }

        return intersection;


    }



}
