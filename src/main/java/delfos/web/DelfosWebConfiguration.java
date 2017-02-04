/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web;

import delfos.Constants;
import delfos.ERROR_CODES;
import delfos.common.Global;
import delfos.configuration.ConfigurationManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author jcastro
 */
public class DelfosWebConfiguration {

    public static final String RS_AND_DATABASE_DIRECTORY = "/home/delfos/";
    public static final String LIBRARY_CONFIGURATION_DIRECTORY = "/home/delfos/.config/delfos/";

    public static final String DATABASE_CONFIG_FILE = RS_AND_DATABASE_DIRECTORY + "db-config.xml";
    public static final String GRS_CONFIG_FILE = RS_AND_DATABASE_DIRECTORY + "grs-config.xml";
    public static final String RS_CONFIG_FILE = RS_AND_DATABASE_DIRECTORY + "rs-config.xml";
    public static final String NON_PERSONALISED_CONFIG_FILE = RS_AND_DATABASE_DIRECTORY + "non-personalised-config.xml";

    public static void setConfiguration() {

        Constants.setExitOnFail(false);
        ConfigurationManager.setConfigurationDirectory(new File(DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY));
    }

    public static String printXML(String xmlPath) {
        DelfosWebConfiguration.setConfiguration();
        File xml = new File(xmlPath);
        if (!xml.exists()) {
            ERROR_CODES.CONFIG_FILE_NOT_EXISTS.exit(new FileNotFoundException("Configuration file '" + xml.getAbsolutePath() + "' not found"));
        }
        Global.showMessageTimestamped("Loading config file " + xml.getAbsolutePath());
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(xml);
        } catch (JDOMException | IOException ex) {
            Global.showError(ex);
            ERROR_CODES.CANNOT_LOAD_CONFIG_FILE.exit(ex);
            throw new IllegalStateException(ex);
        }
        Element config = doc.getRootElement();
        config.setAttribute("configurationFilePath", xml.getAbsolutePath());
        XMLOutputter outputter = new XMLOutputter(Constants.getXMLFormat());
        return outputter.outputString(doc);
    }
}
