/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.clients.iesdigital.anuncis.AnuncisDefinition;
import org.iesapp.clients.iesdigital.anuncis.AnuncisParser;
import org.iesapp.framework.dialogs.ReportFactory;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class Preferences extends javax.swing.JDialog {
    private final AnuncisModule parental;
    private final Cfg cfg;
   
    /**
     * Creates new form Preferences
     */
    public Preferences(AnuncisModule parent, boolean modal, Cfg cfg) {
        super((JFrame) null, modal);
        this.cfg = cfg;
        this.parental = parent;
        initComponents();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1);
        bg.add(jRadioButton2);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cfg.getCoreCfg().anyAcademic);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        jDateChooser1.setDate(cal.getTime());
        jDateChooser2.setDate(new java.util.Date());
       
        jCheckBox1.setSelected(PreferencesBean.getInstance(cfg).isSounds());
         
        DefaultComboBoxModel model1 = new DefaultComboBoxModel();
        model1.addElement("Totes les categories");
        DefaultListModel modelList1 = new DefaultListModel();
        for(AnuncisDefinition b: AnuncisDefinition.getMapDefined().values())
        {
            model1.addElement(b.getAnuncisTypeName());
            modelList1.addElement(b.getAnuncisTypeName());
        }
        jComboBox1.setModel(model1);
        jList1.setModel(modelList1);
        jList1.setSelectionInterval(0, modelList1.getSize()-1);
    
        DefaultComboBoxModel model2 = new DefaultComboBoxModel();
        model2.addElement("none");
        model2.addElement("*suro.png");
       
        FileFilter filter = new FileFilter(){

            @Override
            public boolean accept(File file) {
                String sfile = file.getName().toLowerCase();
                boolean accepta = !file.isDirectory() && (sfile.endsWith(".gif") ||
                        sfile.endsWith(".jpg") || sfile.endsWith(".png"));
                return accepta;
            }
        };
        
        
        File f = new File(CoreCfg.contextRoot+"/applications/iesapp-anuncis/icons/");
        File[] list = f.listFiles(filter);
        if(list!=null)
        {    
            for(int i=0; i<list.length; i++)
            {
                model2.addElement(list[i].getName());
                //ImageIcon icon = new ImageIcon(list[i].getAbsolutePath());                
            }
        }
        
        jComboBox2.setModel(model2);
        jComboBox2.setSelectedItem(PreferencesBean.getInstance(cfg).getBackground());
        
        int pos =0;
        int i =0;
        for(AnuncisDefinition b: AnuncisDefinition.getMapDefined().values())
        {
            if(b.getAnuncisTypeId()==PreferencesBean.getInstance(cfg).getTypeAnuncis())
            {
                pos = i;
                break;
            }
            i +=1;
        }
        
        jComboBox1.setSelectedIndex(pos);
    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton4 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuració");

        jCheckBox1.setText("Avís sonor quan tingui noves sol·licituds d'informació");

        jButton1.setText("Cancel·la");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jButton2.setText("Restaura per defecte");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        jButton3.setText("Desa");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        jLabel1.setText("A l'inici de la sessió, mostra anuncis de la forma següent");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Fons del tauler");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(184, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jCheckBox1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(127, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Preferències", jPanel2);

        jLabel3.setText("Tipus d'anuncis");

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel4.setText("Dates de publicació");

        jLabel5.setText("Des de");

        jLabel6.setText("Fins");

        jButton4.setText("Exporta llistat");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel7.setText("Ordena per");

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Data de publicació");

        jRadioButton2.setText("Data de l'event");

        jButton5.setText("Exportació completa");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton2)
                                    .addComponent(jRadioButton1))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Exportació", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jCheckBox1.setSelected(true);
        jComboBox1.setSelectedIndex(0); 
        jComboBox2.setSelectedItem("*suro.png");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         String bckg = (String) jComboBox2.getSelectedItem();
         PreferencesBean.getInstance(cfg).setBackground(bckg);
         PreferencesBean.getInstance(cfg).setTypeAnuncis(jComboBox1.getSelectedIndex());
         PreferencesBean.getInstance(cfg).setSounds(jCheckBox1.isSelected());
         PreferencesBean.getInstance(cfg).save();
         
         if(bckg.equals("none"))
         {
             parental.setBackgroundImg(null);
         }
         else if(bckg.startsWith("*")) //as Resource
         {
             parental.setBackgroundImg(
                     new ImageIcon(Preferences.class.getResource("/org/iesapp/anuncis/icons/"+bckg.substring(1)))
                     );
         }
         else   //from external file
         {
             if(new File(CoreCfg.contextRoot+ "/applications/iesapp-anuncis/icons/"+bckg).exists())
             {
                 System.out.println(parental);
                 parental.setBackgroundImg( new ImageIcon(CoreCfg.contextRoot+
                                    "/applications/iesapp-anuncis/icons/"+bckg));
             }
        }  
    }//GEN-LAST:event_jButton3ActionPerformed

    //Llistat   Tipus | DataEvent | Titol
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
          exportAnuncis(0);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         exportAnuncis(1);
    }//GEN-LAST:event_jButton5ActionPerformed
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    private void exportAnuncis(int tipus) {
            String reportName = "misc/anuncisList";
            if(tipus==1)                
            {
                reportName = "misc/anuncisDetail";
            }
            ArrayList<AnunciBean> list = new ArrayList<AnunciBean>();
            //We must get the query
            
            String condType = " AND (";
            for(int idx: jList1.getSelectedIndices())
            {
                condType += " mis.missatge LIKE '%type={"+AnuncisDefinition.getMapDefined().get(idx).getAnuncisTypeId()+"}%' OR ";
            }
            condType = StringUtils.BeforeLast(condType, "OR")+") ";
            
            String SQL1="";
             
            String cond = "";
            String orderCond ="";
            String varName = "";
            if(jRadioButton1.isSelected()) //by postdate
            {
                varName = "mis.data";
            }
            else
            {
                varName = "STR_TO_DATE(REPLACE(LEFT(RIGHT(missatge,LENGTH(missatge)-10-LOCATE('eventDate={',missatge)),10),'-','.'), GET_FORMAT(DATE, 'EUR'))";
            }
            
            cond += " AND "+varName+">='"+new DataCtrl(jDateChooser1.getDate()).getDataSQL()+"' AND "+
                             varName+" <='"+new DataCtrl(jDateChooser2.getDate()).getDataSQL()+"'";      
            orderCond = " ORDER by "+varName+" ASC, id ASC"; //Order by post date
            
           
            //Les ordena per data de post, no per data de l'event
            SQL1 = "SELECT mis.id, mis.data, mis.missatge, mis.de as abrev FROM "
                  + " sig_missatges as mis LEFT JOIN sig_professorat as prof ON mis.de=prof.abrev "
                  + " WHERE instantani=2 "+cond+"  "+condType+" "+orderCond;
                   
        try {
            Statement st = cfg.getCoreCfg().getMysql().createStatement();
            ResultSet rs1 = cfg.getCoreCfg().getMysql().getResultSet(SQL1, st);
            while(rs1!=null && rs1.next())
            {
                String mis = rs1.getString("missatge");
                AnunciBean anunci = AnuncisParser.getBean(mis);
                list.add(anunci);
            }
            rs1.close();
            st.close();
           } catch (SQLException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
           

        final ReportFactory reportFactory = new org.iesapp.framework.dialogs.ReportFactory();
        HashMap map = new HashMap();
        map.put("periode", "De "+new DataCtrl(jDateChooser1.getDate()).getDiaMesComplet()+" a "+ new DataCtrl(jDateChooser2.getDate()).getDiaMesComplet());
        reportFactory.customReport(list, map, reportName);

        reportFactory.setReportGeneratedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object generatedReport = reportFactory.getGeneratedReport();
                DisplayReportDlg dlg = new DisplayReportDlg(generatedReport);
                dlg.setVisible(true);
            }
        });
        reportFactory.generateReport();


    }

}
