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

public class PiLightConfigMain extends Application {

    public static final File FILENAME = new File("pilight-wlan-config.txt");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // UI laden und starten
        InputStream inputStream = getClass().getResource("WlanConfig.properties").openStream();
        ResourceBundle wlanConfigI18nResource = new PropertyResourceBundle(inputStream);
        URL wlanConfigFxml = getClass().getResource("WlanConfig.fxml");
        Parent wlanConfigParent = FXMLLoader.load(wlanConfigFxml, wlanConfigI18nResource);

        Scene scene = new Scene(wlanConfigParent, 600, 375);

        String i18nPropFile = getClass().getSimpleName() + ".properties";
        inputStream = getClass().getResource(i18nPropFile).openStream();
        ResourceBundle piLightConfigI18nResource = new PropertyResourceBundle(inputStream);
        String wlanConfigStageTitle = piLightConfigI18nResource.getString("stage.title");
        stage.setTitle(wlanConfigStageTitle);
        stage.setScene(scene);
        stage.show();
    }

}
