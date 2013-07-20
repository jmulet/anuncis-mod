/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class DetailedAnunci extends javax.swing.JDialog {
    private AnunciBean bean;
    private int fontSize = 14;

    /**
     * Creates new form DetailedAnunci
     */
    public DetailedAnunci(java.awt.Frame parent, final AnunciBean bean) {
        super(parent, false);
        initComponents();
        setBean(bean);
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, fontSize));
      
        //if(Cfg.moduleGrant.get("print_anuncis").getValue()!=GrantBean.NONE)
        {
            
            JButton jLinkButton3 = new JButton();
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
                                jTextPane1.print();
                            } catch (PrinterException ex) {
                                Logger.getLogger(MyAnunci.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        }.start();
                        
                   
                }
            });
            jToolBar1.add(jLinkButton3);
            jToolBar1.add(new JSeparator());
            
            JButton jLinkButton4 = new JButton();
            jLinkButton4.setToolTipText("Desa");
            jLinkButton4.setMargin(new Insets(2,2,2,2));
            jLinkButton4.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/anuncis/icons/save.gif")));
            jLinkButton4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Desa el fitxer a disc
                JFileChooser fc = new JFileChooser();
                fc.setDialogType(JFileChooser.SAVE_DIALOG);
                fc.setCurrentDirectory(new File(System.getProperty("user.home")));
                fc.setSelectedFile(new File("\\anunci"+bean.getDbId()+".doc"));
                if(fc.showSaveDialog(DetailedAnunci.this)==JFileChooser.APPROVE_OPTION && 
                        !fc.getSelectedFile().isDirectory())
                {
                    FileWriter fw = null;
                    try {
                        fw = new FileWriter(fc.getSelectedFile());
                        String doc = bean.getBody();
                        if(!bean.getBody().contains("<body>"))
                        {
                            doc = "<html><body>"+bean.getBody()+"</body></html>";
                        }
                        if(!bean.getBody().contains("<html>"))
                        {
                            doc = "<html>"+doc+"</html>";
                        }
                        
                        fw.write(doc);
                        fw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DetailedAnunci.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            fw.close();
                            Desktop.getDesktop().open(fc.getSelectedFile());
                        } catch (Exception ex) {
                            Logger.getLogger(DetailedAnunci.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                     
             }
            });
            jToolBar1.add(jLinkButton4);
        }
      
        jTextPane1.addHyperlinkListener(new HyperlinkListener() {
            

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                  
                if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                {
                    try {
                        URL url;
                       
                               
                         
                        if(e.getDescription().startsWith("file") ||
                           !e.getDescription().contains(CoreCfg.cloudBaseURL))   //This is a local file or html page
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
                            //System.out.println(destination);
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
                    
                    jTextPane1.setToolTipText(null);
                }
                else if(e.getEventType().equals(HyperlinkEvent.EventType.ENTERED))
                {
                    jTextPane1.setToolTipText(e.getDescription());
                }
                else if(e.getEventType().equals(HyperlinkEvent.EventType.EXITED))
                {
                    jTextPane1.setToolTipText(null);
                }
                
                
        }});
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setText("A+");
        jButton1.setToolTipText("Lletra més gran");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jSeparator1.setPreferredSize(new java.awt.Dimension(10, 0));
        jToolBar1.add(jSeparator1);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton2.setText("A-");
        jButton2.setToolTipText("Lletra més petita");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jSeparator2.setPreferredSize(new java.awt.Dimension(10, 0));
        jToolBar1.add(jSeparator2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel1.setText("Publicat per:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel2.setText("Publicat dia:");

        jLabel3.setText(" ");

        jLabel4.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

       
        
    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        try {
           Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
           Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);           
           mSetWindowOpacity.invoke(null, this, Float.valueOf(0.75f));
           mSetWindowOpacity.invoke(null, this, Float.valueOf(0.75f));
            
        } catch (Exception ex) {
            //Logger.getLogger(AnuncisDlg.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }//GEN-LAST:event_formWindowLostFocus

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
          try {
           Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
           Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);           
           mSetWindowOpacity.invoke(null, this, Float.valueOf(1f));
           mSetWindowOpacity.invoke(null, this, Float.valueOf(1f));
            
        } catch (Exception ex) {
            //Logger.getLogger(AnuncisDlg.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }//GEN-LAST:event_formWindowGainedFocus

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        fontSize +=2;
        if(fontSize>40)
        {
            fontSize=40;
        }
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, fontSize)); // NOI18N
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        fontSize -=2;
        if(fontSize<4)
        {
            fontSize=4;
        }
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, fontSize)); // NOI18N
    }//GEN-LAST:event_jButton2ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    public final void setBean(AnunciBean bean) {
        this.bean = bean;
        String titulo = "";
        if(bean.getShowDateInTitle()>0)
        {
            titulo += new DataCtrl(bean.getEventdate()).getDiaMesComplet()+" : ";
        }
        titulo += bean.getTitle();
        jTextPane1.setText(bean.getBody());
        jTextPane1.setCaretPosition(0);
        this.setTitle(titulo);
        this.setIconImage(AnuncisModule.getIcon(bean.getIconType(), bean.getIconName()).getImage());
        jLabel3.setText(bean.getAuthor());
        jLabel4.setText(new DataCtrl(bean.getPostdate()).getDiaMesComplet());
         
    }
}
