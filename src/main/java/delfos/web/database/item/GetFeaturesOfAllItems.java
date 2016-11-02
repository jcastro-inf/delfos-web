/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.item;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.FeatureJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetFeaturesOfAllItems")
@Produces(MediaType.TEXT_PLAIN)
public class GetFeaturesOfAllItems {

    @GET
    public String getAsPlain() {
        return getAsJson().toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson() {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(GetFeaturesOfAllItems.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .build();
        }

        JsonArray jsonArray = FeatureJson.getFeaturesJson(datasetLoader.getContentDataset().getFeatures());

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "Retrieve all features of all items in the database")
                .add(FeatureJson.FEATURES, jsonArray)
                .build();

    }

}
