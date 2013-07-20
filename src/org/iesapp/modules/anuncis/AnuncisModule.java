/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;


import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.StatusBar;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.clients.iesdigital.anuncis.AnuncisDefinition;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.framework.pluggable.grantsystem.GrantBean;
import org.iesapp.framework.pluggable.preferences.UserPreferencesBean;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;


    
    
    
/**
 *
 * @author Josep
 */
public class AnuncisModule extends TopModuleWindow{

    protected boolean editing=false;
    public static boolean printerStarted=false;
    private int solPendents;
    private int vigents;
    public static ImageIcon backgroundImg;
    private Thread hook;
    private Dimension screenSize;
    public static String whoami;
   
    private Cfg cfg;
    private boolean listening = false;
    private boolean isLoading = false;
    private final JButton buttonNouAnunci;
    private final JButton buttonConfigure;

    /**
     * Creates new form AnuncisModule
     */
    public AnuncisModule() {
        this.moduleDescription = "Anuncis";
        this.moduleDisplayName = "Anuncis";
        this.moduleName = "anuncis";
        this.multipleInstance = false;
        initComponents();
        jLinkButton1.setIcon(new ImageIcon(AnuncisModule.class.getResource("/org/iesapp/framework/icons/down.gif")));
        
        ButtonGroup buttonGroup2 =new ButtonGroup();
        buttonGroup2.add(jRadioButtonMenuItem1);
        buttonGroup2.add(jRadioButtonMenuItem2);
        
        buttonNouAnunci = new JButton();
        buttonNouAnunci.setToolTipText("Nou anunci");
        buttonNouAnunci.setName("nouanunci");
        buttonNouAnunci.setIcon(new ImageIcon(AnuncisModule.class.getResource("/org/iesapp/modules/anuncis/icons/insert.gif")));

        buttonNouAnunci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShefEditor dlg = new ShefEditor(javar.JRDialog.getActiveFrame(), true, cfg);
                AnunciBean ba = new AnunciBean();
                ba.setAuthor(cfg.getCoreCfg().getUserInfo().getName());
                dlg.setBean(ba);
               
                screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                dlg.setSize((int) (screenSize.width*0.8), (int) (screenSize.height*0.9) );
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
                fillList();
            }
        });

          
        
        buttonConfigure = new JButton();
        buttonConfigure.setToolTipText("Preferències");
        buttonConfigure.setName("configure");
        buttonConfigure.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/modules/anuncis/icons/configIcon.gif")));
        buttonConfigure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences dlg = new Preferences(AnuncisModule.this, true, cfg);
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
            }
        });


     
        
    }
    @Override
    public void postInitialize()
    {
      //beanModule is not yet defined (we must put this after initialize)
        //////System.out.println("iniciant amb "+this.getBeanModule().getIniParameters());
        cfg = new Cfg(this.getBeanModule().getIniParameters(), coreCfg);
        //////System.out.println("DONE");
        
        DefaultComboBoxModel model1 = new DefaultComboBoxModel();
        model1.addElement("Totes les categories");
        int selection = 0;
        int i=0;
        for(AnuncisDefinition b: AnuncisDefinition.getMapDefined().values())
        {
            model1.addElement(b.getAnuncisTypeName());
            if(b.getAnuncisTypeId()==PreferencesBean.getInstance(cfg).getTypeAnuncis())
            {
                selection = i;
            }
            i += 1;
        }
        jComboBox1.setModel(model1);
        jComboBox1.setSelectedIndex(selection);    
    }
    
    
