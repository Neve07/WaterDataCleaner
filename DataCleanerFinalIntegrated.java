import java.util.ArrayList;
import java.util.Scanner;


import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;


class DataCleaner{
        
        ////////////////////////////////////////////////////////////////////////////////////
        ///                                    Main                                      ///
        ////////////////////////////////////////////////////////////////////////////////////
        public static void main(String[] args) throws IOException{
                welcome();  //hi.
                
                
                String path = existingFile("csv", 'r');
                String[][] fileData = readCsvToArray(path);
                
                System.out.println("Clustering by Zone...");
                System.out.println("0%");
                ArrayList <String[][]> clusteredData = clusterize(fileData, 0);
                System.out.println("100%");
                System.out.println("Successfully clustered by Zone.");
                
                System.out.println("Clustering by Date...");
                System.out.println("0%");
                ArrayList <ArrayList<String[][]>> dateClusteredData = new ArrayList<ArrayList<String[][]>>();
                for (int h = 0; h < clusteredData.size(); h++){
                        dateClusteredData.add(clusterize(clusteredData.get(h), 1));
                        System.out.println((((h+1) * 100) / clusteredData.size()) + "%");
                }
                System.out.println("Successfully clustered by Date.");
                
                System.out.println("Sorting by Date.");
                System.out.println("0%");
                for (int h = 0; h < dateClusteredData.size(); h++){
                        for (int i = 0; i < dateClusteredData.get(h).size(); i++){
                                dateClusteredData.set(h, insertionSorte(dateClusteredData.get(h)));
                                System.out.println(((h) * 100 / dateClusteredData.size() + ((i+1) * 20) / dateClusteredData.get(h).size()) + "%" + " ...Sorting Cluster " + (h+1) + ": " + (((i+1) * 100) / dateClusteredData.get(h).size()) + "%");
                        }
                        System.out.println((((h+1) * 100) / dateClusteredData.size()) + "%");
                }
                ArrayList<ArrayList<ArrayList<String[][]>>> fullyClusteredData = new ArrayList<ArrayList<ArrayList<String[][]>>>();
                
                for (int h = 0; h < dateClusteredData.size(); h++){
                        
                        ArrayList<ArrayList<String[][]>> tempArray = new ArrayList<ArrayList<String[][]>>();
                        for (int i = 0; i < dateClusteredData.get(h).size(); i++){
                                tempArray.add(clusterize(dateClusteredData.get(h).get(i), 3));
                        }
                        fullyClusteredData.add(tempArray);
                        
                }
                
                ArrayList<ArrayList<ArrayList<String[]>>> filteredClusteredData = average(fullyClusteredData);
                
                
                
                write(System.getProperty("user.dir") + "\\temporary_file.csv", filteredClusteredData);
                
                //Nevedhaa's part
                
                String[][] finalLines = readFromCsvFile(System.getProperty("user.dir") + "\\temporary_file.csv"); // The temporary data file given from Kiarash // Stores the 2D array processed from Kiarash's temporary file, through read method
                
                fileWrite(finalLines);
                
        }
        
        ////////////////////////////////////////////////////////////////////////////////////
        ///                                   Methods                                    ///
        ////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////
        //          GUI Stuff           //
        //////////////////////////////////
        
        public static void welcome(){
                
                //welcomes the user
                //getss nothing
                //returns nothing
                //does say hello!
                
                System.out.println("Welcome to the DataCleaner 2.0");
                System.out.println("This file will clean your data if the csv format looks like below. Otherwise You will run into error and the program will crash.");
                System.out.println("Zone, Date, Month, Depth, ParmName, Units, Value, Mdl");
                System.out.println("To proceed press ENTER");
                System.out.println();
                Scanner prompt = new Scanner(System.in);
                prompt.nextLine();
                
        }
        
