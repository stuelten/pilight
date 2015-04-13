/*
 * Copyright (c) 2015 Timo Stülten <timo@stuelten.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ckc.agwa.pilight.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLKonfigurationsfensterController implements Initializable {

    public static final String KEY_SSID = "ssid:";
    public static final String KEY_PW = "pw:";
    @FXML
    public PasswordField wlanPasswd;
    @FXML
    private Text actiontarget;
    @FXML
    private TextField wlanName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actiontarget.setText("Lese Zugangsdaten aus " + Konfigurationsfenster.FILENAME.getAbsolutePath());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Konfigurationsfenster.FILENAME));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().startsWith(KEY_SSID)) {
                    String wlan = line.substring(KEY_SSID.length());
                    wlanName.setText(wlan);
                } else if (line.trim().startsWith(KEY_PW)) {
                    String pw = line.substring(KEY_PW.length());
                    wlanPasswd.setText(pw);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLKonfigurationsfensterController.class.getName()).log(Level.INFO, null, ex);
        }

        actiontarget.setText("Speichern in: " + Konfigurationsfenster.FILENAME.getAbsolutePath());
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        actiontarget.setText("Speichere Zugangsdaten ...");
        try {
            actiontarget.setText("Speichere Zugangsdaten in " + Konfigurationsfenster.FILENAME.getAbsolutePath());

            BufferedWriter writer = new BufferedWriter(new FileWriter(Konfigurationsfenster.FILENAME));
            writer.write(KEY_SSID + wlanName.getText() + "\n");
            writer.write(KEY_PW + wlanPasswd.getText() + "\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLKonfigurationsfensterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        actiontarget.setText("Zugangsdaten gespeichert.");
    }

    @FXML
    private void handleAbbrechenButtonAction(ActionEvent event) {
        actiontarget.setText("Abbrechen Button gedrückt");
        System.exit(0);
    }

}