//    private JButton findLinkButtonByName(String modulename)
//    {
//        JButton button= null;
//        
//        for (Component comp : jToolbar1.getComponents()) {
//            if (comp instanceof JButton) {
//                String name = comp.getName();
//                if (name != null && name.equals(modulename)) {
//                    button = ((JButton) comp);
//                }
//            }
//        }
//        return button;
//    }
//     
    @Override
    public void refreshUI() {
        //System.out.println("on refreshUI");
        String property = coreCfg.getUserPreferences().getProperty("anuncis.showTypeAnuncis", "-1");
        int sel = Integer.parseInt(property)+1;
        System.out.println("PROPERTY IS "+property+" AND SEL IS "+sel);
        if(sel<jComboBox1.getItemCount())
        {
            jComboBox1.setSelectedIndex(sel);  //cfg.onStartLoad
        }
        
        property = coreCfg.getUserPreferences().getProperty("anuncis.showNumberAnuncis", cfg.onStartLoad);
        jComboBox2.setSelectedItem(property);
        
         //Display the correct background image
        String bckg = coreCfg.getUserPreferences().getProperty("anuncis.background", "*suro.png");
        if(bckg.startsWith("*"))
        {
            //Resource image
            setBackgroundImg( new javax.swing.ImageIcon(AnuncisModule.class.getResource("/org/iesapp/modules/anuncis/icons/"+bckg.substring(1))));
        }
        else if(bckg.equals("none"))
        {
            //No background
            setBackgroundImg(null);
        }
        else
        {
            File file = new File(CoreCfg.contextRoot+ "/applications/org-iesapp-apps-anuncis/icons/"+bckg);
            if(file.exists())
            {
                //File image (try to get it from file)
                setBackgroundImg (new ImageIcon(file.getAbsolutePath()));
            }
        }
        
       
        startUp();
        
       
    }

    @Override
    public void dispose() {
        super.dispose();
        //Get rid of the detailedAnunciPane
        if(MyAnunci.detailedAnunci!=null)
        {
            MyAnunci.detailedAnunci.setVisible(false);
            MyAnunci.detailedAnunci.dispose();
        }
    }

    
    @Override
    public void setMenus(JMenuBar jMenuBar1, JToolBar jToolbar1, StatusBar jStatusBar1) {
       
        jToolbar1.add(buttonNouAnunci);
        jToolbar1.add(buttonConfigure);
        jToolbar1.revalidate();
   
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLinkButton1 = new com.l2fprod.common.swing.JLinkButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTaskPane1 = new com.l2fprod.common.swing.JTaskPane(){

            public void scrollRectToVisible(Rectangle r)
            {
                //This is to avoid a problem with scrolling
            }

            @Override
            public void paintComponent(Graphics g) {
                if(backgroundImg!=null)
                {
                    java.awt.Dimension d = getSize();
                    for( int x = 0; x < d.width; x += backgroundImg.getIconWidth() )
                    {
                        for( int y = 0; y < d.height; y += backgroundImg.getIconHeight() )
                        {
                            g.drawImage( backgroundImg.getImage(), x, y, null, null );
                        }}
                    }
                }

            }
            ;

            jRadioButtonMenuItem3.setSelected(true);
            jRadioButtonMenuItem3.setText("Col·lapsa/Mostra tots els anuncis");
            jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButtonMenuItem3ActionPerformed(evt);
                }
            });
            jPopupMenu1.add(jRadioButtonMenuItem3);

            jMenu1.setText("Ordena per");

            jRadioButtonMenuItem1.setSelected(true);
            jRadioButtonMenuItem1.setText("Data d'event");
            jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButtonMenuItem1ActionPerformed(evt);
                }
            });
            jMenu1.add(jRadioButtonMenuItem1);

            jRadioButtonMenuItem2.setText("Data de publicació");
            jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButtonMenuItem2ActionPerformed(evt);
                }
            });
            jMenu1.add(jRadioButtonMenuItem2);

            jPopupMenu1.add(jMenu1);

            setPreferredSize(new java.awt.Dimension(300, 350));

            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Totes les categories" }));
            jComboBox1.setFocusCycleRoot(true);
            jComboBox1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });

            jLabel1.setText("  Mostra");

            jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "15", "20", "*" }));
            jComboBox2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jComboBox2ActionPerformed(evt);
                }
            });

            jLabel2.setText("  Cerca");

            jTextField1.setMaximumSize(new java.awt.Dimension(100, 24));
            jTextField1.setPreferredSize(new java.awt.Dimension(90, 24));
            jTextField1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jTextField1ActionPerformed(evt);
                }
            });
            jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    jTextField1KeyReleased(evt);
                }
            });

            jLinkButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jLinkButton1ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1, 1, 1)
                    .addComponent(jLabel1)
                    .addGap(1, 1, 1)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1, 1, 1)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addGap(0, 0, 0)
                    .addComponent(jLinkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLinkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(jLabel1))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(jLabel2))
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(1, 1, 1))
            );

            com.l2fprod.common.swing.PercentLayout percentLayout1 = new com.l2fprod.common.swing.PercentLayout();
            percentLayout1.setGap(14);
            percentLayout1.setOrientation(1);
            jTaskPane1.setLayout(percentLayout1);
            percentLayout1.setGap(1);
            jScrollPane1.setViewportView(jTaskPane1);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentContainer());
            getContentContainer().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1, 1, 1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
            );
        }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        toogle();
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        this.fillList();
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        this.fillList();
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
       this.fillList();
       jPanel1.repaint();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        fillList();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        fillList();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jLinkButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLinkButton1ActionPerformed
        Point location = jLinkButton1.getLocation();
        jPopupMenu1.show(jLinkButton1, location.x , location.y+30);
        
    }//GEN-LAST:event_jLinkButton1ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
       fillList();
    }//GEN-LAST:event_jTextField1KeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.l2fprod.common.swing.JLinkButton jLinkButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.l2fprod.common.swing.JTaskPane jTaskPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables



    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * @param editing the editing to set
     */
    public void setEditing(boolean editing) {
        this.editing = editing;
    }

   
    public boolean shouldDisplay() {
        boolean shouldDisplay = true;
        if(solPendents==0 && vigents==0)
        {
            shouldDisplay = false;
        }
        return shouldDisplay;
    }
    
    
    public void setBackgroundImg(ImageIcon icon) {
       backgroundImg = icon;
       jTaskPane1.repaint();
    }
  
    
