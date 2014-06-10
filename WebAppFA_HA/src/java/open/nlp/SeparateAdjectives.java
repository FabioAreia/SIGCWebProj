/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package open.nlp;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import static java.lang.Double.NaN;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import open.nlp.TreatText;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.PlainTextGISModelReader;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DocumentCategorizerME;
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
public class SeparateAdjectives {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
//        tokinization();
        //System.out.println(""+evaluateComment("Wow thats great And I luv all ur videos 3"));
        //csvEvaluateVideo("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/ccommentsforlucene.csv");
    }

    public static LinkedList<String> tokinization(String comment) throws InvalidFormatException,
            IOException {
//        System.out.println("Entrei no tokinazation e o comment é " + comment);
//        File file = new File("Dataset_Wikinews.txt");
//        BufferedReader readerText = new BufferedReader(new FileReader(file));

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/en-token.bin");
        TokenizerModel model = new TokenizerModel(is);
        Tokenizer tokenizer = new TokenizerME(model);

        LinkedList<String> tokens = new LinkedList<>();

//        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = comment.toLowerCase();
//        while ((line = br.readLine()) != null) {
        String tokensTemp[] = tokenizer.tokenize(line);
        for (int i = 0; i < tokensTemp.length; i++) {
            tokens.add(tokensTemp[i]);
        }
//        }
//        br.close();

//        System.out.println(sentences[0]);
//        System.out.println(sentences[1]);
//        System.out.println(sentences[2]);
//        for (int i = 0; i < tokens.size(); i++) {
//            System.out.println(tokens.get(i));
//        }
//        System.out.println("Numero de palavras: " + tokens.size());
        is.close();
        return tokens;
    }

    public static double evaluateComment(String comment) {
        try{
            double score = 0.0;
            boolean butclause = false;
            boolean opinioShifter = false;
    //        TreatText treatText = new TreatText("Dataset_Wikinews.txt");
    //        treatText.run();
            POSModel model = new POSModelLoader()
                    .load(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/en-pos-maxent.bin"));
            PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
            POSTaggerME tagger = new POSTaggerME(model);
            LinkedList<String> tokens = new LinkedList<>();
            tokens = tokinization(comment.toLowerCase());
    //        System.out.println("tamanho dos tokens" + tokens.size());

            SentiWordNet sentidor = new SentiWordNet("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/SentiWordNet_3.0.0_20130122.txt");
            LinkedList<String> adjectives = new LinkedList<>();
            LinkedList<String> adjectivesShiftados = new LinkedList<>();
            LinkedList<String> adjectivesWithBut = new LinkedList<>();
            LinkedList<String> adverbs = new LinkedList<>();
            LinkedList<String> adverbsShiftados = new LinkedList<>();
            LinkedList<String> adverbsWithBut = new LinkedList<>();

    //        String input = tokinization().get(6);
            String input = "";
            for (int i = 0; i < tokens.size(); i++) {
                input = tokens.get(i);
                String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                        .tokenize(tokens.get(i));
                String[] tags = tagger.tag(whitespaceTokenizerLine);
                for (String tag : tags) {
                    if (tag.contains("JJ") && !opinioShifter && !butclause) {
                        adjectives.add(tokens.get(i).toLowerCase());
    //                        sentidor.pontuar(tokens.get(i));
                    }

                    if (tag.contains("JJ") && opinioShifter) {
                        adjectivesShiftados.add(tokens.get(i).toLowerCase());
    //                    System.out.println("APANHEI UM NOT ANTES");
    //  
                    }

                    if (tag.contains("JJ") && butclause) {
                        adjectivesWithBut.add(tokens.get(i).toLowerCase());
    //                    System.out.println("APANHEI UM NOT ANTES");
                    }

                    if (tag.contains("JJ")) {
                        butclause = false;
                        opinioShifter = false;
                    }

                    if (tag.contains("RB") && !opinioShifter && !butclause) {
                        adverbs.add(tokens.get(i).toLowerCase());
    //                        sentidor.pontuar(tokens.get(i));
                    }

                    if (tag.contains("RB") && opinioShifter) {
                        adverbsShiftados.add(tokens.get(i).toLowerCase());
                    }

                    if (tag.contains("RB") && butclause) {
                        adverbsWithBut.add(tokens.get(i).toLowerCase());
                    }

                    if (tag.contains("RB")) {
                        butclause = false;
                        opinioShifter = false;
                    }

                    if (tokens.get(i).equals("not") || tokens.get(i).equals("never") || tokens.get(i).equals("none") || tokens.get(i).equals("nobody") || tokens.get(i).equals("nowhere") || tokens.get(i).equals("neither") || tokens.get(i).equals("cannot")) {
                        opinioShifter = true;
    //                    System.out.println("Apanhado opinion Shifter");
                    }

                    if (tokens.get(i).equals("but")) {
                        butclause = true;
    //                    System.out.println("Apanhado butclause");
                    }

                }

                POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
    //            System.out.println(sample.toString());
            }
            score = sentidor.scoreComment(adjectives, adjectivesWithBut, adjectivesShiftados, adverbs, adverbsWithBut, adverbsShiftados);
            return score;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static void csvEvaluateVideo(String pathFile) throws IOException {
        int lastLine = 0;
        File file = new File(pathFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        LinkedList<Double> scoreCommentsVideo = new LinkedList<>();

        File fileWrite = new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/CommentsPerVideo.csv");
        FileWriter fileW = new FileWriter(fileWrite);
        BufferedWriter buffW = new BufferedWriter(fileW);

        String line;
        String previousTitle = "";
        String previousChannel = "";
        String previousTheme = "";
        String previousLink = "";
        double previousScore = 0;
        double previousRatios = 0;
        double previousLikes = 0;
        double previousDeslikes = 0;

        while ((line = reader.readLine()) != null) {

            String[] splits = line.split(";");
//            System.out.println(splits[2]);
            if (previousTitle.equals(splits[2])) {
                previousTitle = splits[2];
                double wordScore = 0;

                try {
                    wordScore = evaluateComment(splits[8]);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                }

//                System.out.println("COMENTARIO é" + splits[8]);
                if (wordScore > 0 || wordScore < 0) {
//                    System.out.println("SCORESS  " + wordScore);
//                    System.out.println("Nao é nan");
                    scoreCommentsVideo.add(wordScore);
                }
            }
            if (!previousTitle.equals(splits[2])) {
                double pontuacao = evaluateList(previousTitle, scoreCommentsVideo);
                scoreCommentsVideo.clear();

                double wordScore = 0;
                try {
                    wordScore = evaluateComment(splits[8]);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                }

                if (wordScore > 0 || wordScore < 0) {
                    scoreCommentsVideo.add(wordScore);
                }



//                buffW.write(previousRow + ";" + pontuacao);
                buffW.write(previousTheme + ";" + previousChannel + ";" + previousLink + ";" + previousTitle + ";" + previousScore + ";" + previousRatios + ";" + pontuacao);

                buffW.newLine();
                
                
                                previousTitle = splits[2];
                previousChannel = splits[1];
                previousTheme = splits[0];
                previousLink = splits[6];
                try {

                    previousScore = Double.parseDouble(splits[3]);

                    previousLikes = Double.parseDouble(splits[4]);
                    previousDeslikes = Double.parseDouble(splits[5]);

                } catch (java.lang.NumberFormatException e) {

                }
                if (previousLikes >= 0 && previousDeslikes > 0) {
                    previousRatios = previousLikes / previousDeslikes;
                } else {
                    if (previousDeslikes == 0) {
                        previousRatios = previousLikes;
                    } else {
                        previousRatios = -1;
                    }
                }

            }
        }
        buffW.close();
        fileW.close();
    }

    public static double evaluateList(String title, LinkedList<Double> listaScores) throws IOException {
//        System.out.println("Entrei");

        double pontuacaoTotal = 0;
        double pontuacao = 0;
        for (int i = 0; i < listaScores.size(); i++) {
            pontuacaoTotal = pontuacaoTotal + listaScores.get(i);
        }
        pontuacao = pontuacaoTotal / listaScores.size();
        System.out.println("A pontuação do video " + title + "media é de " + pontuacao);
        return pontuacao;
    }
}

