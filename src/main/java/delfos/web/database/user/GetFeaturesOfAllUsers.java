/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.FeatureJson;
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
@Path("/Database/GetFeaturesOfAllUsers")
@Produces(MediaType.TEXT_PLAIN)
public class GetFeaturesOfAllUsers {

    @GET
    public String getAsPlain() throws CommandLineParametersError {
        return getAsJson().toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson() throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

        DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        JsonArray jsonArray = FeatureJson.getFeaturesJson(datasetLoader.getUsersDataset().getFeatures());

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "Retrieve all features of all users in the database")
                .add(FeatureJson.FEATURES, jsonArray)
                .build();

    }

}
