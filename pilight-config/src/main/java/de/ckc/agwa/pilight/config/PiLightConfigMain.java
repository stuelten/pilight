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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Entry class for UI.
 */
public class PiLightConfigMain extends Application {
    public static final Logger LOGGER = Logger.getLogger(PiLightConfigMain.class.getName());

    /**
     * The file with the configuration.
     */
    protected static final String CONFIG_FILENAME = "pilight.config";
    protected static final File CONFIG_FILE;

    static {
        // FIXME Use proper location for file lookup
        URL templateUrl = PiLightConfigMain.class.getResource("/" + CONFIG_FILENAME);
        String templateUrlFile = templateUrl.getFile();
        CONFIG_FILE = new File(templateUrlFile);
        LOGGER.info("Use as config file: " + CONFIG_FILE);
    }

    /**
     * I18n properties file
     */
    protected static final String I18N_PROPERTIES_FILENAME = "PiLightConfigMain.properties";

    /**
     * GUI properties file
     */
    protected static final String WLAN_CONFIG_UI_PROPERTIES = "PiLightConfigUI.properties";

    /**
     * JavaFX UI
     */
    protected static final String CONFIG_UI_FXML = "PiLightConfigUI.fxml";

    // ----------------------------------------------------------------------

    /**
     * Usually not called, as JavaFX apps start via {@link #start(Stage)}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    // ----------------------------------------------------------------------

    /**
     * Get properties and create ui.
     *
     * @param stage the stage to use
     */
    @Override
    public void start(Stage stage) throws Exception {

        // get i18n properties
        InputStream wlanConfigUIPropertiesInputStream = getClass().getResource(WLAN_CONFIG_UI_PROPERTIES).openStream();
        ResourceBundle wlanConfigUII18nResource = new PropertyResourceBundle(wlanConfigUIPropertiesInputStream);
        URL wlanConfigUIFxml = getClass().getResource(CONFIG_UI_FXML);

        // create ui with i18n properties
        Parent wlanConfigUIParent = FXMLLoader.load(wlanConfigUIFxml, wlanConfigUII18nResource);

        // populate and configure ui
        InputStream piLightConfigMainPropertiesInputStream = getClass().getResource(I18N_PROPERTIES_FILENAME).openStream();
        ResourceBundle piLightConfigMainI18nResource = new PropertyResourceBundle(piLightConfigMainPropertiesInputStream);

        double sceneWidth = Double.valueOf(piLightConfigMainI18nResource.getString("scene.width"));
        double sceneHeight = Double.valueOf(piLightConfigMainI18nResource.getString("scene.height"));

        Scene scene = new Scene(wlanConfigUIParent, sceneWidth, sceneHeight);

        String wlanConfigStageTitle = piLightConfigMainI18nResource.getString("stage.title");
        stage.setTitle(wlanConfigStageTitle);
        stage.setScene(scene);
        stage.show();
    }

}
