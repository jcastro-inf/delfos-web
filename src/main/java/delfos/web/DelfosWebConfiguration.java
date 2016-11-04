/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web;

import delfos.Constants;
import delfos.configuration.ConfigurationManager;
import java.io.File;

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
}
