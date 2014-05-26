/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package open.nlp;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.IntDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author hamaro
 */
public class LuceneBuilder {

    public LuceneBuilder() {
        //buildLuceneIndex();
    }

    public void buildLuceneIndex() {
        try {
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            Directory directory = FSDirectory.open(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/luceneindex"));

            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);

            IndexWriter iwriter = new IndexWriter(directory, config);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/ccommentsforlucene.csv"), "UTF-8"))) {
                //StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                line = br.readLine();

                int numc = 2;
                while (line != null) {
                    if (numc % 200 == 0) {
                        System.out.println("running: " + (numc / 52271.0f * 100) + " %");
                    }

                    String[] splits = line.split(";");

                    //System.out.println(""+line);
                    //String text = "This is the text to be indexed.";
                    if (splits.length == 9) {
                        Document doc = new Document();
                        Float score = Float.parseFloat(splits[3]);
                        Integer like = Integer.parseInt(splits[4]);
                        Integer dislike = Integer.parseInt(splits[5]);
                        doc.add(new StringField("tema", splits[0], Store.YES));
                        doc.add(new StringField("canal", splits[1], Store.YES));
                        doc.add(new Field("titulo", splits[2], TextField.TYPE_STORED));
                        doc.add(new FloatDocValuesField("score", score));
                        doc.add(new IntDocValuesField("likes", like));
                        doc.add(new IntDocValuesField("dislikes", dislike));

                        doc.add(new StringField("video", splits[6], Store.YES));

                        doc.add(new StringField("user", splits[7], Store.YES));
                        doc.add(new Field("comment", splits[8], TextField.TYPE_STORED));

                        iwriter.addDocument(doc);
                    }

                    //System.out.println(line);
                    line = br.readLine();
                    numc++;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            iwriter.close();

        } catch (IOException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void searchLucene() {
        try {
            Directory directory = FSDirectory.open(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/luceneindex"));
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Parse a simple query that searches for "text":
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            QueryParser parser = new QueryParser(Version.LUCENE_46, "comment", analyzer);
            Query query = parser.parse("ferrari");
            ScoreDoc[] hits = isearcher.search(query, null, 10, Sort.RELEVANCE).scoreDocs;
            //assertEquals(1, hits.length);
            // Iterate through the results:
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                System.out.println("canal: " + hitDoc.get("canal") + " title: " + hitDoc.get("titulo"));

            }
            ireader.close();
            directory.close();
        } catch (IOException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String searchLuceneFuzzy(String searchString, int page, int recordsPerPage) {
        StringBuilder strb = new StringBuilder();
        try {
            // Setup the fields to search through
            String[] searchfields = new String[]{"titulo", "comment"};
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            // Build our booleanquery that will be a combination of all the queries for each individual search term
            BooleanQuery finalQuery = new BooleanQuery();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_46, searchfields, analyzer);

            
            // Split the search string into separate search terms by word
            String[] terms = searchString.split(" ");
            for(String term : terms){
                if(term.length()>3)
                    finalQuery.add(parser.parse(term), BooleanClause.Occur.SHOULD);
            }
            
            
            
            Directory directory = FSDirectory.open(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/luceneindex"));
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            
            TopDocs docs = isearcher.search(finalQuery, Integer.MAX_VALUE);
            ScoreDoc[] hits = docs.scoreDocs;
            
            int offset = page * recordsPerPage;
            int count = Math.min(hits.length - offset, recordsPerPage);
            //ScoreDoc[] hits = isearcher.search(finalQuery, 10).scoreDocs;
            //assertEquals(1, hits.length);
            // Iterate through the results:
            strb.append("{ \"results\": ").append(hits.length).append(",\"docs\":");
            strb.append("[");
            for (int i = 0; i < count; i++) {
                if(i>0)
                    strb.append(",");

                int docId = hits[offset+i].doc;
                Document d = isearcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("canal")+" title: " + d.get("titulo") + " Score: "+ hits[offset+i].score);
                
                strb.append("{");
                
                strb.append("\"searchscore\"");
                strb.append(":");
                strb.append("\"");
                strb.append(hits[offset+i].score);
                strb.append("\",");
                
                strb.append("\"canal\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("canal"));
                strb.append("\",");
                
                strb.append("\"tema\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("tema"));
                strb.append("\",");
                
                strb.append("\"titulo\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("titulo"));
                strb.append("\",");
                
                strb.append("\"user\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("user"));
                strb.append("\",");
                
                strb.append("\"video\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("video"));
                strb.append("\",");
                
                strb.append("\"score\"");
                strb.append(":");
                strb.append("\"");
                //ByteBuffer buff = ByteBuffer.wrap(d.getBinaryValue("score").bytes);
                //strb.append(buff.getFloat());
                strb.append("\",");
                
                strb.append("\"likes\"");
                strb.append(":");
                strb.append("\"");
                //buff = ByteBuffer.wrap(d.getBinaryValue("likes").bytes);
                //strb.append(buff.getInt());
                strb.append("\",");
                
                strb.append("\"dislikes\"");
                strb.append(":");
                strb.append("\"");
                //buff = ByteBuffer.wrap(d.getBinaryValue("dislikes").bytes);
                //strb.append(buff.getInt());
                strb.append("\",");
                
                strb.append("\"comment\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("comment"));
                strb.append("\"");
            
            strb.append("}");
                //Document hitDoc = isearcher.doc(hits[i].doc);
                //System.out.println(hits[i].score+" - canal: " + hitDoc.get("canal") + " title: " + hitDoc.get("titulo"));

            }
            strb.append("]}");
            ireader.close();
            directory.close();
            
        } catch (IOException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return strb.toString();
    }

    
    public String searchLuceneAutocomplete(String searchString){
        System.out.println("Search: "+searchString);
        StringBuilder strb = new StringBuilder();
        try {
            Directory directory = FSDirectory.open(new File("/Users/hamaro/Desktop/develop/aulasrepo/SIGCWebProj/WebAppFA_HA/luceneindex"));
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Parse a simple query that searches for "text":
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            QueryParser parser = new QueryParser(Version.LUCENE_46, "titulo", analyzer);
            Query query = parser.parse(searchString+"*");
            
            
            TopDocs docs = isearcher.search(query, 15);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println("Count: "+hits.length);
            //ScoreDoc[] hits = isearcher.search(finalQuery, 10).scoreDocs;
            //assertEquals(1, hits.length);
            // Iterate through the results:
            
            strb.append("[");
            for (int i = 0; i < hits.length; i++) {
                if(i>0)
                    strb.append(",");

                int docId = hits[i].doc;
                Document d = isearcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("canal")+" title: " + d.get("titulo") + " Score: "+ hits[i].score);
                
                strb.append("{");
                strb.append("\"id\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("titulo"));
                strb.append("\",");
                
                strb.append("\"label\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("titulo"));
                strb.append("\",");
                
                strb.append("\"value\"");
                strb.append(":");
                strb.append("\"");
                strb.append(d.get("titulo"));
                strb.append("\"");
            
            strb.append("}");
                //Document hitDoc = isearcher.doc(hits[i].doc);
                //System.out.println(hits[i].score+" - canal: " + hitDoc.get("canal") + " title: " + hitDoc.get("titulo"));

            }
            strb.append("]");
            ireader.close();
            directory.close();
        } catch (IOException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return strb.toString();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        LuceneBuilder lb = new LuceneBuilder();
        //lb.buildLuceneIndex();
        //lb.searchLuceneFuzzy("porche");
        //lb.searchLuceneAutocomplete("Porsche 911 Tri");
    }

}
