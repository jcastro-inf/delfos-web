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
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import delfos.main.managers.database.submanagers.DatabaseCaseUseSubManager;
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.json.UserJson;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/AddUser")
@Produces(MediaType.TEXT_PLAIN)
public class AddUser {

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    public String getAsText(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        return getAsJSon(idUser);
    }

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsJSon(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        DelfosWebConfiguration.setConfiguration();

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
        ChangeableDatasetLoader changeableDatasetLoader = DatabaseCaseUseSubManager.viewDatasetLoaderAsChangeable(datasetLoader);

        if (changeableDatasetLoader.getChangeableContentDataset().allIDs().contains(idUser)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User already exists")
                    .add(UserJson.ID_USER, idUser).build().toString();
        } else {
            final User user = new User(idUser);
            changeableDatasetLoader.getChangeableUsersDataset().addUser(user);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("user", UserJson.createWithFeatures(user))
                    .build().toString();

        }

    }

}
