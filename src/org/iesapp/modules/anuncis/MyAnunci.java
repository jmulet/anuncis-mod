/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import com.l2fprod.common.swing.JLinkButton;
import com.l2fprod.common.swing.JTaskPaneGroup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */ //JTaskPaneGroup
public class MyAnunci extends JTaskPaneGroup {
    
    JTextPane body;
    int globalId = -1;
    protected AnunciBean bean;
    private JPanel jPanel1;
    private boolean zone1;
    private boolean zone2;
    private String trimmedBody;
    public static DetailedAnunci detailedAnunci;
    private final JFrame anuncisFrame;
    private final Cfg cfg;
    private final AnuncisModule anuncisModule;

    public MyAnunci(AnunciBean bean, JFrame anuncisFrame, AnuncisModule anuncisModule, Cfg cfg) {
        this.cfg = cfg;
        this.bean = bean;
        this.anuncisFrame = anuncisFrame;
        this.anuncisModule = anuncisModule;
        this.setScrollOnExpand(true);
        this.setAutoscrolls(true);
        this.setAnimated(false);
        
        this.setToolTipText(bean.getDescription());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0); 
        cal.set(Calendar.MILLISECOND,0);
        
        //We must trim posts which are too long
        //Trimmed posts will include hiperlink [...MORE] 
        //in order to open DetailedAnunci window
        
        //You can control the position of the trimming by modifing
        //the location of the tag <!--TRIM_POSITION-->
        
        generateTrimmedBody();
        
        Date now = cal.getTime();
        
        //Administra el collapse d'anuncis segons el parametre de l'anunci
        this.setExpanded(bean.getCollapse()==0);
        
        //Gestiona anuncis passats
        if(cfg.collapsaAnuncisPassats==1)
        {
            //Anunci simple
            if (bean.getEventdate() != null && bean.getEventdate2()==null && (bean.getEventdate().before(now))) {
               this.setExpanded(false);
            } else  if (bean.getEventdate() != null && bean.getEventdate2()!=null && (bean.getEventdate2().before(now))) {
               this.setExpanded(false);
            } 
        }
        else if(cfg.collapsaAnuncisPassats==2)
        {
              //Anunci simple
            if (bean.getEventdate() != null && bean.getEventdate2()==null && (bean.getEventdate().before(now))) {
               this.setVisible(false);
            } else  if (bean.getEventdate() != null && bean.getEventdate2()!=null && (bean.getEventdate2().before(now))) {
               this.setVisible(false);
            } 
        }
       
               
        initialize();
       //Si l'anunci és SIMPLE i té com dia de l'event avui la remarca
        if (bean.getEventdate() != null && bean.getEventdate2()==null && (bean.getEventdate().equals(now))) {
           this.getContentPane().setBackground(Color.orange);           
        } 
        //Si l'anunci és MULTIPLE DAYS i avui esta compres entre aquests dies, la remarca
        else if (bean.getEventdate() != null && bean.getEventdate2()!=null && (bean.getEventdate().compareTo(now)<=0
                && now.compareTo(bean.getEventdate2())<=0)) {
           this.getContentPane().setBackground(Color.orange);           
        } 
        
        jPanel1.setVisible(false);
       
        String diastr="";
        if(bean.getShowDateInTitle()>0){
            diastr = new DataCtrl(bean.getEventdate()).getDiaMesComplet()+": ";
        }
        this.setTitle(diastr+bean.getTitle());
        this.setIcon(AnuncisModule.getIcon(bean.getIconType(), bean.getIconName()));
        body.setFont(new java.awt.Font("Tahoma", 0, 11));
        body.setText(trimmedBody);
        body.setCaretPosition(0);
        globalId = bean.getGlobalId();
        body.setToolTipText("Creat per "+bean.getAuthor()+ " dia "+ new DataCtrl(bean.getPostdate()).getDiaMesComplet());        
     
        java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
           
           @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
               Component source = evt.getComponent();
               
