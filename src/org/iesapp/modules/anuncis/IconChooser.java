/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import com.l2fprod.common.swing.JLinkButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import javax.swing.ImageIcon;
import org.iesapp.clients.iesdigital.anuncis.AnunciBean;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;
/**
 *
 * @author Josep
 */
public final class IconChooser extends javax.swing.JDialog {

    protected String iconName = null;
    protected String iconType = null;
    /**
     * Creates new form IconChooser
     */
    public IconChooser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
       
        //Show resource icons first
       /// resource icons are undefined
        for(int i=0; i<Cfg.resourceIcons.length; i++)
        {
            JLinkButton label = new JLinkButton();
            //System.out.println("/org/iesapp/modules/anuncis/icons/"+Cfg.resourceIcons[i]);
            URL resource = IconChooser.class.getResource("/org/iesapp/modules/anuncis/icons/"+Cfg.resourceIcons[i]);
            System.out.println(resource);
            ImageIcon icon = new ImageIcon( resource );
            if(icon.getIconWidth()<50) //Discard big images
            {
                label.setIcon(icon);                     
                jPanel1.add(label);
                label.setActionCommand(AnunciBean.RESOURCE_ICON+Cfg.resourceIcons[i]);
                label.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String actionCommand = e.getActionCommand();
                    setIconType(actionCommand.substring(0, 1));
                    iconName = actionCommand.substring(1, actionCommand.length());
                    IconChooser.this.setVisible(false);
                }
            });
            }
        }
        //Load icons from file
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
            JLinkButton label = new JLinkButton();
            ImageIcon icon = new ImageIcon(list[i].getAbsolutePath());
            if(icon.getIconWidth()<50) //Discard big images
            {
            label.setIcon(icon);                     
            jPanel1.add(label);
            String nomdeicona=list[i].getAbsolutePath();
            if(nomdeicona.contains("/")) {
                nomdeicona= StringUtils.AfterLast(nomdeicona, "/");
            }
            else if(nomdeicona.contains("\\")) {
                nomdeicona= StringUtils.AfterLast(nomdeicona, "\\");
            }
                
            
            label.setActionCommand(AnunciBean.IMPORTED_ICON+nomdeicona);
            label.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String actionCommand = e.getActionCommand();
                    setIconType(actionCommand.substring(0, 1));
                    iconName = actionCommand.substring(1, actionCommand.length());
                    IconChooser.this.setVisible(false);
                }
            });
        }
        }
        }
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
        jPanel1 = new javax.swing.JPanel();

        setTitle("Triau una icona");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setAutoscrolls(true);
        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
    }//GEN-LAST:event_formWindowClosing

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

 public ImageIcon getIcona(String iconName, String iconType) {  
       
        ImageIcon icon = null;
        if(iconType.equals(AnunciBean.RESOURCE_ICON)) {
            URL resource = IconChooser.class.getResource("/org/iesapp/modules/anuncis/icons/"+iconName);
            //System.out.print(resource);
            icon = new ImageIcon(resource);
        }
        else if(iconType.equals(AnunciBean.IMPORTED_ICON))
        {
            icon = new ImageIcon(CoreCfg.contextRoot+"/applications/iesapp-anuncis/icons/"+iconName);
        }
        return icon;
    }

    /**
     * @return the inconName
     */
    public String getInonName() {
        return iconName;
    }

    /**
     * @param inconName the inconName to set
     */
    public void setIconName(String inconName) {
        this.iconName = inconName;
    }

    /**
     * @return the iconType
     */
    public String getIconType() {
        return iconType;
    }

    /**
     * @param iconType the iconType to set
     */
    public void setIconType(String iconType) {
        this.iconType = iconType;
    }
}
