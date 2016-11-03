/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.features.Feature;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.FeatureJson;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetUsedValuesOfUserFeature")
@Produces(MediaType.TEXT_PLAIN)
public class GetUsedValuesOfUserFeature {

    @Path("{" + FeatureJson.FEATURE_NAME + "}")
    @GET
    public String getAsPlain(@PathParam(FeatureJson.FEATURE_NAME) String featureName) throws CommandLineParametersError {
        return getAsJson(featureName).toString();
    }

    @Path("{" + FeatureJson.FEATURE_NAME + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(FeatureJson.FEATURE_NAME) String featureName) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

        datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        List<Feature> featuresMatched = Arrays.asList(datasetLoader.getUsersDataset().getFeatures())
                .parallelStream()
                .filter(feature -> featureName.equals(feature.getName()))
                .collect(Collectors.toList());

        if (featuresMatched.size() == 1) {

            Feature feature = featuresMatched.get(0);

            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("message", "requested feature '" + featureName + "' defined values")
                    .add(FeatureJson.FEATURE, FeatureJson.createFeatureDetailsJson(feature, datasetLoader.getUsersDataset()));

            return jsonObjectBuilder.build();

        } else if (featuresMatched.isEmpty()) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User feature with name '" + featureName + "' does not exist")
                    .add(FeatureJson.FEATURE_NAME, featureName)
                    .build();
        } else {
            List<String> featuresMatchedNames = featuresMatched.parallelStream()
                    .map(feature -> feature.getName())
                    .sorted()
                    .collect(Collectors.toList());

            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "More than one feature matched: " + featuresMatchedNames.toString())
                    .add(FeatureJson.FEATURE_NAME, featureName)
                    .build();
        }

    }
}
