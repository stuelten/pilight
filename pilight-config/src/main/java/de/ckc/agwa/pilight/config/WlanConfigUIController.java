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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontrolliert das UI und stößt Aktionen an.
 */
public class WlanConfigUIController implements Initializable {

    public static final String KEY_SSID = "ssid:";
    public static final String KEY_PW = "pw:";
    public static final Logger LOGGER = Logger.getLogger(WlanConfigUIController.class.getName());
    /**
     * Das WLAN-Passwort.
     */
    @FXML
    public PasswordField wlanPasswd;
    /**
     * Der Name des WLANs.
     */
    @FXML
    private TextField wlanName;
    /**
     * Die Statusanzeige
     */
    @FXML
    private Text statusText;

    /**
     * Das {@link ResourceBundle} mit den i18n-Texten.
     */
    private ResourceBundle i18n;

    @Override
    public void initialize(URL url, ResourceBundle i18nResourceBundle) {
        this.i18n = i18nResourceBundle;
        String statusI18n = MessageFormat.format(
                this.i18n.getString("status.readWlanInfoFromFile.File"),
                PiLightConfigMain.FILENAME.getAbsolutePath());
        statusText.setText(statusI18n);

        readConfig();

        statusI18n = MessageFormat.format(
                i18n.getString("status.willSaveConfigInFile.File"),
                PiLightConfigMain.FILENAME.getAbsolutePath());
        statusText.setText(statusI18n);
    }

    @FXML
    private void handleSaveConfig(ActionEvent event) {
        statusText.setText(i18n.getString("status.saveConfig"));
        String statusI18n = MessageFormat.format(
                i18n.getString("status.saveConfigInFile.File"),
                PiLightConfigMain.FILENAME.getAbsolutePath());
        statusText.setText(statusI18n);

        saveConfig();

        statusText.setText(i18n.getString("status.configSaved"));
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        statusText.setText(i18n.getString("status.cancelled"));
        System.exit(0);
    }

    // ----------------------------------------------------------------------

    private void readConfig() {
        try (FileReader fileReader = new FileReader(
                PiLightConfigMain.FILENAME);
             BufferedReader reader = new BufferedReader(
                     fileReader);) {
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
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, null, ex);
        }
    }

    private void saveConfig() {
        URL template = getClass().getResource("interfaces");
        createFileFromTemplate(template, PiLightConfigMain.FILENAME);
    }

    protected void createFileFromTemplate(URL template, File targetFile) {
        try (FileReader templateReader = new FileReader(template.getFile());
             BufferedReader templateBR = new BufferedReader(templateReader);
             FileWriter fileWriter = new FileWriter(targetFile);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            String line = null;
            do {
                line = templateBR.readLine();
                if (line != null) {
                    // Templates mit konkreten Eingaben ersetzen
                    line = line.replaceAll("#SSID#", wlanName.getText());
                    line = line.replaceAll("#PASSWD#", wlanPasswd.getText());
                    writer.write(line + "\n");
                }
            } while (line != null);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