        public static void write(String path, ArrayList<ArrayList<ArrayList<String[]>>> fullyCookedLines) throws IOException{
                
                //writes in a file, the given ArrayList
                //gets: path of the file, the ArrayList *path must include the file  name
                //returns: nothing
                //does: make a file in the given path.
                
                File file = new File(path);
                file.createNewFile();
                boolean works = false;
                PrintWriter printer;
                while (!works){
                        try{
                                printer = new PrintWriter(path);
                                works = true;
                                printer.close();
                        }
                        catch (Exception e){
                                System.out.println("The file is open, close it and retry");
                                path = existingFile(".csv", 'w');
                        }
                }
                printer = new PrintWriter(path);
                for (int h = 0; h < fullyCookedLines.size(); h++){
                        for (int i = 0; i < fullyCookedLines.get(h).size(); i++){
                                for (int j = 0; j < fullyCookedLines.get(h).get(i).size(); j++){
                                        for (String k: fullyCookedLines.get(h).get(i).get(j)){
                                                
                                                printer.print(k + ",");
                                        }
                                        printer.println();
                                }
                        }
                        
                }
                printer.close();
        }
        
        //////////////////////////////////
        //       Clustering Stuff       //
        //////////////////////////////////
        
        
        public static String[] addString(String[] array, String arg){
                //Adds a String to a String Array
                //formal: array: the String Array, arg: the String Argument to add
                //returns: ret = array + arg
                String[] ret = new String[array.length + 1];
                for (int h = 0; h < array.length; h++){
                        ret[h] = array[h];
                }
                ret[array.length] = arg;
                return ret;
        }
        public static int[] addInt(int[] array, int arg){
                //Adds an int to an int Array
                //formal: array: the int Array, arg: the int Argument to add
                //returns: ret = array + arg
                int[] ret = new int[array.length + 1];
                for (int h = 0; h < array.length; h++){
                        ret[h] = array[h];
                }
                ret[array.length] = arg;
                return ret;
        }
        
        public static ArrayList<String[][]> clusterize(String[][] array, int index){
                //Getss a 2d Array, gets an index, makes an arrayList of 2d Arrays where the 2d Array is the Splitted lines which their index'th index are the same.
                //formal: array: the original 2d array, index: the filter of clustering
                //retirns: arrayClustered: has all of the String 2d Arrays which their arguments have the same index'th index
                ArrayList<String[][]> arrayClustered = new ArrayList<String[][]>();
                String[] allZones = new String[0];
                int[] allCounts = new int[0];
                for (String[] h: array){
                        boolean alreadySeen = false;
                        for (int i = 0; i < allZones.length; i++){
                                if (allZones[i].equals(h[index])){
                                        alreadySeen = true;
                                        allCounts[i]++;
                                        break;
                                }
                        }
                        if (!alreadySeen){
                                allZones = addString(allZones, h[index]);
                                allCounts = addInt(allCounts, 1);
                        }
                }
                for (int h = 0; h < allCounts.length;  h++){
                        arrayClustered.add(new String[allCounts[h]][5]);
                        int counter = 0;
                        for (int i = 0; i < array.length; i++){
                                if (array[i][index].equals(allZones[h])){
                                        arrayClustered.get(h)[counter] = array[i];
                                        counter++;
                                }
                        }
                }
                return arrayClustered;
        }
        //
        
        //////////////////////////////////
        //      Date sorting Stuff      //
        //////////////////////////////////
        public static ArrayList<String[][]> insertionSorte(ArrayList<String[][]> array) {  
                int n = array.size();  
                for (int j = 1; j < n; j++) {  
                        int key = dateToValue(dateToArray(array.get(j)[0][1]));  
                        int i = j - 1;
                        while ((i > -1) && (dateToValue(dateToArray(array.get(i)[0][1])) > key)){  
                                String[][] temp = array.get(i+1);
                                array.set(i+1, array.get(i));
                                array.set(i, temp);
                                i--;  
                        }  
                        /*
                         String[][] temp2 = array.get(i+1);
                         array.set(i+1, array.get(j));
                         array.set(j, temp2);
                         */
                }  
                return array;
        }
        
