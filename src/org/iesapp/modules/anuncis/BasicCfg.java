/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;
import java.util.ArrayList;
import java.util.HashMap;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class BasicCfg{
    
    public ArrayList<String> allowedEditingUsers;
    public ArrayList<String> fullEditingUsers;
    public String validationPolicy = "ASKONCE";
    public boolean isValidated = false;
    public String onStartLoad;
    public boolean allowModifyStartup;
    public int collapsaAnuncisPassats=1;
    protected final HashMap<String, Object> moduleInitParams;
    
    public BasicCfg(HashMap<String,Object> moduleInitParams)
    {
        this.moduleInitParams = moduleInitParams;
        start();
    }
    
    private void start()
    {
        
        //Process preferences from moduleInit      
        String txt = (String) moduleInitParams.get("allowedEditingUsers");
        allowedEditingUsers = StringUtils.parseStringToArray(txt, ";", StringUtils.CASE_UPPER);
        
        txt = (String) moduleInitParams.get("fullEditingUsers");
        fullEditingUsers = StringUtils.parseStringToArray(txt, ";", StringUtils.CASE_UPPER);
        
        validationPolicy = (String) moduleInitParams.get("validationPolicy");
        if(validationPolicy==null)
        {
            validationPolicy = "ASKONCE";
        }
        onStartLoad = (String) moduleInitParams.get("onStartLoad");
        if(onStartLoad==null)
        {
            onStartLoad = "5";
        }
        allowModifyStartup = ((Number) moduleInitParams.get("allowModifyStartup")).intValue()>0;
        collapsaAnuncisPassats = ((Number) moduleInitParams.get("collapsaAnuncisPassats")).intValue();
    }

   
    
}
