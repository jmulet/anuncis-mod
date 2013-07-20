/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

/**
 *
 * @author Josep
 */
public class PreferencesBean {
    private static PreferencesBean instance;
    
    //Properties
    private boolean sounds = true;
    private int typeAnuncis = 0;
    private String background = "*suro.png";
    private final Cfg cfg;
 
    private PreferencesBean(Cfg cfg)
    {
        //Non instanciable class
          
       this.cfg = cfg;
    }
    
    public final void load()
    {         
        setSounds(cfg.getCoreCfg().getUserPreferences().getProperty("anuncis.sounds", "1").equals("1"));
        setTypeAnuncis(Integer.parseInt(cfg.getCoreCfg().getUserPreferences().getProperty("anuncis.showTypeAnuncis", "0")));
        setBackground(cfg.getCoreCfg().getUserPreferences().getProperty("anuncis.background", "*suro.png"));
    }
    
    public final void save()
    {
        cfg.getCoreCfg().saveIniProperties();
      
    }
    
    public static PreferencesBean getInstance(Cfg cfg)
    {
        if(instance==null)
        {
            instance = new PreferencesBean(cfg);
        }
        
        return instance;
    }

   

    public boolean isSounds() {         
        return sounds;
    }

    public void setSounds(boolean sounds) {
        this.sounds = sounds;
        cfg.getCoreCfg().setUserPreferences("anuncis.sounds", sounds?"1":"0");
    }

    public int getTypeAnuncis() {
        return typeAnuncis;
    }

    public void setTypeAnuncis(int typeAnuncis) {
        this.typeAnuncis = typeAnuncis;
        cfg.getCoreCfg().setUserPreferences("anuncis.showTypeAnuncis", this.typeAnuncis+"");
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
        cfg.getCoreCfg().setUserPreferences("anuncis.background", background+"");
    }
    
}
