/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.initializer;

import java.awt.Label;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import kcwiki.x.cic.gui.HelloJava;
import kcwiki.x.cic.gui.PivotForms;
import net.miginfocom.swing.MigLayout;
import org.apache.pivot.wtk.DesktopApplicationContext;


/**
 *
 * @author x5171
 * https://www.formdev.com/jformdesigner/doc/layouts/miglayout/
 * http://www.miglayout.com/QuickStart.pdf
 * https://medium.com/prodsters/how-to-build-a-desktop-application-with-java-a34ee9c18ee3
 */
public class MainGui {
    
    public static void main(String[] args) {
        
//        DesktopApplicationContext.main(HelloJava.class, args); 

    JFrame frame = new JFrame();

    // Settings for the Frame
    frame.setSize(400, 400);
    frame.setLayout(new MigLayout(""));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Parent panel which contains the panel to be docked east
    JPanel parentPanel = new JPanel(new MigLayout("", "[grow]", "[grow]"));

    // This is the panel which is docked east, it contains the panel (bottomPanel) with all the components
    // debug outlines the component (blue) , the cell (red) and the components within it (blue)
    JPanel easternDock = new JPanel(new MigLayout("debug, insets 0", "", "push[]")); 

    // Panel that contains all the components
    JPanel bottomPanel = new JPanel(new MigLayout());


    bottomPanel.add(new JButton("Button 1"), "wrap");
    bottomPanel.add(new JButton("Button 2"), "wrap");
    bottomPanel.add(new JButton("Button 3"), "wrap");

    easternDock.add(bottomPanel, "");
    Label label1 = new Label();
        label1.setText("label1");
        Label label2 = new Label();
        label2.setText("label2");
        Label label3 = new Label();
        label3.setText("label3");
        Label label4 = new Label();
        label4.setText("label4");
        parentPanel.add(label1);
        parentPanel.add(label2);
        parentPanel.add(label3, "wrap"); // Wrap to next row
        parentPanel.add(label4);

    parentPanel.add(easternDock, "east");

    frame.add(parentPanel, "push, grow");
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
        
    }
}
