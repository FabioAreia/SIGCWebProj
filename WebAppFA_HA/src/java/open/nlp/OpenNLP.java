/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package open.nlp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 *
 * @author Fábio
 */
public class OpenNLP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
//        File file = new File("C:\\Users\\Fábio\\Documents\\NetBeansProjects\\Open NLP\\en-pos-maxent.bin");
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        
//        PlainTextGISModelReader readerNLP = new PlainTextGISModelReader(file);
//        GISModel model = (GISModel) readerNLP.getModel();
//        DocumentCategorizerME dc = new DocumentCategorizerME (model);

        //SentenceDetect();
        //tokinization();
        //POSTag();
        //trainModel();
        testModel();
        //trainModelTotal();
        /*System.out.println(classifyModel("Since when did Captain Slow test fast cars?") + " = TopGear");
        System.out.println(classifyModel("where do you get your materials from? my mother said she would kill me if I destroyed another alarm clock. :)") + " = jewl");
        System.out.println(classifyModel("Hey, where can I get these parts commonly... I have a steampunk band and I need to make clothes and accessories for us... please help... great work by the way :)") + " = jewl");
        System.out.println(classifyModel("got my first penta kill on jayce today. had to come listen to this song XD") + " = RiotGamesInc");

        System.out.println(classifyModel("who cares the show has been shit this season. why do a season in the wet season any way. plus they do laps twin and split screen fucking annoying. they did a shitty piece on the P1 no hot lap. the Porsche 918 had sponsored placement written all over it. top gear is slipping!") + " = TopGear");
        System.out.println(classifyModel("there was about 1,000 real black hddvd given to xbox workers for working on the the black xbox elite") + " = retrogames");
        System.out.println(classifyModel("The quaility of the camera is excellent and 40 a bargin indeed,i paid much more for my HD camera,and it looks equally as good,lucky you for spotting a bargain.") + " = retrogames");
        System.out.println(classifyModel("Could you do a honest trailer for the first Transformers movie? You know the real one thats a cartoon.") + " = screenjunkies");*/
    }

    public static String classifyModel(String teste) {
        teste = LuceneBuilder.removeStopWords(teste);
        InputStream is = null;
        String category = "";
        try {
            String modelFilePath = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/modeltotal.bin";
            is = new FileInputStream(modelFilePath);
            DoccatModel model = new DoccatModel(is);
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(teste);

            int u = 0;
            double max = 0;
            int mids = -1;
            for (double d : outcomes) {
                if (d > max) {
                    mids = u;
                    max = d;
                }
                //System.out.println("out "+u+": "+d);
                u++;
            }
            //System.out.println("Max: "+max+" idx: "+mids);
            category = myCategorizer.getBestCategory(outcomes);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return category;
    }

    public static void testModel() {
        double cumulative = 0;
        
        for(int i=0; i<10; i++){


            String modelFilePath = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/modeltrain"+i+".bin";
            String trainingFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrain"+i+".txt";
            // Instance of openNLP's default model class
            String destFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrain"+i+"_.txt";
            limpastopwords(trainingFile,destFile);
            DoccatModel model = null;
            InputStream dataIn = null;
            try {
                dataIn = new FileInputStream(destFile);
                ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
                        "UTF-8");
                ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
                // "en" is language code of English.
                model = DocumentCategorizerME.train("en", sampleStream);
            } catch (IOException e) {
                //log.error("Failed to read or parse training data, training failed", e);
            } finally {
                if (dataIn != null) {
                    try {
                        // free the memory resources.
                        dataIn.close();
                    } catch (IOException e) {
                        //log.warn(e.getLocalizedMessage());
                    }
                }
            }
            OutputStream modelOut = null;
            try {
                modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
                model.serialize(modelOut);
            } catch (IOException e) {
                //log.error("Failed to save model at location " + modelFilePath);
            } finally {
                if (modelOut != null) {
                    try {
                        modelOut.close();
                    } catch (IOException e) {
                        //log.error("Failed to correctly save model. Written model might be invalid.");
                    }
                }
            }





            ArrayList<String> cats = new ArrayList<>();
            cats.add("TopGear");
            cats.add("GameSack");
            cats.add("ZONEofTECH");
            cats.add("retrogametech");
            cats.add("screenjunkies");
            ArrayList<Integer> was = new ArrayList<>();
            ArrayList<Integer> got = new ArrayList<>();
            trainingFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtest"+i+".txt";
            destFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtest"+i+"_.txt";
            limpastopwords(trainingFile,destFile);
            InputStream is = null;
            try {
                //String modelFilePath = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/modeltrain.bin";
                is = new FileInputStream(modelFilePath);
                model = new DoccatModel(is);
                DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
                DocumentCategorizerEvaluator evaluator = new DocumentCategorizerEvaluator(myCategorizer);
                //ArrayList<DocumentSample> docsamples = new ArrayList<>();
                //double acertos = 0;
                //double totais = 0;

                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(destFile), "UTF-8"))) {
                    //StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {

                        //System.out.println(line);
                        String canal = line.substring(0, line.indexOf(" "));
                        String comment = line.substring(line.indexOf(" ") + 1);
                        DocumentSample sample = new DocumentSample(canal, comment);
                        was.add(cats.indexOf(canal));
                        got.add(cats.indexOf(sample.getCategory()));
                        //System.out.println(""+sample.getClass());
                        /*if(cats.indexOf(canal)==cats.indexOf(myCategorizer.getBestCategory(myCategorizer.categorize(comment)))){
                            acertos++;
                        }
                        totais++;*/
                        
                        evaluator.evaluteSample(sample);

                        line = br.readLine();
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileStatsAndFilter.class.getName()).log(Level.SEVERE, null, ex);
                }

                //String category = "TopGear";
                //String content = "the back of most lamborghinis are pretty ugly, to put it nicely :)";
                double result = evaluator.getAccuracy();
                cumulative += result;
                System.out.println("Accuracy = " + result);
                //System.out.println("Accuracy = " + (acertos/totais));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/confusionmatrix"+i+".csv"), false);

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (int y = 0; y < was.size(); y++) {
                    bufferedWriter.write(was.get(y) + "," + got.get(y));
                    bufferedWriter.newLine();
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
        }
        
        System.out.println("CROSSFOLD Accuracy = " + (cumulative/10));
    }

    public static void trainModel() {
        String modelFilePath = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/modeltrain.bin";
        String trainingFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrainDataTrain.txt";
        // Instance of openNLP's default model class
        String destFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrainDataTrain_.txt";
        limpastopwords(trainingFile,destFile);
        DoccatModel model = null;
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream(destFile);
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
                    "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            // "en" is language code of English.
            model = DocumentCategorizerME.train("en", sampleStream);
        } catch (IOException e) {
            //log.error("Failed to read or parse training data, training failed", e);
        } finally {
            if (dataIn != null) {
                try {
                    // free the memory resources.
                    dataIn.close();
                } catch (IOException e) {
                    //log.warn(e.getLocalizedMessage());
                }
            }
        }
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
            model.serialize(modelOut);
        } catch (IOException e) {
            //log.error("Failed to save model at location " + modelFilePath);
        } finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    //log.error("Failed to correctly save model. Written model might be invalid.");
                }
            }
        }
    }

    public static void limpastopwords(String fonte, String dest) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(dest), false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                //System.out.println(line);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fonte), "UTF-8"))) {
                //StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    try {
                        String canal = line.substring(0, line.indexOf(" "));
                        String comm = line.substring(line.indexOf(" ")+1);
                        comm = LuceneBuilder.removeStopWords(comm);
                        bufferedWriter.write(canal+" "+comm + "\"");
                        bufferedWriter.newLine();
                    } catch (Exception ex) {
                        Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
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

    }

    public static void trainModelTotal() {
        String modelFilePath = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/modeltotal.bin";
        String trainingFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrainDataFull.txt";

        String destFile = "/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/newOpenNLPtrainDataFull_.txt";
        limpastopwords(trainingFile,destFile);
        // Instance of openNLP's default model class
        DoccatModel model = null;
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream(destFile);
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
                    "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            // "en" is language code of English.
            model = DocumentCategorizerME.train("en", sampleStream);
        } catch (IOException e) {
            //log.error("Failed to read or parse training data, training failed", e);
        } finally {
            if (dataIn != null) {
                try {
                    // free the memory resources.
                    dataIn.close();
                } catch (IOException e) {
                    //log.warn(e.getLocalizedMessage());
                }
            }
        }
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
            model.serialize(modelOut);
        } catch (IOException e) {
            //log.error("Failed to save model at location " + modelFilePath);
        } finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    //log.error("Failed to correctly save model. Written model might be invalid.");
                }
            }
        }
    }

}
