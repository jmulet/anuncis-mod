/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.clients.iesdigital.anuncis.AnuncisDefinition;
import org.iesapp.clients.iesdigital.guardies.Presencia;
import org.iesapp.clients.sgd7.mensajes.MensajesListas;
import org.iesapp.clients.sgd7.mensajes.MensajesListasProfesores;
import org.iesapp.cloudws.client.CloudClientSession;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class ShefEditor extends javax.swing.JDialog {
    private final HTMLDocument document;
    private final DefaultComboBoxModel model;
    private AnunciBean bean;
    private final DefaultComboBoxModel comboModelDesde;
    private final DefaultComboBoxModel comboModelFins;
    private boolean accept;
    private boolean listening;
    private final CloudClientSession session;
    private final Cfg cfg;

    /**
     * Creates new form ShefEditor
     */
    public ShefEditor(Frame parent, boolean modal, final Cfg cfg) {
        super(parent, modal);
        this.cfg = cfg;
        initComponents();
        this.setTitle("iesDigital "+CoreCfg.VERSION+": Editor d'anuncis");
         
        //Ha d'iniciar la sessio pel cloud
        formatDateEventFields();
        
        CloudClientSession.baseURL = CoreCfg.cloudBaseURL;
        session = new CloudClientSession(cfg.getCoreCfg().getUserInfo().getSystemUser(),cfg.getCoreCfg().getUserInfo().getClaveUP());
        this.editor1.setCloudClientSession(session);
        
        java.util.Date avui = new java.util.Date();
        jDateChooser1.setMinSelectableDate(avui);
        jDateChooser2.setMinSelectableDate(avui);
        jDateChooser1.getJCalendar().getDayChooser().addDateEvaluator(new org.iesapp.framework.util.FestiusDateEvaluator(avui, null,cfg.getCoreCfg()));
        jDateChooser2.getJCalendar().getDayChooser().addDateEvaluator(new org.iesapp.framework.util.FestiusDateEvaluator(avui, null,cfg.getCoreCfg()));
        
        
        jMenuBar1.add(editor1.getFileMenu());
        jMenuBar1.add(editor1.getEditMenu());
        jMenuBar1.add(editor1.getFormatMenu());        
        jMenuBar1.add(editor1.getInsertMenu());
      
        document = editor1.getHtmlDocument();
        
        model = new DefaultComboBoxModel();
        comboModelDesde = new DefaultComboBoxModel();
        comboModelFins = new DefaultComboBoxModel();
        jComboBoxDesde.setModel(comboModelDesde);
        jComboBoxFins.setModel(comboModelFins);
        
        
        for(AnuncisDefinition b: AnuncisDefinition.getMapDefined().values())
        {
            model.addElement(b.getAnuncisTypeName());
        }
        jComboBoxTipus.setModel(model);

        jComboBoxTipus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(bean==null)
                {
                    return;
                }
                int sel = jComboBoxTipus.getSelectedIndex(); 
                bean.setType((byte)sel);
                jIcon.setIcon(AnuncisModule.getIcon(bean.getIconType(), bean.getIconName()));
                
                jExtraescolars.setVisible(jComboBoxTipus.getSelectedIndex()==0);
               
                if(sel>=0 && bean!=null && bean.getDbId()<=0)
                {
                    editor1.setText(AnuncisDefinition.getMapDefined().get(sel).getDocumentTemplate());             
                    editor1.setCaretPosition(0);
                }
            }
        });
        
         jTitol.addKeyListener( new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                 Element element2 = document.getElement("lloc2");
                 try {
                    document.setInnerHTML(element2, jTitol.getText());
                } catch (BadLocationException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       
        ActionListener actionlistener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element element2 = document.getElement("diahores2");
                StringBuilder strbuild = new StringBuilder(" Dia ");
                if (jDateChooser1.getDate() != null) {
                    strbuild.append(new DataCtrl(jDateChooser1.getDate()).getDiaMesComplet());
                }

                strbuild.append(" de ").append(StringUtils.BeforeLast((String) jComboBoxDesde.getSelectedItem(), "-"))
                        .append(" a ").append(StringUtils.AfterLast((String) jComboBoxFins.getSelectedItem(), "-"));
                try {
                    document.setInnerHTML(element2, strbuild.toString());
                } catch (BadLocationException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };

           
        PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
           
                Element element2 = document.getElement("diahores2");
                StringBuilder strbuild = new StringBuilder(" Dia ");
                if (jDateChooser1.getDate() != null) {
                    strbuild.append(new DataCtrl(jDateChooser1.getDate()).getDiaMesComplet());
                }

                strbuild.append(" de ").append(StringUtils.BeforeLast((String) jComboBoxDesde.getSelectedItem(), "-"))
                        .append(" a ").append(StringUtils.AfterLast((String) jComboBoxFins.getSelectedItem(), "-"));
                try {
                    document.setInnerHTML(element2, strbuild.toString());
                } catch (BadLocationException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

           
        };
        jComboBoxDesde.addActionListener(actionlistener);
        jComboBoxFins.addActionListener(actionlistener);
        jDateChooser1.getDateEditor().addPropertyChangeListener(propertyChangeListener);
        jDateChooser2.getDateEditor().addPropertyChangeListener(propertyChangeListener);

        //Create a model for jcomboBoxDesde i fins
        String SQL1 = "SELECT * FROM sig_hores_classe where idTipoHoras=2 and fin<='15:00:00' order by inicio";
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                String tmp = StringUtils.formatTime(rs1.getTime("inicio"))+"-"+StringUtils.formatTime(rs1.getTime("fin"));
                comboModelDesde.addElement(tmp);
                comboModelFins.addElement(tmp);
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
                
      
        fieldProfessorat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String text = fieldProfessorat.getText();
                ArrayList<String> parsedArray = StringUtils.parseStringToArray(text, ":", StringUtils.CASE_INSENSITIVE);
                ProfChooser dlg = new ProfChooser(javar.JRDialog.getActiveFrame(), true, jDateChooser1.getDate(),cfg);
                dlg.setProfes(parsedArray);
                dlg.setLocationRelativeTo(fieldProfessorat);
                dlg.setVisible(true);
                ArrayList<String> profes = dlg.getProfes();
                text = "";
                for (int i = 0; i < profes.size(); i++) {
                    text += profes.get(i) + " : ";
                }
                fieldProfessorat.setText(text);



                Element element2 = document.getElement("professorat2");
                try {
                    ArrayList<String> parseStringToArray = StringUtils.parseStringToArray(text, ":", StringUtils.CASE_INSENSITIVE);
                    StringBuilder strbuild = new StringBuilder("<ul>");
                    for (String str : parseStringToArray) {
                        if (!str.isEmpty()) {
                            strbuild.append("<li>").append(StringUtils.BeforeLast(str, "[")).append("</li>");
                        }
                    }
                    strbuild.append("</ul>");
                    document.setInnerHTML(element2, strbuild.toString());

                    strbuild = null;
                    parseStringToArray = null;

                } catch (BadLocationException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ShefEditor.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
 
        jComboFor.setModel(cfg.combomodelGList);
        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editor1 = new net.atlanticbb.tantlinger.shef.HTMLEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(32767, 0));
        jLabel1 = new javax.swing.JLabel();
        jComboFor = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(32767, 0));
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTitol = new javax.swing.JTextField();
        jComboBoxTipus = new javax.swing.JComboBox();
        jIcon = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jDesc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jCheckBox1 = new javax.swing.JCheckBox();
        jExtraescolars = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jComboBoxDesde = new javax.swing.JComboBox();
        jComboBoxFins = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        fieldProfessorat = new javax.swing.JTextField();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButton1.setText("Cancel·la");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jPanel1.add(filler1);

        jLabel1.setText("Per a");
        jPanel1.add(jLabel1);

        jComboFor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(jComboFor);
        jPanel1.add(filler2);

        jButton2.setText("Publica");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        jLabel2.setText("Tipus");

        jLabel3.setText("Titol");

        jTitol.setText(" ");

        jComboBoxTipus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcon.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jIconMouseClicked(evt);
            }
        });

        jLabel6.setText("Data");

        jDateChooser1.setLocale(new java.util.Locale("ca"));

        jLabel4.setText("Descripció breu");

        jDesc.setText(" ");

        jLabel7.setText("Fins");

        jDateChooser2.setLocale(new java.util.Locale("ca"));

        jCheckBox1.setText("Més d'un dia");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jExtraescolars.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Hores des de");

        jLabel9.setText("fins");

        jComboBoxDesde.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxFins.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel10.setText("Professors");

        fieldProfessorat.setEditable(false);
        fieldProfessorat.setText(" ");

        javax.swing.GroupLayout jExtraescolarsLayout = new javax.swing.GroupLayout(jExtraescolars);
        jExtraescolars.setLayout(jExtraescolarsLayout);
        jExtraescolarsLayout.setHorizontalGroup(
            jExtraescolarsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jExtraescolarsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxFins, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldProfessorat)
                .addContainerGap())
        );
        jExtraescolarsLayout.setVerticalGroup(
            jExtraescolarsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jExtraescolarsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jExtraescolarsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jComboBoxDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxFins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(fieldProfessorat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("Mostra data en titol");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Collapsa ");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jExtraescolars, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(55, 55, 55)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxTipus, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTitol)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDesc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox3)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxTipus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jTitol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox2))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox3)))
                    .addComponent(jIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jCheckBox1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jExtraescolars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(editor1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(editor1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Choose icon
    private void jIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jIconMouseClicked
         IconChooser dlg = new IconChooser(javar.JRDialog.getActiveFrame(),true);
         dlg.setLocationRelativeTo(null);
         dlg.setVisible(true);
         
         String nomicona = dlg.getInonName();
         String tipusicona = dlg.getIconType();
         if(nomicona!=null)
         {
             bean.setIconName(nomicona);
             bean.setIconType(tipusicona);
             jIcon.setIcon(AnuncisModule.getIcon(bean.getIconType(), bean.getIconName()));
         }
         dlg.dispose();
         
    }//GEN-LAST:event_jIconMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        save();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        formatDateEventFields();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(session!=null)
        {
            session.close();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.atlanticbb.tantlinger.shef.HTMLEditorPane editor1;
    private javax.swing.JTextField fieldProfessorat;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox jComboBoxDesde;
    private javax.swing.JComboBox jComboBoxFins;
    private javax.swing.JComboBox jComboBoxTipus;
    private javax.swing.JComboBox jComboFor;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JTextField jDesc;
    private javax.swing.JPanel jExtraescolars;
    private javax.swing.JLabel jIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTitol;
    // End of variables declaration//GEN-END:variables

 /**
     * @return the accept
     */
    public boolean isAccept() {
        return accept;
    }

    /**
     * @param accept the accept to set
     */
    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getText() {
         String replaceAll = editor1.getText().replaceAll("#EEDDDD", "#FFFFFF");
         return replaceAll;
    }

    public void setText(String text) {
        editor1.setText(text.replaceAll("#FFFFFF", "#EEDDDD"));
    }

    /**
     * @return the bean
     */
//    public AnunciBean getBean() {
//        
//        bean.setType((byte) jComboBoxTipus.getSelectedIndex());
//        bean.setTitle(jTitol.getText());
//        bean.setDescription(jDesc.getText());
//        bean.setEventdate(jDateChooser1.getDate());
//        return bean;
//    }

    /**
     * @param bean the bean to set
     */
    public void setBean(AnunciBean bean) {
        this.bean = bean;  //aixo es un puntero
        listening = false;
        
        //Cerca el tipus
        int pos =0;
        int i = 0;
        for(AnuncisDefinition b: AnuncisDefinition.getMapDefined().values())
        {
            if(b.getAnuncisTypeId()==bean.getType())
            {
                pos = i;
                break;
            }
            i += 1;
        }
        jComboBoxTipus.setSelectedIndex(pos);
        jComboFor.setSelectedItem(bean.getPara());

        jTitol.setText(bean.getTitle());
        jDesc.setText(bean.getDescription());
        jDateChooser1.setDate(bean.getEventdate());
        jDateChooser2.setDate(bean.getEventdate2());
        jCheckBox1.setSelected(bean.getEventdate2()!=null);
        fieldProfessorat.setText(bean.getProfessorat());
        jComboBoxDesde.setSelectedIndex(bean.gethIndex0());
        jComboBoxFins.setSelectedIndex(bean.gethIndex1());
        jIcon.setIcon(AnuncisModule.getIcon(bean.getIconType(), bean.getIconName()));
        listening = true;
        formatDateEventFields();
        jCheckBox2.setSelected(bean.getShowDateInTitle()>0);
        jCheckBox3.setSelected(bean.getCollapse()>0);
        this.setText(bean.getBody());
        //System.out.println(bean.dbId+" "+Cfg.templates);
        
        //Indica que es un document nou
        if(bean.getDbId()<=0)
        {
             editor1.setText(AnuncisDefinition.getMapDefined().get(0).getDocumentTemplate());
        }
        else
        {
//              jcomboBox3.setVisible(false);
//              jLabel11.setVisible(false);
        }
        
     
        jExtraescolars.setVisible(bean.getType() == AnunciBean.SORTIDA);
         
    }
    
        private void save()
        {
                  
        //Check required fields
        //A description is required
        if(jTitol.getText().trim().isEmpty())
        {
         
            INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);

            // Now lets build the notification
            new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Atenció") // Required.
                    .withMessage("Cal que afegiu un titol per a l'anunci.") // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_EXCLAMATION)) // Optional. You could also use a String path
                    .withDisplayTime(5000) // Optional
                    .withPosition(Positions.CENTER) // Optional. Show it at the center of the screen
                    .showNotification();
                     
            jTitol.requestFocus();
            return;
        }
        
        //A date for the event is required
        if((jDateChooser1.getDate()==null))
        {
            
            INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);

            // Now lets build the notification
            new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Atenció") // Required.
                    .withMessage("Es requereix una data vàlida per a l'anunci.") // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_EXCLAMATION)) // Optional. You could also use a String path
                    .withDisplayTime(5000) // Optional
                    .withPosition(Positions.CENTER) // Optional. Show it at the center of the screen
                    .showNotification();
            jDateChooser1.requestFocus();
            return;
        }
      
          //A date2 for the event is required when both dates are there
        if(jCheckBox1.isSelected() &&((jDateChooser1.getDate()==null) || (jDateChooser2.getDate()==null ||
                jDateChooser1.getDate().after(jDateChooser2.getDate()))))
        {
            
            INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);

            // Now lets build the notification
            new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Atenció") // Required.
                    .withMessage("Es requereixen dates d'inici i fi vàlides per a l'anunci.") // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_EXCLAMATION)) // Optional. You could also use a String path
                    .withDisplayTime(5000) // Optional
                    .withPosition(Positions.CENTER) // Optional. Show it at the center of the screen
                    .showNotification();
                    jDateChooser2.requestFocus();
            return;
        }
      
        //ini and fi is also required
        if(jComboBoxDesde.getSelectedIndex()>jComboBoxFins.getSelectedIndex())
        {
           
            INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);

            // Now lets build the notification
            new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                    .withTitle("Atenció") // Required.
                    .withMessage("Cal que especifiqueu unes hores d'inici i fi vàlides per l'activitat.") // Optional
                    .withIcon(new ImageIcon(NotificationBuilder.ICON_EXCLAMATION)) // Optional. You could also use a String path
                    .withDisplayTime(5000) // Optional
                    .withPosition(Positions.CENTER) // Optional. Show it at the center of the screen
                    .showNotification();
            return;
        }
        
        
        
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy");
        
        String sdate = "";
        String sdate2= "";
        if(jDateChooser1.getDate()!=null) {
            sdate = formatter.format(jDateChooser1.getDate());
         }
        else
        {
            sdate = "null";
        }
         
        if(jDateChooser2.getDate()!=null) {
            sdate2 = formatter.format(jDateChooser2.getDate());
         }
        else
        {
            sdate2 = "null";
        }
        
        int sel = jComboBoxTipus.getSelectedIndex();
        int id = AnuncisDefinition.getMapDefined().get(sel).getAnuncisTypeId();
        
        String doc2="title={"+jTitol.getText()+
                "};description={"+jDesc.getText()+
                "};body={"+editor1.getText()+
                "};type={"+id+
                "};eventdate={"+sdate+
                "};eventdate2={"+sdate2+
                "};postdate={"+formatter.format(bean.getPostdate()) +
                "};author={"+bean.getAuthor()+
                "};professorat={"+fieldProfessorat.getText()+
                "};hIndex0={"+jComboBoxDesde.getSelectedIndex()+
                "};hIndex1={"+jComboBoxFins.getSelectedIndex()+
                "};iconName={"+bean.getIconName()+
                "};iconType={"+bean.getIconType()+
                "};collapse={"+(jCheckBox3.isSelected()?"1":"0")+
                "};showDateInTitle={"+(jCheckBox2.isSelected()?"1":"0")+
                "};para={"+jComboFor.getSelectedItem()+
                "};";
        
        //Gestiona els destinataris
        String para= "";
        String selected = (String) jComboFor.getSelectedItem();
        if(selected.startsWith("[")) //Llistes de professors al sistema sgd
        {
            String code = StringUtils.AfterFirst(selected, "[");
            code = StringUtils.BeforeFirst(code, "]");
            int icode = Integer.parseInt(code);
            MensajesListas pointer = null;
            
            for(MensajesListas b: Cfg.mensajesListas)
            {
                if(icode==b.getId())
                {
                    pointer = b;
                    break;
                }
            }
            
            if(pointer!=null)
            {
                //M'he d'assegurar que inclogo l'autor de l'anunci a la llista sino 
                //li desapareixera i no el podra editar
                para += cfg.getCoreCfg().getUserInfo().getAbrev()+";";
                for(MensajesListasProfesores mlp: pointer.getListMensajesListasProfesores())
                {
                    String idProfe = mlp.getCodigo(); //is idProfesores
                    String abrev = cfg.getCoreCfg().getSgdClient().getProfesoresCollection().getAbrev(idProfe);
                    para +=abrev+";";
                }
                    
            }
            else
            {
                para = "*";
            }
        }
        else    //Llista definida dins el fitxer anuncis.ini
        {
            int pos = jComboFor.getSelectedIndex();
            if(pos>0)
            {
                para += cfg.getCoreCfg().getUserInfo().getAbrev()+";";
            }
            para += cfg.glist.get(pos);
        }
        
        
        if(bean!=null && bean.getDbId()>0)
        {
            String SQL1 = "UPDATE sig_missatges SET para=?, missatge=? WHERE id="+bean.getDbId();
            int nup = cfg.getCoreCfg().getMysql().preparedUpdate(SQL1,new Object[]{para, doc2});
        }
        else
        {
            String SQL1 = "INSERT INTO sig_missatges (de, para, data, missatge, instantani)"
                    + " VALUES ('"+bean.getAbrev()+"','"+para+"',NOW(),?,'2')";
            int nup = cfg.getCoreCfg().getMysql().preparedUpdate(SQL1,new Object[]{doc2});
        }
        
         String text = fieldProfessorat.getText();
         ArrayList<String> parsedArray = StringUtils.parseStringToArray(text, ":", StringUtils.CASE_INSENSITIVE);
         
        //S'assegura si ha hagut un canvi de dia
        if(bean.getEventdate()!=null && jDateChooser1.getDate()!=null && bean.getEventdate().compareTo(jDateChooser1.getDate())!=0)
        {
            //Fa un update, modificant la data de l'event
             for(String prof: parsedArray)
             {
                 String abrev = StringUtils.AfterLast(prof,"[");
                 abrev = StringUtils.BeforeFirst(abrev,"]");
                 String sqlOldDate = new DataCtrl(bean.getEventdate()).getDataSQL();
                 String sqlDate = new DataCtrl(jDateChooser1.getDate()).getDataSQL();
             
                 String SQL1 = "UPDATE sig_signatures SET data='"+sqlDate+"' "
                     + " WHERE data='"+sqlOldDate+"' AND abrev='"+abrev+"'";
                 int nup = cfg.getCoreCfg().getMysql().executeUpdate(SQL1);
             }
            
        }
        
        
        //Desa les faltes del professorat dins la taula sig_signatures        
         Presencia pres = new Presencia(cfg.getCoreCfg().getIesClient());
         
         for(String prof: parsedArray)
         {
             String abrev = StringUtils.AfterLast(prof,"[");
             abrev = StringUtils.BeforeFirst(abrev,"]");
              
             
             //Primer comprova si esta creada la signatura del dia i sino la crea (a ?)
             pres.writeHorariIfNotExists(abrev, jDateChooser1.getDate(), 0);
             
             
             //Canvia les hores a sortida
             for(int k=jComboBoxDesde.getSelectedIndex()+1; k<jComboBoxFins.getSelectedIndex()+2; k++)
             {
                DataCtrl dataCtrl = new DataCtrl(jDateChooser1.getDate());
                cfg.getCoreCfg().getIesClient().getGuardiesClient().getGuardiesCollection().
                        updateHorari(abrev, dataCtrl.getIntDia(), dataCtrl.getDataSQL(), k, 3, k>7);
                              
             }
             
         }
         
        //Torna a carregar en pantalla els anuncis
          accept = true;
          pres = null;
         this.setVisible(false);
        }

    private void formatDateEventFields() {
       
        jLabel7.setVisible(jCheckBox1.isSelected());
        jDateChooser2.setVisible(jCheckBox1.isSelected());
        if(jCheckBox1.isSelected())
        {
            jLabel6.setText("Desde");  
            jLabel7.setText("Fins");
        }
        else
        {
            jLabel6.setText("Data");
            jLabel7.setText("");
            jDateChooser2.setDate(null);
        }
    }
    

}
