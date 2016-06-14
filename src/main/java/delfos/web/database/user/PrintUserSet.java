/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.UserJson;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/PrintUserSet")
public class PrintUserSet {

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAsText() {
        List<User> userSet = getUserSet();
        JsonArray users = UserJson.getUsersArray(userSet);

        return users.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsJson() {
        Constants.setExitOnFail(false);
        List<User> userSet = getUserSet();
        JsonArray users = UserJson.getUsersArray(userSet);

        return users.toString();
    }

    public List<User> getUserSet() {
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            ChangeableDatasetLoader changeableDatasetHandler = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);

            return changeableDatasetHandler.getUsersDataset().stream().sorted().collect(Collectors.toList());
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(PrintUserSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.EMPTY_LIST;
    }
}
