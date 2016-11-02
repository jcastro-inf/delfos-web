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
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import static delfos.web.database.user.AddUserFeatures.IDUSER;
import delfos.web.json.UserJson;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static final String IDUSER = AddUser.IDUSER;

    @Path("{idUser}/{features}")
    @GET
    public String getAsText(@PathParam("idUser") int idUser, @PathParam("features") String features) {

        return getAsJson(idUser, features).toString();
    }

    @Path("{idUser}/{features}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam("idUser") int idUser, @PathParam("features") String features) {
        Constants.setExitOnFail(false);

        if (!ParameterParser.isFeaturesToAddWithSuffixValid(features) || !ParameterParser.isFeaturesToAddValid(features)) {
            return ParameterParser.errorJson(features);
        }

        Map<String, String> featuresMap = ParameterParser.extractFeatureToAdd(features);
        String newName = ParameterParser.extractNewName(features);

        ChangeableDatasetLoader changeableDatasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
            changeableDatasetLoader = DatabaseCaseUseSubManager.viewDatasetLoaderAsChangeable(datasetLoader);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddUserFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add(IDUSER, idUser).build();
        }

        User user;
        try {
            user = changeableDatasetLoader.getChangeableUsersDataset().getUser(idUser);
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not exists")
                    .add(IDUSER, idUser).build();
        }

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

    }

}
