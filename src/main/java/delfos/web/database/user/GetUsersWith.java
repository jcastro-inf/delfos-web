/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.features.EntityWithFeatures;
import delfos.dataset.basic.features.Feature;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.dataset.basic.user.User;
import delfos.main.managers.database.DatabaseManager;
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.database.feature.FilterByFeatureValue;
import delfos.web.json.UserJson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetUsersWith")
@Produces(MediaType.TEXT_PLAIN)
public class GetUsersWith {

    @Path("{features}")
    @GET
    public String getAsPlain(@PathParam("features") String features) throws CommandLineParametersError {
        return getAsJson(features).toString();
    }

    @Path("{features}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam("features") String features) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        Stream<User> candidateUsers = datasetLoader.getUsersDataset().parallelStream();

        Map<String, String> featuresMap = ParameterParser.extractFeatureToAdd(features);

        String name = ParameterParser.extractNewName(features);
        if (name != null) {
            candidateUsers = filterContainingNameIgnoreCase(candidateUsers, name);
        }

        for (Map.Entry<String, String> entry : featuresMap.entrySet()) {

            Feature feature = Arrays.asList(datasetLoader.getContentDataset().getFeatures()).parallelStream()
                    .filter(featureInner -> featureInner.getName().equals(entry.getKey()))
                    .findFirst().orElse(null);

            Object featureValue = feature.getType().parseFeatureValue(entry.getValue());

            candidateUsers = candidateUsers.filter(new FilterByFeatureValue(feature, featureValue));
        }

        List<User> users = candidateUsers.sorted().collect(Collectors.toList());

        JsonArrayBuilder usersArrayJson = Json.createArrayBuilder();
        users.forEach(user -> usersArrayJson.add(UserJson.create(user)));

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "requested users with'" + features + "'")
                .add("query", "requested users with'" + features + "'")
                .add("numUsers", users.size())
                .add("users", usersArrayJson)
                .build();

    }

    private static <Entity extends EntityWithFeatures> Stream<Entity> filterContainingNameIgnoreCase(Stream<Entity> candidateUsers, String name) {
        return candidateUsers.filter(entity -> entity.getName().toLowerCase().contains(name.toLowerCase()));
    }
}
