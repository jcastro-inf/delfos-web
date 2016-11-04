/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.common.exceptions.dataset.users.UserNotFound;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import delfos.main.managers.database.submanagers.DatabaseCaseUseSubManager;
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.json.FeatureJson;
import delfos.web.json.UserJson;
import java.util.Map;
import javax.json.Json;
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
@Path("/Database/AddUserFeatures")
@Produces(MediaType.TEXT_PLAIN)
public class AddUserFeatures {

    @Path("{idUser}/{features}")
    @GET
    public String getAsPlain(@PathParam(UserJson.ID_USER) int idUser, @PathParam(FeatureJson.FEATURES) String features) throws CommandLineParametersError {
        return getAsJson(idUser, features).toString();
    }

    @Path("{idUser}/{features}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(UserJson.ID_USER) int idUser, @PathParam(FeatureJson.FEATURES) String features) throws CommandLineParametersError {
        DelfosWebConfiguration.setConfiguration();

        if (!ParameterParser.isFeaturesToAddWithSuffixValid(features) || !ParameterParser.isFeaturesToAddValid(features)) {
            return ParameterParser.errorJson(features);
        }

        Map<String, String> featuresMap = ParameterParser.extractFeatureToAdd(features);
        String newName = ParameterParser.extractNewName(features);

        ChangeableDatasetLoader changeableDatasetLoader;

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
        changeableDatasetLoader = DatabaseCaseUseSubManager.viewDatasetLoaderAsChangeable(datasetLoader);

        try {
            User user = changeableDatasetLoader.getChangeableUsersDataset().getUser(idUser);
            delfos.main.managers.database.submanagers.AddUserFeatures.getInstance()
                    .addUserFeatures(
                            changeableDatasetLoader, user, newName, featuresMap
                    );

            changeableDatasetLoader.commitChangesInPersistence();
            user = changeableDatasetLoader.getChangeableUsersDataset().get(idUser);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("user", UserJson.createWithFeatures(user))
                    .build();
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not found")
                    .build();
        }

    }
}