        //////////////////////////////////
        // Reading and validation stuff //
        //////////////////////////////////
        public static String[][] readCsvToArray(String path) throws IOException{
                //change csv file into 2d array and ignore some unwanted columns
                //formal: a validated path helped by existingFile
                //returns: a 2d array, filtered by ignored columns.
                File csvFile = new File(path);
                Scanner csvFileScanner = new Scanner(csvFile);
                int csvFileLineCounter = 0;
                
                while (csvFileScanner.hasNextLine()){
                        csvFileScanner.nextLine();
                        csvFileLineCounter++;
                }
                
                String[][] csvFileArray = new String[csvFileLineCounter - 1][5]; 
                String csvFileLine;
                String[] csvFileLineSplitted;
                csvFileScanner = new Scanner(csvFile);
                csvFileLineCounter --;
                csvFileScanner.nextLine();
                
                while (csvFileScanner.hasNextLine()){
                        csvFileLine = csvFileScanner.nextLine();
                        csvFileLineSplitted = csvFileLine.split(",");
                        
                        csvFileArray[csvFileArray.length - csvFileLineCounter][0] = csvFileLineSplitted[0];
                        csvFileArray[csvFileArray.length - csvFileLineCounter][1] = csvFileLineSplitted[1];
                        csvFileArray[csvFileArray.length - csvFileLineCounter][2] = csvFileLineSplitted[2];
                        csvFileArray[csvFileArray.length - csvFileLineCounter][3] = csvFileLineSplitted[4];
                        csvFileArray[csvFileArray.length - csvFileLineCounter][4] = csvFileLineSplitted[6];
                        
                        csvFileLineCounter --;
                        
                }
                
                return csvFileArray;
        }
        
        public static String existingFile(String fileExtension, char activity) throws IOException{
                
                /*
                 * Return a valid path to read from/write to
                 * 
                 * Input: 
                 * The required extension for the file like csv or txt or java
                 * The action, is either 'r' (read) or 'w' (write)
                 * 
                 * Output:
                 * The first valid path eligible for doing the action
                 * 
                 * */
                
                String path = "";
                File file;
                Scanner pathInput = new Scanner(System.in);
                
                boolean gotAnswer = false;
                boolean correctExtension;
                
                if (activity == 'r'){
                        System.out.println("Give the path of the file that you wish to read from: ");
                }
                else{
                        System.out.println("Give the path of the file that you wish to write in: ");
                }     
                
                while (!gotAnswer){
                        
                        
                        path = pathInput.nextLine();
                        
                        correctExtension = true;
                        for (int h = 0; h < fileExtension.length(); h++){
                                if (path.charAt(path.length() - h - 1) != fileExtension.charAt(fileExtension.length() - h - 1)){
                                        correctExtension = false;
                                        System.out.println("Wrong extension file, " + fileExtension + " file expected.");
                                        break;
                                }
                        }
                        
                        if (correctExtension){
                                file = new File(path);
                                
                                if (activity == 'r' && file.exists()){
                                        System.out.println("Path validated successfully.");
                                        gotAnswer = true;
                                        break;
                                }
                                
                                
                                else if (activity == 'w' && file.exists()){
                                        
                                        System.out.println("Exists. Overwrite? (y/n)");
                                        System.out.println(file.getPath());
                                        String overwrite;
                                        overwrite = validInput(new String[]{"Y", "N", "y", "n"});
                                        if (overwrite.equals("y") || overwrite.equals("Y")){
                                                file.createNewFile();
                                                gotAnswer = true;
                                                System.out.println("File validated and overwrote successfully.");
                                                break;
                                                
                                        }
                                }
                                
                                
                                else if (activity == 'w' && !file.exists()){
                                        
                                        try{
                                                file.createNewFile();
                                                gotAnswer = true;
                                                System.out.println("File validated and created successfully.");
                                                break; 
                                        }
                                        
                                        catch (Exception e){
                                                System.out.println("Wrong path, retry;");
                                        }
                                }
                                
                                else{
                                        System.out.println("File doesn't exist, retry;");
                                }
                        }
                }
                return path;
        }
        public static String validInput(String[] allowedInputs){ // used for getting Y, N, y, n from user.
                String notSure;
                String valid = "";
                Scanner input = new Scanner(System.in);
                
                //Takes several inputs until a valid one comes, based on the valid input list it got as an argument;
                
                
                while (valid == ""){ 
                        notSure = input.nextLine();
                        
                        //cchecks if input is in the valid domain;
                        
                        for (String h : allowedInputs){
                                try{
                                        if (h.equals(notSure)){
                                                valid = h;
                                                break;
                                        }
                                }
                                catch(Exception e){
                                        System.out.println("");
                                }
                        }
                        
                        
                        //no need to type invalid if the input works!
                        
                        if (valid != ""){
                                break;
                        }
                        
                        System.out.println("Invalid, Retry!");
                }
                
                
                
                
                //returns the first valid input entered.
                
                return valid;
        }
        
