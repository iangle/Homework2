import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReadFile {

    /* a hashmap that will hold the each column of the data. the key is the name of the column */
    private HashMap<String, ArrayList<String>> incomingData = new HashMap<>();

    /*an arraylist that holds each line of the file so we can split it by commas*/
    private ArrayList<String> _fileLines = new ArrayList<>();

    private ArrayList<String> columnNames = new ArrayList<>();

    /**
     *
     * @param fileLocation the location of the file we are reading in
     * the constructor for this class, reads the file into the class and updates incoming data
     * code for the reader from: https://www.w3schools.com/java/java_files_read.asp
     */
    public ReadFile(String fileLocation) {
        assert(fileLocation != null);

        _fileLines = getFile(fileLocation);

        incomingData = getColumns(_fileLines);

    }

    /**
     *
     * @param fileLocation the location of the file as a string
     * @return an arraylist that contains each line of the file
     */
    private ArrayList<String> getFile(String fileLocation){

        ArrayList<String> fileLines = new ArrayList<>();

        try{
            //get the file
            File myFile = new File(fileLocation);

            //open the scanner
            Scanner myScanner = new Scanner(myFile);

            //get each line of the file
            while(myScanner.hasNextLine()) {
                fileLines.add(myScanner.nextLine());
            }

            //close the scanner
            myScanner.close();

            //catch a file not found exception if needed
        }catch(FileNotFoundException e){
            System.out.println("sorry but the program could not find the file path you specified");
            e.printStackTrace();
        }

        return fileLines;
    }


    //GeneID,Essential,Class,Complex,Phenotype,Motif,Chromosome,Function,Localization
    private HashMap<String, ArrayList<String>> getColumns(ArrayList<String> fileLines){

        //the hashmap that we are going to fill and return
        HashMap<String,ArrayList<String>> data = new HashMap<>();

        //all the arraylists that will hold each column
        ArrayList<String> geneIDs = new ArrayList<>();
        ArrayList<String> essentials = new ArrayList<>();
        ArrayList<String> classes = new ArrayList<>();
        ArrayList<String> complexes = new ArrayList<>();
        ArrayList<String> phenotypes = new ArrayList<>();
        ArrayList<String> motifs = new ArrayList<>();
        ArrayList<String> chromosomes = new ArrayList<>();
        //ArrayList<String> functions = new ArrayList<>();
        ArrayList<String> localizations = new ArrayList<>();

        //get the first line of the file, this is the name of each column
        String firstLine = fileLines.get(0);

        //get the first line and split it by commas to get each name of the column
        String[] parsedFirstLine = firstLine.split(",");

        //fill columnNames with the names of the columns
        for(int i = 0; i < parsedFirstLine.length; i++)
            columnNames.add(parsedFirstLine[i]);

        //we start at 1 to keep from getting the class labels
        for(int i = 1; i < fileLines.size(); i++){

            //split the line by commas
            String[] parsedLine = fileLines.get(i).split(",");

            //add each data variable on each line to the correct arraylist
            geneIDs.add(parsedLine[0]);
            essentials.add(parsedLine[1]);
            classes.add(parsedLine[2]);
            complexes.add(parsedLine[3]);
            phenotypes.add(parsedLine[4]);
            motifs.add(parsedLine[5]);
            chromosomes.add(parsedLine[6]);
            //functions.add(parsedLine[7]);
            localizations.add(parsedLine[8]);
        }

        //fill up the map with the values we got from the file
        data.put(parsedFirstLine[0],geneIDs);
        data.put(parsedFirstLine[1],essentials);
        data.put(parsedFirstLine[2],classes);
        data.put(parsedFirstLine[3],complexes);
        data.put(parsedFirstLine[4],phenotypes);
        data.put(parsedFirstLine[5],motifs);
        data.put(parsedFirstLine[6],chromosomes);
        //data.put(parsedFirstLine[7],functions);
        data.put(parsedFirstLine[8],localizations);

        //return the map
        return data;
    }

    /**
     *
     * @return a hashmap containing each column of the data, the key is the name of the column
     */
    public HashMap<String, ArrayList<String>> getIncomingData(){
        assert (!incomingData.isEmpty());

        return incomingData;
    }

    /**
     *
     * @return an arraylist that contains the names of the columns
     */
    public ArrayList<String> getColumnNames(){ assert (!columnNames.isEmpty()); return columnNames; }


    /**
     *
     * @return a hash map using the geneID as the key and the localization value as it object
     */
    public HashMap<String,String> getGeneIdMap ()
    {
        assert (!incomingData.isEmpty());

        HashMap<String,String> geneMap = new HashMap<>();

        for(int i = 0; i < incomingData.get("GeneID").size(); i++)
        {
            geneMap.put(incomingData.get("GeneID").get(i),incomingData.get("Localization").get(i));
        }

        return geneMap;
    }

}
