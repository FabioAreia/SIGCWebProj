/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamaro
 */
public class FileStatsAndFilter {
    
    public FileStatsAndFilter(){
        
    }

    public void readFile(String filename){
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void countCanais(String filename){
        HashMap<String, Integer> contas = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                
                String canal = line.substring(0, line.indexOf(" "));
                if(contas.containsKey(canal)){
                    contas.put(canal, contas.get(canal)+1);
                }else  {
                    contas.put(canal, 0);
                }
                
                //System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (Map.Entry<String, Integer> entry : contas.entrySet()) {
            String string = entry.getKey();
            Integer integer = entry.getValue();
            System.out.println(string + " - " + integer);
        }
    }
    
    
    public void filterCanaisWeka(String filename){
        
        HashMap<String, Integer> contas = new HashMap<>();
        
        FileWriter fileWriter = null;
        FileWriter fileWriter2 = null;
        FileWriter fileWriter3 = null;
        try {
            fileWriter = new FileWriter(new File("newWekatrainDataFull.csv"), false);
            fileWriter2 = new FileWriter(new File("newWekatrainDataTrain.csv"), false);
            fileWriter3 = new FileWriter(new File("newWekatrainDataTest.csv"), false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
            BufferedWriter bufferedWriter3 = new BufferedWriter(fileWriter3);
                //System.out.println(line);

            

           
        
        
        
        
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                
                String canal = line.substring(0, line.indexOf(" "));
                
                if(canal.equalsIgnoreCase("Game")){
                    line = line.replaceFirst("Game Sack", "GameSack");
                    canal = line.substring(0, line.indexOf(" "));
                }
                
                if(canal.equalsIgnoreCase("RiotGamesInc")){
                    canal = "GameSack";
                    line = line.replaceFirst("RiotGamesInc", canal);
                }
                
                if(canal.equalsIgnoreCase("ZONEofTECH")||canal.equalsIgnoreCase("GameSack")||canal.equalsIgnoreCase("retrogametech")||canal.equalsIgnoreCase("TopGear")||canal.equalsIgnoreCase("screenjunkies")){
                    if(contas.containsKey(canal)){
                        contas.put(canal, contas.get(canal)+1);
                    }else  {
                        contas.put(canal, 0);
                    }
                    if(contas.get(canal)<5500){
                        line = line.replaceAll("\"", "");
                        line = line.replaceFirst(canal+" ", "\""+canal+"\",\"");
                        bufferedWriter.write(line+"\"");
                        bufferedWriter.newLine();
                        double rand = Math.random();
                        if(rand>0.75f){
                            bufferedWriter3.write(line+"\"");
                            bufferedWriter3.newLine();
                        }else {
                            bufferedWriter2.write(line+"\"");
                            bufferedWriter2.newLine();
                        }
                    }
                }
                
                
                
                //System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
         bufferedWriter.close();
         bufferedWriter2.close();
         bufferedWriter3.close();
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
                fileWriter2.close();
                fileWriter3.close();
            } catch (IOException ex) {
                Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (Map.Entry<String, Integer> entry : contas.entrySet()) {
            String string = entry.getKey();
            Integer integer = entry.getValue();
            System.out.println(string + " - " + integer);
        }
    }
    
    
    
    public void filterCanaisOpenNLP(String filename){
        
        HashMap<String, Integer> contas = new HashMap<>();
        
        FileWriter fileWriter = null;
        FileWriter fileWriter2 = null;
        FileWriter fileWriter3 = null;
        try {
            fileWriter = new FileWriter(new File("newOpenNLPtrainDataFull.txt"), false);
            fileWriter2 = new FileWriter(new File("newOpenNLPtrainDataTrain.txt"), false);
            fileWriter3 = new FileWriter(new File("newOpenNLPtrainDataTest.txt"), false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
            BufferedWriter bufferedWriter3 = new BufferedWriter(fileWriter3);
                //System.out.println(line);

            

           
        
        
        
        
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                
                String canal = line.substring(0, line.indexOf(" "));
                
                if(canal.equalsIgnoreCase("Game")){
                    line = line.replaceFirst("Game Sack", "GameSack");
                    canal = line.substring(0, line.indexOf(" "));
                }
                
                if(canal.equalsIgnoreCase("RiotGamesInc")){
                    canal = "GameSack";
                    line = line.replaceFirst("RiotGamesInc", canal);
                }
                
                if(canal.equalsIgnoreCase("ZONEofTECH")||canal.equalsIgnoreCase("GameSack")||canal.equalsIgnoreCase("retrogametech")||canal.equalsIgnoreCase("TopGear")||canal.equalsIgnoreCase("screenjunkies")){
                    if(contas.containsKey(canal)){
                        contas.put(canal, contas.get(canal)+1);
                    }else  {
                        contas.put(canal, 0);
                    }
                    if(contas.get(canal)<5500){
                        line = line.replaceAll("\"", "");
                        bufferedWriter.write(line);
                        bufferedWriter.newLine();
                        double rand = Math.random();
                        if(rand>0.75f){
                            bufferedWriter3.write(line);
                            bufferedWriter3.newLine();
                        }else {
                            bufferedWriter2.write(line);
                            bufferedWriter2.newLine();
                        }
                    }
                }
                
                
                
                //System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
         bufferedWriter.close();
         bufferedWriter2.close();
         bufferedWriter3.close();
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
                fileWriter2.close();
                fileWriter3.close();
            } catch (IOException ex) {
                Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (Map.Entry<String, Integer> entry : contas.entrySet()) {
            String string = entry.getKey();
            Integer integer = entry.getValue();
            System.out.println(string + " - " + integer);
        }
    }
    
    
    
    public void writeToFile(String file, String line){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(file), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            
            
            bufferedWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    public void filterCanaisOpenNLPFolds(String filename){
        
        HashMap<String, Integer> contas = new HashMap<>();
        
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File("newOpenNLPtrainDataFull.txt"), false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                //System.out.println(line);

            

           
        
        
        
        
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                
                String canal = line.substring(0, line.indexOf(" "));
                
                if(canal.equalsIgnoreCase("Game")){
                    line = line.replaceFirst("Game Sack", "GameSack");
                    canal = line.substring(0, line.indexOf(" "));
                }
                
                if(canal.equalsIgnoreCase("RiotGamesInc")){
                    canal = "GameSack";
                    line = line.replaceFirst("RiotGamesInc", canal);
                }
                
                if(canal.equalsIgnoreCase("ZONEofTECH")||canal.equalsIgnoreCase("GameSack")||canal.equalsIgnoreCase("retrogametech")||canal.equalsIgnoreCase("TopGear")||canal.equalsIgnoreCase("screenjunkies")){
                    if(contas.containsKey(canal)){
                        contas.put(canal, contas.get(canal)+1);
                    }else  {
                        contas.put(canal, 0);
                    }
                    if(contas.get(canal)<5500){
                        line = line.replaceAll("\"", "");
                        bufferedWriter.write(line);
                        bufferedWriter.newLine();
                        for (int j=0; j<10; j++){
                            if(contas.get(canal)%10==j){
                                writeToFile("newOpenNLPtest"+j+".txt", line);
                            }else {
                                writeToFile("newOpenNLPtrain"+j+".txt", line);
                            }
                        }
                    }
                }
                
                
                
                //System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
         bufferedWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (Map.Entry<String, Integer> entry : contas.entrySet()) {
            String string = entry.getKey();
            Integer integer = entry.getValue();
            System.out.println(string + " - " + integer);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        FileStatsAndFilter fe = new FileStatsAndFilter();
        fe.filterCanaisOpenNLPFolds("traindata.txt");
        //fe.filterCanaisWeka("traindata.txt");
    }
    
}