        //////////////////////////////////
        //    Date processing Stuff     //
        //////////////////////////////////
        
        
        
        public static String[] dateToArray(String date){
                //changes the date to a String Array
                //formal: date by form: xx-xx-xx
                //returns: {xx, xx, xx}
                String[] dateArray = date.split("-");
                
                return dateArray;
        }
        
        public static String[] changeDateFormat(String[] date){
                //changes from [dd, mm, yy] to [dd, mm, yyyy]
                //gets: date: date array [dd, mm, yy]
                //returns: date array [dd, mm, yyyy]
                if ((date[0] + date[1] + date[2]).length() == 8){
                        return date;
                }
                String[] newDate = new String[3];
                if (Integer.parseInt(date[2]) > 50){
                        newDate[0] = "19" + date[2];
                }
                else{
                        newDate[0] = "20" + date[2];
                }
                newDate[1] = date[1];
                newDate[2] = date[0];
                
                return newDate;
        }
        
        public static int dateToValue(String[] date){
                //gives the rational value of date helped by the other date methods
                ///gets: [dd, mm, yy]
                //returns: dateValue
                
                int dateValue;
                date = changeDateFormat(date);
                
                String dateString = "";
                
                dateString = date[0] + date[1] + date[2];
                dateValue = Integer.parseInt(dateString);
                
                return dateValue;
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        //                                     Printing                                      //
        ///////////////////////////////////////////////////////////////////////////////////////
        public static void fileWrite (String [] [] finalArray) throws IOException {
                /*
                 * Purpose: To create a new file and write the data. 
                 * Parameters: String array,  file name
                 * Return: Null
                 * */
                //Writing Declerations
                String fileName = ""; 
                boolean gotFile = false;
                PrintWriter pw;
                
                while (!gotFile){
                        try{
                                
                                fileName = existingFile(".csv", 'w');
                                pw = new PrintWriter(fileName);
                                gotFile = true;
                                pw.close();
                                
                        }
                        catch (Exception e){
                                System.out.println("File is already open. please retry.");
                        }
                }
                
                
                File file = new File(fileName);
                file.createNewFile();
                pw = new PrintWriter(fileName);
                
                //Header in file
                ArrayList <String> arrHeading = new ArrayList<String>();
                arrHeading.add("Location"); arrHeading.add("Collect Date");
                arrHeading.add("Month");
                
                //Header, adds all the different parameters, by checking if there is a new parameter, then adding it to the array list
                for (int i = 0; i<finalArray.length; i++){
                        boolean exists = false;
                        for (int k = 0; k < arrHeading.size(); k++){
                                if (finalArray[i][3].equals(arrHeading.get(k))){
                                        exists = true;
                                        break;
                                }
                        }
                        if (!exists){ // Add a new heading
                                arrHeading.add(finalArray[i][3]);
                        }
                }
                
                //Create the cleaned version of the array
                ArrayList<ArrayList<String>> arrClean = new ArrayList<ArrayList<String>>();
                arrClean.add(arrHeading);
                
                int rowCounter = 0;
                for (int i = 0; i < finalArray.length; i++){
                        if (!finalArray[i][0].equals(arrClean.get(rowCounter).get(0)) || !finalArray[i][1].equals(arrClean.get(rowCounter).get(1))){
                                arrClean.add(new ArrayList<String>());
                                rowCounter++;
                                arrClean.get(rowCounter).add(finalArray[i][0]);
                                arrClean.get(rowCounter).add(finalArray[i][1]);
                                arrClean.get(rowCounter).add(finalArray[i][2]);
                                for (int h = 0; h < arrHeading.size(); h++){
                                        arrClean.get(rowCounter).add("");
                                }
                        }
                        for (int j = 0; j < arrHeading.size(); j++){
                                
                                if ((finalArray[i][3].equals(arrHeading.get(j))) && !(arrHeading.get(j).equals("pH") && finalArray[i][4].equals("0.0"))){
                                        arrClean.get(rowCounter).set(j, finalArray[i][4]);
                                }
                        }
                }
                
                
                
                
                // Print array into file
                for (int i = 0; i < arrClean.size(); i++){
                        
                        for (int j = 0; j < arrClean.get(i).size(); j++){
                                pw.print(arrClean.get(i).get(j) + ",");
                        }  
                        pw.println(","); // To make next stuff appear in next line
                }
                
                //Close
                pw.flush(); 
                pw.close(); 
                
                System.out.println("Record Saved");
        }
        public static String[][] readFromCsvFile(String path) throws IOException{
                File file = new File(path); // Gets the temporary file
                Scanner fileReader = new Scanner(file);// Scans values in the file
                
                int lineCounter = 0; // Counter to store lines 
                while (fileReader.hasNextLine()){
                        fileReader.nextLine();
                        lineCounter ++;
                }
                
                String[][] fileRead = new String[lineCounter][5];// Stores 2D array 
                
                fileReader = new Scanner(file);
                while (fileReader.hasNextLine()){// Seperates the values one by onee
                        String[] lineSplitted = fileReader.nextLine().split(",");
                        fileRead[fileRead.length - lineCounter] = lineSplitted;
                        lineCounter--;
                }
                return fileRead;// Returns the array for printing
        }
        public static ArrayList<ArrayList<ArrayList<String[]>>> average(ArrayList<ArrayList<ArrayList<String[][]>>> array){
                //Iterate through all clusters of Parm Name and replaces the average String[] with the String[][]
                //get the location, date and parm clustered arraylist, which has String[][] s with multiple repeated String[]s
                //return the location, date, parm clustered arraylist, which has onr String[] in each cluster that is average of all String[][]'s String[]s
                //
                ArrayList<ArrayList<ArrayList<String[]>>> ret = new ArrayList<ArrayList<ArrayList<String[]>>>();
                for (int h = 0; h < array.size(); h++){
                        ArrayList<ArrayList<String[]>> tempHigh = new ArrayList<ArrayList<String[]>>();
                        for (int i = 0; i < array.get(h).size(); i++){
                                ArrayList<String[]> tempLow = new ArrayList<String[]>();
                                for (int j = 0; j < array.get(h).get(i).size(); j++){
                                        double sum = 0;
                                        for (int k = 0; k < array.get(h).get(i).get(j).length; k++){
                                                sum += Double.parseDouble(array.get(h).get(i).get(j)[k][4]);
                                        }
                                        sum = sum / array.get(h).get(i).get(j).length;
                                        tempLow.add(new String[]{array.get(h).get(i).get(j)[0][0], array.get(h).get(i).get(j)[0][1], array.get(h).get(i).get(j)[0][2], 
                                                array.get(h).get(i).get(j)[0][3], Double.toString(sum)});
                                }
                                tempHigh.add(tempLow);
                        }
                        ret.add(tempHigh);
                }
                return ret;
        }
}
