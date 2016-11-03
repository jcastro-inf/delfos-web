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
import delfos.main.managers.database.DatabaseManager;
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.json.UserJson;
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
@Path("/Database/GetUserDetails")
@Produces(MediaType.TEXT_PLAIN)
public class GetUserDetails {

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    public String getAsPlain(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        return getAsJson(idUser).toString();
    }

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        try {
            User user = datasetLoader.getUsersDataset().getUser(idUser);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("message", "user with id " + idUser)
                    .add(UserJson.USER, UserJson.createWithFeatures(user))
                    .build();
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not found")
                    .build();
        }

    }
}
