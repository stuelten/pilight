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

import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.Light;
import de.ckc.agwa.pilight.services.client.PiLightServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controls the ui and starts any actions.
 */
public class PiLightConfigUIController implements Initializable {
    public static final Logger LOGGER = Logger.getLogger(PiLightConfigUIController.class.getName());

    /**
     * Config key for SSID.
     */
    public static final String KEY_SSID = "SSID";
    /**
     * Config key for SSID.
     */
    public static final String KEY_PASSWD = "PASSWD";
    /**
     * Config key for SSID.
     */
    public static final String KEY_SERVER = "SERVER";
    /**
     * Config key for SSID.
     */
    public static final String KEY_FAMILY = "FAMILY";
    /**
     * Config key for SSID.
     */
    public static final String KEY_LIGHT = "LIGHT";


    public static final String TEMPLATE_VALUE = "#{0}#";
    /**
     * Wifi password
     */
    @FXML
    public PasswordField passwd;
    /**
     * The server to communicate with
     */
    @FXML
    public TextField server;
    /**
     * The name of the family to use
     */
    @FXML
    public ComboBox<String> family;
    /**
     * The name of the light
     */
    @FXML
    public ComboBox<String> light;
    /**
     * Wifi name a.k.a. SSID
     */
    @FXML
    private TextField ssid;
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
                PiLightConfigMain.CONFIG_FILE.getAbsolutePath());
        statusText.setText(statusI18n);

        // FIXME: I/O should be done in a background thread.
        readConfig();

        // set status text, what happens next
        statusI18n = MessageFormat.format(
                i18n.getString("status.willSaveConfigInFile.File"),
                PiLightConfigMain.CONFIG_FILE.getAbsolutePath());
        statusText.setText(statusI18n);
    }

    @FXML
    protected void handleSaveConfig(ActionEvent event) {
        statusText.setText(i18n.getString("status.saveConfig"));
        String statusI18n = MessageFormat.format(
                i18n.getString("status.saveConfigInFile.File"),
                PiLightConfigMain.CONFIG_FILE.getAbsolutePath());
        statusText.setText(statusI18n);

        // FIXME: I/O should be done in a background thread.
        saveConfig();

        statusI18n = MessageFormat.format(
                i18n.getString("status.configSaved"),
                PiLightConfigMain.CONFIG_FILE.getAbsolutePath());
        statusText.setText(statusI18n);
    }

    @FXML
    protected void handleCancel(ActionEvent event) {
        statusText.setText(i18n.getString("status.cancelled"));
        System.exit(0);
    }

    /**
     * Callback method when server changes.
     * Tries to connect to the server and requests the family names.
     *
     * @param event the event
     */
    @FXML
    // FIXME: I/O should be done in a background thread.
    protected void handleServerChanged(ActionEvent event) {
        String serverURL = server.getText();

        String statusI18n = MessageFormat.format(i18n.getString("status.server.connect"), serverURL);
        statusText.setText(statusI18n);

        PiLightServiceClient serviceClient = new PiLightServiceClient(serverURL);
        String serverStatus = serviceClient.serviceStatusPlain();

        statusI18n = MessageFormat.format(this.i18n.getString("status.server.status"), serverStatus);
        statusText.setText(statusI18n);

        String[] familyNames = serviceClient.serviceKnownFamilyNames();
        if (null != familyNames) {
            ObservableList<String> familyItems = FXCollections.observableArrayList(familyNames);
            family.setItems(familyItems);
            family.setValue(null);
        }

        light.setValue(null);
        if (null == serverStatus || "".equals(serverStatus) || null == familyNames) {
            statusI18n = MessageFormat.format(i18n.getString("status.server.error"), serverStatus);
            statusText.setText(statusI18n);
        } else {
            statusI18n = MessageFormat.format(i18n.getString("status.server.connected"), serverStatus);
            statusText.setText(statusI18n);
        }
    }

    /**
     * Callback method when family changes.
     * Tries to connect to the server and requests the family names.
     *
     * @param event the event
     */
    @FXML
    // FIXME: I/O should be done in a background thread.
    protected void handleFamilyChanged(ActionEvent event) {
        String familyName = family.getValue();
        String serverURL = server.getText();

        // Status
        String statusI18n = MessageFormat.format(i18n.getString("status.server.family.info"), familyName, serverURL);
        statusText.setText(statusI18n);

        PiLightServiceClient serviceClient = new PiLightServiceClient(serverURL);

        if (null != familyName && !familyName.trim().isEmpty()) {
            // request lights from server
            Family familyInfo = serviceClient.serviceFamilyInfo(familyName);
            if (familyInfo != null && familyInfo.getLights() != null) {
                // Transform array of lights to sorted list of light's names
                List<String> lightNames = Arrays.asList(familyInfo.getLights())
                        .stream().map(Light::getName).sorted().collect(Collectors.toList());
                light.setItems(FXCollections.observableList(lightNames));
            }
        }

        light.setValue(null);
        statusI18n = MessageFormat.format(i18n.getString("status.server.connected"), serverURL);
        statusText.setText(statusI18n);
    }

    // ----------------------------------------------------------------------

    /**
     * Reads configuration from config file.
     */
    private void readConfig() {
        try (FileReader fileReader = new FileReader(PiLightConfigMain.CONFIG_FILE);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line = reader.readLine();
            while (line != null) {
                String key = new StringTokenizer(line).nextToken();
                // search for config entries, ignore other lines
                if (null != key && key.length() > 0 && !"#".equals(key)) {
                    String value = line.substring(key.length() + 1).trim();
                    switch (key) {
                        case KEY_FAMILY:
                            family.setValue(value);
                            break;
                        case KEY_LIGHT:
                            light.setValue(value);
                            break;
                        case KEY_PASSWD:
                            passwd.setText(value);
                            break;
                        case KEY_SERVER:
                            server.setText(value);
                            break;
                        case KEY_SSID:
                            ssid.setText(value);
                            break;
                        default:
                            // ignore
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, null, ex);
        }
    }

    /**
     * Writes back configuration into config file.
     */
    protected void saveConfig() {
        // URL templateUrl = getClass().getResource(PiLightConfigMain.CONFIG_FILENAME);
        // String templateUrlFile = templateUrl.getFile();
        File template = PiLightConfigMain.CONFIG_FILE;
        File target = PiLightConfigMain.CONFIG_FILE;

        String content = createNewConfigContent(template);
        writeConfig(target, content);
    }

    protected String createNewConfigContent(File template) {
        String ret;
        try (FileReader templateReader = new FileReader(template);
             BufferedReader templateBR = new BufferedReader(templateReader)) {

            String output = "";
            String line;
            do {
                // read and write line by line
                line = templateBR.readLine();
                if (line != null) {
                    String key = new StringTokenizer(line).nextToken();

                    // search for config entries, leave other lines unchanged
                    if (null != key && key.length() > 0) {
                        switch (key) {
                            case KEY_FAMILY:
                                line = key + "  " + family.getValue();
                                break;
                            case KEY_LIGHT:
                                line = key + "   " + light.getValue();
                                break;
                            case KEY_PASSWD:
                                line = key + "  " + passwd.getText();
                                break;
                            case KEY_SERVER:
                                line = key + "  " + server.getText();
                                break;
                            case KEY_SSID:
                                line = key + "    " + ssid.getText();
                                break;
                            default:
                                // other line: use it as it is
                        }
                    }
                    // collect lines
                    output += line + "\n";
                }
            } while (line != null);

            ret = output;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot read from " + template, ex);
        }

        return ret;
    }

    protected void writeConfig(File target, String content) {
        try (FileWriter fileWriter = new FileWriter(target);
             BufferedWriter writer = new BufferedWriter(fileWriter)
        ) {
            writer.write(content);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot write to " + target, ex);
        }
    }

}