               if(source.getClass().equals(MyAnunci.class))
               {
                  ((MyAnunci) source).jPanel1.setVisible(true);
               }
            }
             @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
             
                    Point p1 = evt.getLocationOnScreen();
                    Point p2 = MyAnunci.this.getLocationOnScreen();
                    int w = MyAnunci.this.getWidth();
                    int h = MyAnunci.this.getHeight();
                    if( (p2.x<p1.x && p1.x<(p2.x+w)) && (p2.y<p1.y && p1.y<(p2.y+h)) )
                    {
                        jPanel1.setVisible(true);
                    }
                    else
                    {
                        jPanel1.setVisible(false);
                    }
            }

        };
        this.addMouseListener(mouseAdapter);
         
    }

     
    
    private void initialize()
    {
        //System.out.println("on initalize in myanunci");
        
        //JTaskPane jtpane = new JTaskPane();
        body = new JTextPane();
        body.setEditable(false);
        body.setContentType("text/html");
        body.setOpaque(false);
        body.setBorder(null);
        
        body.addHyperlinkListener(new HyperlinkListener() {
            

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                  
                if(e.getEventType().equals(EventType.ACTIVATED))
                {
                    try {
                        URL url;
                        
                        if(e.getDescription().isEmpty())
                        {
                            //We want detailed description
                            if(detailedAnunci==null)
                            {
                                detailedAnunci = new DetailedAnunci(javar.JRDialog.getActiveFrame(),bean);
                                detailedAnunci.setLocation(0, anuncisFrame.getLocation().y);
                                detailedAnunci.setSize(anuncisFrame.getLocation().x,anuncisFrame.getLocation().y+anuncisFrame.getSize().height);
                                detailedAnunci.setVisible(true);
                                
                            }
                            else
                            {
                                detailedAnunci.setBean(bean);
                                detailedAnunci.setLocation(0, anuncisFrame.getLocation().y);
                                detailedAnunci.setSize(anuncisFrame.getLocation().x,anuncisFrame.getLocation().y+anuncisFrame.getSize().height);
                                detailedAnunci.setVisible(true);
                            }
                            
                            return;
                        }
                        
                        String pattern = StringUtils.noNull(CoreCfg.cloudBaseURL).replace("webresources/", "");
                         
                        if(e.getDescription().startsWith("file") ||
                           !e.getDescription().contains(pattern))   //This is a local file or html page
                        {
                            url = new URL(e.getDescription());
                            Desktop.getDesktop().browse(url.toURI());
                        }
                        else //This is a file from server (download it and open it)
                        {
                            String fileName = StringUtils.AfterLast(e.getDescription(),"/");
                            boolean containsDispo = fileName.contains("?");
                            String urlText = e.getDescription();
                            if(containsDispo)
                            {
                                fileName = StringUtils.BeforeLast(fileName, "?");
                            }
                            else
                            {
                                urlText = e.getDescription()+"?disposition=attachment";
                            }
                            String destination = System.getProperty("java.io.tmpdir")+File.separator+fileName;
                            ////System.out.println(destination);
                            Cfg.saveUrl( destination, urlText);
                            Desktop.getDesktop().browse(new File(destination).toURI());
                        }
                        
                        } 
                        catch (URISyntaxException ex) {
                            //Logger.getLogger(MyAnunci.class.getName()).log(Level.SEVERE, null, ex);
                            Cfg.launchFile(e.getDescription());
                        }   
                        catch (IOException ex) {
                        }
                    body.setToolTipText(null);
                }
                else if(e.getEventType().equals(EventType.ENTERED))
                {
                    body.setToolTipText(e.getDescription());
                }
                else if(e.getEventType().equals(EventType.EXITED))
                {
                    body.setToolTipText(null);
                }
                
                
        }});
        
        this.add(body);
        
        boolean isMine = cfg.getCoreCfg().getUserInfo()!=null && getBean().getAbrev().equals(cfg.getCoreCfg().getUserInfo().getAbrev());
        //Condicio de quan puc editar un anunci (si soc full o si soc restringit i l'anunci es meu)
        boolean condition0 = cfg.getCoreCfg().getUserInfo()!=null && (cfg.fullEditingUsers.contains(cfg.getCoreCfg().getUserInfo().getAbrev()) || 
              (cfg.allowedEditingUsers.contains(cfg.getCoreCfg().getUserInfo().getAbrev()) && isMine));
        
        boolean editableCondition = condition0 || 
                (cfg.moduleGrant.get("edit_all_anuncis").getValue()!=GrantBean.NONE ||
                (cfg.moduleGrant.get("edit_own_anuncis").getValue()!=GrantBean.NONE) && isMine );
        
        boolean deletableCondition = condition0 || 
                (cfg.moduleGrant.get("delete_all_anuncis").getValue()!=GrantBean.NONE ||
                (cfg.moduleGrant.get("delete_own_anuncis").getValue()!=GrantBean.NONE) && isMine );
            
        //System.out.println("conditions done!");
       jPanel1 = new JPanel();
       jPanel1.setOpaque(false);

       JLinkButton jLinkButton0 = new JLinkButton();
       jLinkButton0.setToolTipText("Amplia");
       jLinkButton0.setIcon(new ImageIcon(MyAnunci.class.getResource("/org/iesapp/modules/anuncis/icons/zoom.gif")));
       jLinkButton0.addActionListener(new ActionListener(){
            private Dimension screenSize;

            @Override
           public void actionPerformed(ActionEvent e) {
               //We want detailed description
               if (detailedAnunci == null) {
                   detailedAnunci = new DetailedAnunci(javar.JRDialog.getActiveFrame(), bean);
                   
                   screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                   detailedAnunci.setSize((int) (screenSize.width*0.8), (int) (screenSize.height*0.9) );
                   detailedAnunci.setLocationRelativeTo(null);
                   detailedAnunci.setVisible(true);

               } else {
                   detailedAnunci.setBean(bean);
                   screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                   detailedAnunci.setSize((int) (screenSize.width*0.8), (int) (screenSize.height*0.9) );
                   detailedAnunci.setLocationRelativeTo(null);
                  detailedAnunci.setVisible(true);
               }
           }
       });
       jPanel1.add(jLinkButton0);
       
        if( editableCondition )
        {  
            JLinkButton jLinkButton2 = new JLinkButton();
            jLinkButton2.setToolTipText("Edita");
            jLinkButton2.setMargin(new Insets(2,2,2,2));
            jLinkButton2.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/anuncis/icons/write.png")));
            if (getBean().getDbId() <= 0) {
                jLinkButton2.setEnabled(false);
            }

            jLinkButton2.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    edita();
                }
            });
            jPanel1.add(jLinkButton2);
        }
        
        
        if(deletableCondition)
        {
            JLinkButton jLinkButton1 = new JLinkButton();
            jLinkButton1.setToolTipText("Esborra");
            jLinkButton1.setMargin(new Insets(2,2,2,2));
            jLinkButton1.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/anuncis/icons/delete.png")));

            if (getBean().getDbId() <= 0) {
                jLinkButton1.setEnabled(false);
            }
            jLinkButton1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    delete();
                }
            });

            jPanel1.add(jLinkButton1);
        }  
        
        if(cfg.moduleGrant.get("print_anuncis").getValue()!=GrantBean.NONE)
        {
            
            JLinkButton jLinkButton3 = new JLinkButton();
            jLinkButton3.setToolTipText("Imprimeix");
            jLinkButton3.setMargin(new Insets(2,2,2,2));
            jLinkButton3.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/anuncis/icons/printer.png")));
            jLinkButton3.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
              
                        if(!AnuncisModule.printerStarted)
                        {
                            INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(350).withAlpha(0.86f);

                    // Now lets build the notification
                new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Siau pacients") // Required.
                    .withMessage("S'està engegant el sistema d'impressió. Pot trigar uns instants...") // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_INFO)) // Optional. You could also use a String path
                    .withDisplayTime(6000) // Optional
                    .withPosition(Positions.SOUTH_EAST) // Optional. Show it at the center of the screen
                    .showNotification();
                  }
                            
                        AnuncisModule.printerStarted = true;
                        
                        
                        new Thread(){
                            
                        @Override
                        public void run() {
                            try {
                                body.print();
                            } catch (PrinterException ex) {
                                Logger.getLogger(MyAnunci.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        }.start();
                        
                   
                }
            });
            
            jPanel1.add(jLinkButton3); 
            }


          
            this.add(jPanel1);
    
        //
         //System.out.println("Done!");
    }
    
    public void delete()
    {
        if(getBean().getType()==AnunciBean.SORTIDA && this.getBean().getEventdate()!=null && getBean().getEventdate().before(new java.util.Date()))
        {
             Object[] options = {"D'acord"};
             String missatge = "Ho sentim. No es permet esborrar\nsortides que ja s'han duit a terme.";

            int n = JOptionPane.showOptionDialog(this,
                missatge, "Alerta",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
            return;
        }
                
        Object[] options = {"No", "Sí"};
        String missatge;
        if(getBean().getType()==AnunciBean.SORTIDA) {
            missatge = "Voleu esborrar aquesta sortida,\njuntament amb les faltes del professorat?";
        }
        else {
            missatge = "Voleu esborrar aquest anunci?";
        }
        int n = JOptionPane.showOptionDialog(this,
                missatge, "Confirmació",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n != 1) {
            return;
        }

        String SQL1 = "DELETE FROM sig_missatges WHERE id=" + getBean().getDbId();
        int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
        this.setVisible(false);
        
        //Esborra les faltes del professorat si es tracta d'una sortida
         if(getBean().getType()==AnunciBean.SORTIDA && nup>0)
         {
               ArrayList<String> parsedArray = StringUtils.parseStringToArray(getBean().getProfessorat(), ":", StringUtils.CASE_INSENSITIVE);
         
               for(String prof: parsedArray)
               {
                 String abrev = StringUtils.AfterLast(prof,"[");
                 abrev = StringUtils.BeforeFirst(abrev,"]");                
                 String sqlDate = new DataCtrl(getBean().getEventdate()).getDataSQL();
             
                 SQL1 = "DELETE FROM sig_signatures WHERE data='"+sqlDate+"' AND abrev='"+abrev+"'";
                 cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
           }
         }
    }
   
    public void edita()
    {
       // anuncisFrame.setEditing(true);
        ShefEditor ed = new ShefEditor(anuncisFrame, true, cfg);
        ed.setBean(getBean());
        
       
        Point loc = anuncisFrame.getLocationOnScreen();
        Dimension size = anuncisFrame.getSize();     
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ed.setSize((int) (screenSize.width*0.8), (int) (screenSize.height*0.9) );
        ed.setLocationRelativeTo(null);
      
        ed.setVisible(true);
        //anuncisFrame.setEditing(false);
        ed.dispose();
        //Anuncis list must be updated
        anuncisModule.fillList();
        
    }

    public AnunciBean getBean() {
        return bean;
    }

    private void generateTrimmedBody() {
        
        trimmedBody = bean.getBody();
        int id0 = bean.getBody().indexOf("<!--TRIM_POSITION-->");
        int id1 = bean.getBody().indexOf("</body>");
       // //System.out.println("Calling generateTrimmedBody...."+id0+" - "+id1);
        if(id0>0)
        {
           trimmedBody = bean.getBody().substring(0,id0);
           trimmedBody += "<center><p><a href=\"\">SEGUEIX LLEGINT...</a></p></center>";
           if(id1>0)
           {
                trimmedBody += bean.getBody().substring(id1,bean.getBody().length()-1);
           }
           
           ////System.out.println("Trimmed: "+trimmedBody);
        }
    }
 
            
}