//    public void savePreferences() {
//                     
//         ////////System.out.println("HHHHHHHHHHHHHHHHH Calling savePreferences...");
//         Dimension size = this.getSize();
//         Point p = this.getLocation();
//         
//         coreCfg.setIniProperty("anuncis.dimensions", "("+size.width+","+size.height+")");
//         coreCfg.setIniProperty("anuncis.position", "("+p.x+","+p.y+")");
//         
//         double relative_width = size.width/(1.0*screenSize.width);
//         double relative_height= size.height/(1.0*screenSize.height);
//         double relative_x= p.x/(1.0*screenSize.width);
//         double relative_y= p.y/(1.0*screenSize.height);
//         
//         coreCfg.setIniProperty("anuncis.rdimensions", "("+relative_width+","+relative_height+")");
//         coreCfg.setIniProperty("anuncis.rposition", "("+relative_x+","+relative_y+")");
//         coreCfg.saveIniProperties();
//    }
    
    public static java.util.Date parseDateFromString(String sdate)
    {
        if (sdate==null || sdate.isEmpty() || sdate.equalsIgnoreCase("null") ) {
            return null;
        }
        java.text.DateFormat formatter = null;
    
        if (sdate.contains("/")) {
            formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        } else if (sdate.contains("-")) {
            formatter = new java.text.SimpleDateFormat("dd-MM-yyyy");
        }

        java.util.Date date = null;
        if (!sdate.isEmpty()) {
            try {
                date = (java.util.Date) formatter.parse(sdate);
            } catch (ParseException ex) {
                Logger.getLogger(AnuncisModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return date;
    }
 
    
      //Carrega els anuncis
     //mode=* tots, M mes S setmana A avui
     //type=-1 tots, 1=EXTRAESCOLARS, 2=REUNIONS, 3=EVENTS, 4=NOVETATS, 5=TIC
    
     private ArrayList<AnunciBean> loadAnuncis(int type, int order)
     {
        String limit = (String) jComboBox2.getSelectedItem();
        String txt = jTextField1.getText().toUpperCase().trim();
        return cfg.getCoreCfg().getIesClient().getAnuncisClient().loadAnuncis(type, order, limit, txt);
     }

    public final void startUp()
    {   
         
        //System.out.println("on Startup");
        
        
        if(coreCfg.getUserInfo()!=null)
        {
             cfg.moduleGrant.loadGrantForRole(coreCfg.getUserInfo().getRole(), Cfg.defaultsGrant);
        }
        else
        {
              cfg.moduleGrant.loadGrantForRole("", Cfg.defaultsGrant);
        }
       
        if(coreCfg.getUserInfo()!=null)
        {
            if (cfg.allowedEditingUsers.contains(coreCfg.getUserInfo().getAbrev())
                    || cfg.fullEditingUsers.contains(coreCfg.getUserInfo().getAbrev())
                    || cfg.moduleGrant.get("edit_own_anuncis").getValue() != GrantBean.NONE
                    || cfg.moduleGrant.get("edit_all_anuncis").getValue() != GrantBean.NONE) {


                buttonNouAnunci.setEnabled(true);

            } else {

                buttonNouAnunci.setEnabled(false);

            }
        } else {

            buttonNouAnunci.setEnabled(false);

        }
        
        //////System.out.println("Grants OK");
        listening = true;
        fillList();
        //Problem fillList is done with swingworker and "vigents" may not be
        //populated yet
        String abrev = "";
        if(cfg.getCoreCfg().getUserInfo()!=null)
        {
            abrev = cfg.getCoreCfg().getUserInfo().getAbrev();
        }
        
        vigents = cfg.getCoreCfg().getIesClient().getAnuncisClient().getAnuncisVigents(abrev);
       // System.out.println("VIGENTS ->"+vigents);
        if(vigents==0)
        {
            this.setModuleStatus(TopModuleWindow.STATUS_SLEEPING);
        } 
        else
        {
            this.setModuleStatus(TopModuleWindow.STATUS_NORMAL);
        }
           
        
    }
    

    

    public final void fillList() {
        if(!listening || isLoading)
        {
            return;
        }
        //System.out.println("Called fillList");
        if(this.stray==null)
        {
            return;
        }
        jTaskPane1.removeAll();
        new AnuncisLoader().execute();     
    }
 

    private void toogle()
    {
        boolean selected = jRadioButtonMenuItem3.isSelected();
        
         
         for(Component comp: jTaskPane1.getComponents())
         {
             if(comp.getClass().equals(MyAnunci.class)){
                 MyAnunci ma = (MyAnunci) comp;
                 ma.setExpanded(selected);
             }
             
             
         }
    }
    
    
    private class AnuncisLoader extends javax.swing.SwingWorker<Void,Void>
    {

        @Override
        protected Void doInBackground() throws Exception {
            isLoading = true;
            int sel = jComboBox1.getSelectedIndex();
            int type = -1;
            if (sel > 0) {
                type = AnuncisDefinition.getMapDefined().get(sel - 1).getAnuncisTypeId();
            }

            int orderType = 1;  //0=post date, 1=first event date
            if (jRadioButtonMenuItem2.isSelected()) {
                orderType = 0;
            }
            ArrayList<AnunciBean> listAnuncis = loadAnuncis(type, orderType);
            //////System.out.println("anuncis has been loaded");

            MyAnunci first = null;
            int k = 0;
            vigents = 0;
            for (AnunciBean bean : listAnuncis) {
                //////System.out.println(this.getStray()+"STRAY");
                //////System.out.println(this.getStray().getFrame()+"FRAME");

                MyAnunci ma = new MyAnunci(bean, AnuncisModule.this.getStray().getFrame(), AnuncisModule.this, cfg);

                if (k == 0) {
                    first = ma;
                }
                jTaskPane1.add(ma);
                k += 1;
                if (ma.isExpanded()) {
                    vigents += 1;
                }
            }

            if (listAnuncis != null && listAnuncis.isEmpty()) {
                JTaskPaneGroup emptyLabel = new JTaskPaneGroup();
////////////////            emptyLabel.setCollapsable(false);
                emptyLabel.setExpanded(false);
                emptyLabel.setTitle("No s'han trobat anuncis");

                jTaskPane1.add(emptyLabel);
            }

            listAnuncis.clear();
            listAnuncis = null;
            jTaskPane1.revalidate();
            jTaskPane1.repaint();
            isLoading = false;
            return null;
        }
       
    }
    
         /**-
     * @return the icon
     */
    public static ImageIcon getIcon(String iconType, String iconName) {  
       
        ImageIcon icon = null;
        if(iconType.equals(AnunciBean.RESOURCE_ICON)) {
            icon = new ImageIcon(AnunciBean.class.getResource("/org/iesapp/modules/anuncis/icons/"+iconName));
        }
        else if(iconType.equals(AnunciBean.IMPORTED_ICON))
        {
            icon = new ImageIcon(CoreCfg.contextRoot+"/applications/org-iesapp-apps-anuncis/icons/"+iconName);
        }
        return icon;
    }

    
    //This method generates user customization for framework delegation
    @Override
    public ArrayList<UserPreferencesBean> getUserModulePreferences() {
         userModulePreferences = new ArrayList<UserPreferencesBean>();
         
         JComboBox combo = new JComboBox();
         DefaultComboBoxModel modelCombo = new DefaultComboBoxModel();
         modelCombo.addElement("[-1] Tots");
         
         HashMap<Integer, AnuncisDefinition> mapDefined = AnuncisDefinition.getMapDefined();
         for(int id: mapDefined.keySet())
         {
             modelCombo.addElement("["+id+"] "+mapDefined.get(id).getAnuncisTypeName());
         }
         
         combo.setModel(modelCombo);
         //Put selection
         String property = coreCfg.getUserPreferences().getProperty("anuncis.showTypeAnuncis", "-1");
         for(int i=0; i<modelCombo.getSize(); i++)
         {
             if( modelCombo.getElementAt(i).toString().startsWith("["+property))
             {
                 combo.setSelectedIndex(i);
                 break;
             }                 
         }
         
         combo.setActionCommand("anuncis.showTypeAnuncis");
          
         UserPreferencesBean bean = new UserPreferencesBean();
         bean.setName("Mostra tipus anuncis");
         bean.setFileKey("anuncis.showTypeAnuncis");
         bean.setComponent(combo);
         userModulePreferences.add(bean);   
         
        
        ///////////////////////////////////
         combo = new JComboBox();
         modelCombo = new DefaultComboBoxModel();
         modelCombo.addElement("none");
         modelCombo.addElement("*suro.png");
         File file = new File(CoreCfg.contextRoot+File.separator+"applications"+
                 File.separator+"org-iesapp-apps-anuncis"+File.separator+"icons");
         File[] listFiles = file.listFiles();
         for(File f: listFiles)
         {
             modelCombo.addElement(f.getName());
         }
         
         combo.setModel(modelCombo);
         //Put selection
         property = coreCfg.getUserPreferences().getProperty("anuncis.background", "*suro.png");
         for(int i=0; i<modelCombo.getSize(); i++)
         {
             if(modelCombo.getElementAt(i).toString().startsWith(property))
             {
                 combo.setSelectedIndex(i);
                 break;
             }                 
         }
         
         combo.setActionCommand("anuncis.background");
          
         bean = new UserPreferencesBean();
         bean.setName("Fons del tauler");
         bean.setFileKey("anuncis.background");
         bean.setComponent(combo);
         userModulePreferences.add(bean);  
         
         
         ///////////////////////////////////
         combo = new JComboBox();
         modelCombo = new DefaultComboBoxModel();
         modelCombo.addElement("5");
         modelCombo.addElement("10");
         modelCombo.addElement("15");
         modelCombo.addElement("20");
         modelCombo.addElement("*");
         combo.setModel(modelCombo);
         combo.setActionCommand("anuncis.showNumberAnuncis");
       
         combo.setSelectedItem(coreCfg.getUserPreferences().getProperty("anuncis.showNumberAnuncis",cfg.onStartLoad));
         
         bean = new UserPreferencesBean();
         bean.setName("Fons del tauler");
         bean.setFileKey("anuncis.showNumberAnuncis");
         bean.setComponent(combo);
         userModulePreferences.add(bean);  
                   
         return userModulePreferences;
    }
    
   

}
