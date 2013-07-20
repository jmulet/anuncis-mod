/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.anuncis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author Josep
 */
class DisplayReportDlg extends javar.JRDialog{

    public DisplayReportDlg(Object generatedReport) {
        this.setLayout(new BorderLayout());
        this.add((Component) generatedReport);
        
        this.setLocation(0,0);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width/2,screenSize.height-20);
        this.setTitle("Exportació d'anuncis");
    }
    
}
