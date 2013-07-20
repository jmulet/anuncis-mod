/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.iesapp.clients.iesdigital.anuncis.AnuncisDefinition;
import org.iesapp.clients.sgd7.mensajes.MensajesListas;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.pluggable.grantsystem.GrantModule;
import org.iesapp.framework.pluggable.grantsystem.GrantSystem;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class Cfg extends BasicCfg{
    
    public DefaultComboBoxModel combomodelGList;
    public ArrayList<String> glist;
    public ArrayList<String> actions;
    public int pid;
    public static String[] resourceIcons;
    public GrantModule moduleGrant;
    public static GrantModule defaultsGrant;
    public static ArrayList<MensajesListas> mensajesListas;
    protected final CoreCfg coreCfg;
    
    public Cfg(HashMap<String,Object> moduleInitParams, CoreCfg coreCfg)
    {
        super(moduleInitParams);
        this.coreCfg = coreCfg;
        inicia();
    }
    
    private void inicia()
    {
         
        
//        //Define all available icons
        resourceIcons = new String[]{
            "sortida.gif",
            "reunio.gif",
            "event.gif",
            "new.gif",
            "pda.gif",
            "misc.gif",
            "weib.gif",
            "write.png",
            "pdaweb.png",
            "bulb.gif"                
        };
        
        
        combomodelGList = new DefaultComboBoxModel();
        combomodelGList.addElement("Tothom");
        glist = new ArrayList<String>();
        glist.add("*");
        actions = new ArrayList<String>();       
        
        //Add to the combomodelGList (elments from llistes de missatges)
        mensajesListas = getCoreCfg().getSgdClient().getMensajesCollection().getMensajesListas();
        for(MensajesListas b: mensajesListas)
        {
            combomodelGList.addElement("["+b.getId()+"] "+b.getNombre());
        }
      
        
        //Acaba de construir les preferencies
        defaultsGrant = new GrantModule(null, coreCfg);
        defaultsGrant.register("edit_own_anuncis", "Permet editar anuncis propis", GrantBean.NONE, GrantBean.BASIC_CONFIG);
        defaultsGrant.register("edit_all_anuncis", "Permet editar tots els anuncis", GrantBean.NONE, GrantBean.BASIC_CONFIG);
        defaultsGrant.register("delete_own_anuncis", "Permet esborrar anuncis propis", GrantBean.NONE, GrantBean.BASIC_CONFIG);
        defaultsGrant.register("delete_all_anuncis", "Permet esborrar tots els anuncis", GrantBean.NONE, GrantBean.BASIC_CONFIG);
        defaultsGrant.register("print_anuncis", "Permet imprimir anuncis", GrantBean.ALL, GrantBean.BASIC_CONFIG);
        
        moduleGrant = GrantSystem.getInstance("anuncis", coreCfg);
       
        for(AnuncisDefinition bean : AnuncisDefinition.getMapDefined().values()) {
            //Try to find if is there a template for this type of anunci
            String filename = null;
            String template = "template0" + bean.getAnuncisTypeId();
            System.out.println("Template="+template);
            for(String key: moduleInitParams.keySet() )
            {
                if(key.equalsIgnoreCase(template))
                {
                    filename = (String) moduleInitParams.get(key);
                    break;
                }
            }
            System.out.println("filename="+filename);
            if (filename != null && !filename.isEmpty()) {
                String content = getTemplateFromFile(filename);
                 System.out.println("content="+content);
                bean.setDocumentTemplate(content);
            }
        }
        
        
         for(String key: moduleInitParams.keySet() )
         {
             if (key.startsWith("glist_")) {
                combomodelGList.addElement(StringUtils.AfterFirst(key, "glist_"));
                glist.add((String) moduleInitParams.get(key));
            }
        }
 
    }

     
//    
//    private void saveIni() {
//            Properties props2 = new Properties();
//
//            try {
//              props2.setProperty("validationPolicy", validationPolicy);
//              
//              String users = "";
//              for(int i=0; i<allowedEditingUsers.size(); i++)
//              {
//                  users += allowedEditingUsers.get(i)+";";
//              }
//              users = StringUtils.BeforeLast(users, ";");
//              
//              props2.setProperty("allowedEditingUsers", users);
//              FileOutputStream filestream = new FileOutputStream(Cfg.TAULERINI);
//              props2.store(filestream, null);
//              filestream.close();
//
//            } catch (IOException ex) {
//                Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
//            }
//  
//    }

    private static String getTemplateFromFile(String filename) {
        java.io.File file = new java.io.File(CoreCfg.contextRoot+File.separator+filename);
        if(!file.exists()) 
        {
            //System.out.println("Can't find file "+filename);
            return "";
        }
            
        
        int ch;
        StringBuilder strContent = new StringBuilder("");
    
        try {
            FileInputStream fin = new FileInputStream(file);
            while ((ch = fin.read()) != -1) {
                strContent.append((char) ch);
            }
            fin.close();
        } catch (Exception e) {
            return "";
        }

        return strContent.toString();
    }
      

     
  //this can launch both local and remote files (including spaces)
  public static void launchFile(String filePath)
  {
    if(filePath == null || filePath.trim().length() == 0) {
          return;
      }
    if(!Desktop.isDesktopSupported()) {
          return;
      }
    Desktop dt = Desktop.getDesktop();
    try
    {      
       dt.browse(getFileURI(filePath));
    } catch (Exception ex)
    {
       Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
     }
   }

  //generate uri according to the filePath
  private static URI getFileURI(String filePath)
  {
    
    URI uri = null;
    filePath = filePath.trim();
    if(filePath.indexOf("http") == 0 || filePath.indexOf('\\') == 0)
    {
      if(filePath.indexOf('\\') == 0) {
            filePath = "file:" + filePath;
        }
      try
      {
        filePath = filePath.replaceAll(" ", "%20");
        URL url = new URL(filePath);
        uri = url.toURI();
      } catch (MalformedURLException ex)
      {
         Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (URISyntaxException ex)
      {
         Logger.getLogger(Cfg.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    else
    {
      File file = new File(filePath);
      uri = file.toURI();
    }
     
    return uri;
  }
    
  public static void saveUrl(String filename, String urlString) throws MalformedURLException, IOException
  {
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
    		in = new BufferedInputStream(new URL(urlString).openStream());
    		fout = new FileOutputStream(filename);

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally
    	{
    		if (in != null) {
                in.close();
            }
    		if (fout != null) {
                fout.close();
            }
    	}
    }

    public CoreCfg getCoreCfg() {
        return coreCfg;
    }
}
