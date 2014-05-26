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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import open.nlp.LuceneBuilder;

/**
 * REST Web Service
 *
 * @author hamaro
 */
@Path("autocomplete")
public class AutocompleteRest {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AutocompleteRest
     */
    public AutocompleteRest() {
    }

    /**
     * Retrieves representation of an instance of webservices.AutocompleteRest
     * @param texto
     * @return an instance of java.lang.String
     */
    @GET
    @Consumes("text/plain")
    @Produces("text/plain")
    public String getText(@QueryParam("term") String texto) {
        //TODO return proper representation object
        /*
        StringBuilder strb = new StringBuilder();
        strb.append("[");
        
            strb.append("{");
                strb.append("\"id\"");
                strb.append(":");
                strb.append("\"");
                strb.append("jonas");
                strb.append("\",");
                
                strb.append("\"label\"");
                strb.append(":");
                strb.append("\"");
                strb.append("Jonas");
                strb.append("\",");
                
                strb.append("\"value\"");
                strb.append(":");
                strb.append("\"");
                strb.append("jonas");
                strb.append("\"");
            
            strb.append("}");
            strb.append(",");
            
            
            strb.append("{");
                strb.append("\"id\"");
                strb.append(":");
                strb.append("\"");
                strb.append("2");
                strb.append("\",");
                
                strb.append("\"label\"");
                strb.append(":");
                strb.append("\"");
                strb.append("Ana");
                strb.append("\",");
                
                strb.append("\"value\"");
                strb.append(":");
                strb.append("\"");
                strb.append("ana");
                strb.append("\"");
            
            strb.append("}");
        
        strb.append("]");
        */
        LuceneBuilder lb = new LuceneBuilder();
        
        String result;
        try {
            result = java.net.URLDecoder.decode(texto.replaceAll("[^A-Za-z0-9 ]", ""), "UTF-8");
            return lb.searchLuceneAutocomplete(result);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PesquisaRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";
    }

    /**
     * PUT method for updating or creating an instance of AutocompleteRest
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/plain")
    public void putText(String content) {
    }
}
