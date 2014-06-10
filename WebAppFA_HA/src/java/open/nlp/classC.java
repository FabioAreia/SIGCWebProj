/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.nlp;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hamaro
 */
@XmlRootElement
public class classC {
    
    String query;


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    
}
