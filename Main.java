package scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Reads path of csv file from command line argument
 * Parses file and reads data into database
 *
 * format for a class in a data file should be:
 * classNumber,classType,waitlist["true" or "false],auxiliary["true" or "false"],
 * auxiliaryClassNumbers["num1,num2,..."],begin[military time],end[military time],
 * days["mo,[bool],tu,[bool],we,[bool],th,[bool],fr[bool]"]
 */
public class Main {

    public static void main(String[] args) {
	    //parse csv from command line

        //verify that the command line argument exists
        if (args.length == 0) {
            System.err.println("Usage Error: the program expects file name as an argument.\n");
            System.exit(1);
        }

        //verify that command line argument contains a name of an existing file
        File dataFile = new File(args[0]);
        if (!dataFile.exists()) {
            System.err.println("Error: the file " + dataFile.getAbsolutePath() + " does not exist.\n");
            System.exit(1);
        }
        if (!dataFile.canRead()) {
            System.err.println("Error: the file " + dataFile.getAbsolutePath() +
                    " cannot be opened for reading.\n");
            System.exit(1);
        }

        //open the file for reading
        Scanner inData = null;


        try {
            inData = new Scanner(dataFile);
        } catch (FileNotFoundException e) {
            System.err.println("Error: the file " + dataFile.getAbsolutePath() +
                    " cannot be opened for reading.\n");
            System.exit(1);
        }

        //read the content of the file and save the data
        //classTable stores all class data, with the key being the class number (unique identifier)
        //and the value being the ClassObject
        HashMap<String, ClassObject> classTable = new HashMap<>();
        String line = null;
        while (inData.hasNextLine()) {
            try {
                line = inData.nextLine();

                //parse line and add to dataset
                ArrayList<String> current = splitCSVLine(line);

                classTable.put(current.get(0), new ClassObject(current.get(1),current.get(2),current.get(3),
                        current.get(4),current.get(5),current.get(6),current.get(7)));

            } catch (IllegalArgumentException ex) {
                //caused by an incomplete or miss-formatted line in the input file
                continue;
            }

        }

        //process the list of ClassObjects into their final state
        //this involves setting auxiliary classes and figuring out class conflicts
        for (HashMap.Entry<String, ClassObject> entry: classTable.entrySet()) {
            String key = entry.getKey();
            ClassObject value = entry.getValue();
            //set conflict map
            value.setConflictMap(classTable.values());
            //set auxiliary classes
            for (String classKey : value.auxiliaryClassNumbers) {
                value.auxiliaryClasses.add(classTable.get(classKey));
            }
        }

        //TODO IMPLEMENT SCHEDULE GENERATOR
    }

    /**
     * Splits the given line of a CSV file according to commas and double quotes
     * (double quotes are used to surround multi-word entries so that they may contain commas)
     * @author Joanna Klukowska
     * @param textLine	a line of text to be passed
     * @return an Arraylist object containing all individual entries found on that line
     */
    public static ArrayList<String> splitCSVLine(String textLine){

        ArrayList<String> entries = new ArrayList<String>();
        int lineLength = textLine.length();
        StringBuffer nextWord = new StringBuffer();
        char nextChar;
        boolean insideQuotes = false;
        boolean insideEntry= false;

        // iterate over all characters in the textLine
        for (int i = 0; i < lineLength; i++) {
            nextChar = textLine.charAt(i);

            // handle smart quotes as well as regular quotes
            if (nextChar == '"' || nextChar == '\u201C' || nextChar =='\u201D') {

                // change insideQuotes flag when nextChar is a quote
                if (insideQuotes) {
                    insideQuotes = false;
                    insideEntry = false;
                }else {
                    insideQuotes = true;
                    insideEntry = true;
                }
            } else if (Character.isWhitespace(nextChar)) {
                if ( insideQuotes || insideEntry ) {
                    // add it to the current entry
                    nextWord.append( nextChar );
                }else { // skip all spaces between entries
                    continue;
                }
            } else if ( nextChar == ',') {
                if (insideQuotes){ // comma inside an entry
                    nextWord.append(nextChar);
                } else { // end of entry found
                    insideEntry = false;
                    entries.add(nextWord.toString());
                    nextWord = new StringBuffer();
                }
            } else {
                // add all other characters to the nextWord
                nextWord.append(nextChar);
                insideEntry = true;
            }

        }
        // add the last word ( assuming not empty )
        // trim the white space before adding to the list
        if (!nextWord.toString().equals("")) {
            entries.add(nextWord.toString().trim());
        }

        return entries;
    }
}
