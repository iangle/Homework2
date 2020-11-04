import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class main {

    public static void main(String args[]){
        ReadFile newFile = new ReadFile("Genes_relation.data");

        HashMap<String,ArrayList<String>> tempMap;

        tempMap = preprocessData(newFile.getIncomingData(),"?", newFile.getColumnNames());

        writeToFile(tempMap.get("Complex"), "output.txt");


    }

    private static void run(){ }


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

            //starts at one because we know that the first index of the map contains that column names
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


    private static String KNN_algorithm(){ return ""; }

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


}
