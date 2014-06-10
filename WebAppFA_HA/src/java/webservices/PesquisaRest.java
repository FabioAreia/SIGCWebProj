/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservices;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import open.nlp.LuceneBuilder;
import open.nlp.OpenNLP;
import open.nlp.SeparateAdjectives;
import open.nlp.classC;
import open.nlp.classS;

/**
 * REST Web Service
 *
 * @author hamaro
 */
@Path("pesquisa")
public class PesquisaRest {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PesquisaRest
     */
    public PesquisaRest() {
    }

    /**
     * Retrieves representation of an instance of webservices.PesquisaRest
     * @return an instance of java.lang.String
     */
    @GET
    @Consumes("text/plain")
    @Produces("text/plain")
    public String getText(@QueryParam("query") String texto,@QueryParam("page") int page,@QueryParam("numperpage") int numperpage,@QueryParam("toget") String toget){
        //TODO return proper representation object
        LuceneBuilder lb = new LuceneBuilder();
        
        String result;
        try {
            result = java.net.URLDecoder.decode(texto.replaceAll("[^A-Za-z0-9 ]", ""), "UTF-8");
            System.out.println("INININ:"+texto.replaceAll("[^A-Za-z0-9 ]", ""));
            return lb.searchLuceneFuzzy(result, page, numperpage,toget);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PesquisaRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "{\"results\":0,\"docs\":[]}";
    }
    
    @POST
    @Path("class")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public String getClass(classC res){
        //TODO return proper representation object
        
        String texto = res.getQuery();
        System.out.println("INININ:"+texto.replaceAll("[^A-Za-z0-9 ]", ""));
        String result;
        try {
            result = java.net.URLDecoder.decode(texto.replaceAll("[^A-Za-z0-9 ]", ""), "UTF-8");
            
            return "{\"result\":\""+OpenNLP.classifyModel(result)+"\"}";
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PesquisaRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "{\"result\":\"\"}";
    }
    
    @POST
    @Path("sentiment")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public String getClass(classS res){
        //TODO return proper representation object
        
        String texto = res.getQuery();
        System.out.println("INININ:"+texto.replaceAll("[^A-Za-z0-9 ]", ""));
        String result;
        try {
            result = java.net.URLDecoder.decode(texto.replaceAll("[^A-Za-z0-9 ]", ""), "UTF-8");
            
            return "{\"result\":\""+SeparateAdjectives.evaluateComment(result)+"\"}";
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PesquisaRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "{\"result\":\"\"}";
    }

    /**
     * PUT method for updating or creating an instance of PesquisaRest
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/plain")
    public void putText(String content) {
    }
}
