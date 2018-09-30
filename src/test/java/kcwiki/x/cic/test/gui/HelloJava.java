/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.test.gui;

import java.awt.Color;
import java.awt.Font;

import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.Window;


public class HelloJava implements Application {
    private Window window = null;

    @Override
    public void startup(Display display, Map<String, String> properties) {
        window = new Window();

        Label label = new Label();
        label.setText("Hellold!");
        label.getStyles().put("font", new Font("Arial", Font.BOLD, 24));
        label.getStyles().put("color", Color.RED);
        label.getStyles().put("horizontalAlignment",
            HorizontalAlignment.CENTER);
        label.getStyles().put("verticalAlignment",
            VerticalAlignment.CENTER);

        window.setContent(label);
        window.setTitle("Hello World!");
        window.setMaximized(true);

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }
    
    public static void main(String[] args) {
        DesktopApplicationContext.main(HelloJava.class, args); 
        System.out.println("main");
    }
}