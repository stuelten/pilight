/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package konfigurationsfenster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 *
 * @author Yvi
 */
public class FXMLKonfigurationsfensterController implements Initializable {
    
    @FXML
    private Text actiontarget;
    
    @FXML
    private TextField wlanname;
    
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        actiontarget.setText("Speicher Button gedrückt");
        try {
            RandomAccessFile file = new RandomAccessFile("d:/JavaFX/test.txt", "rw" );
            long pos = file.length();
            file.seek(pos);
            file.writeChars(wlanname.getText());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLKonfigurationsfensterController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLKonfigurationsfensterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleAbbrechenButtonAction(ActionEvent event) {
        actiontarget.setText("Abbrechen Button gedrückt");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
