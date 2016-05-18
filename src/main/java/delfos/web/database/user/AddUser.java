/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.UserJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author jcastro
 */
@Path("/Database/AddUser")
public class AddUser {

    public static final String IDUSER = "idUser";

    @Path("{idUser}")
    @GET
    @Produces("application/json")
    public JsonObject addUser(@PathParam("idUser") int idUser) {

        ChangeableDatasetLoader changeableDatasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);
            changeableDatasetLoader = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddUser.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add("idUser", idUser).build();

        }

        if (changeableDatasetLoader.getUsersDataset().allIDs().contains(idUser)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User already exists")
                    .add("idUser", idUser).build();
        } else {
            final User user = new User(idUser);
            changeableDatasetLoader.getChangeableUsersDataset().addUser(user);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("user", UserJson.createWithFeatures(user))
                    .build();
        }
    }
}
