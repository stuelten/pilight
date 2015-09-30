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
 * Controls the ui and starts any actions.
 */
public class WlanConfigUIController implements Initializable {
    public static final Logger LOGGER = Logger.getLogger(WlanConfigUIController.class.getName());

    /**
     * key for wlan name.
     */
    public static final String KEY_SSID = "ssid:";
    /**
     * key for wlan password
     */
    public static final String KEY_PW = "pw:";

    /**
     * WLAN password
     */
    @FXML
    public PasswordField wlanPasswd;

    /**
     * WLAN name a.k.a. SSID
     */
    @FXML
    private TextField wlanName;

    /**
     * Display some status text
     */
    @FXML
    private Text statusText;

    /**
     * {@link ResourceBundle} with i18n texts.
     */
    private ResourceBundle i18n;

    @Override
    public void initialize(URL url, ResourceBundle i18nResourceBundle) {
        // set status text while reading config
        this.i18n = i18nResourceBundle;
        String statusI18n = MessageFormat.format(
                this.i18n.getString("status.readWlanInfoFromFile.File"),
                PiLightConfigMain.FILENAME.getAbsolutePath());
        statusText.setText(statusI18n);

        readConfig();

        // set status text, what happens next
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

    /**
     * Reads configuration from wlan config file.
     */
    private void readConfig() {
        try (FileReader fileReader = new FileReader(
                PiLightConfigMain.FILENAME);
             BufferedReader reader = new BufferedReader(
                     fileReader)) {
            String line = reader.readLine();
            while (line != null) {
                // search for config entries, ignore other lines
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

    /**
     * Writes back configuration into wlan config file.
     */
    private void saveConfig() {
        URL template = getClass().getResource("interfaces");
        createFileFromTemplate(template, PiLightConfigMain.FILENAME);
    }

    protected void createFileFromTemplate(URL template, File targetFile) {
        try (FileReader templateReader = new FileReader(template.getFile());
             BufferedReader templateBR = new BufferedReader(templateReader);
             FileWriter fileWriter = new FileWriter(targetFile);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            String line;
            do {
                // read and write line by line
                line = templateBR.readLine();
                if (line != null) {
                    // replace all templates with user's entries ...
                    line = line.replaceAll("#SSID#", wlanName.getText());
                    line = line.replaceAll("#PASSWD#", wlanPasswd.getText());

                    // ... and write line to target file
                    writer.write(line + "\n");
                }
            } while (line != null);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
