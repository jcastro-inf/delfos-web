/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.user;

import delfos.web.json.UserJson;
import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author jcastro
 */
@Path("/Database/AddUserFeatures")
public class AddUserFeatures {

    public static final String IDUSER = "idUser";

    @GET
    @Produces("application/json")
    public JsonObject addUserFeatures(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

        if (!queryParams.containsKey(IDUSER)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not specified")
                    .build();
        }

        int idUser;
        {
            final String idUserString = queryParams.get(IDUSER).get(0);
            try {
                idUser = new Integer(idUserString);
            } catch (NumberFormatException ex) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "User id is not an integer")
                        .add(IDUSER, idUserString).build();
            }
        }

        ChangeableDatasetLoader changeableDatasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);
            changeableDatasetLoader = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddUserFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add(IDUSER, idUser).build();
        }

        String newName = extractNewName(queryParams);
        Map<String, String> featuresToAdd = extractFeatures(queryParams);

        if (!changeableDatasetLoader.getUsersDataset().allIDs().contains(idUser)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not exists")
                    .add(IDUSER, idUser).build();
        } else {

            User user = changeableDatasetLoader.getUsersDataset().get(idUser);

            delfos.main.managers.database.submanagers.AddUserFeatures.getInstance().addUserFeatures(
                    featuresToAdd, newName, changeableDatasetLoader, user
            );

            changeableDatasetLoader.commitChangesInPersistence();
            user = changeableDatasetLoader.getUsersDataset().get(idUser);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("user", UserJson.createWithFeatures(user))
                    .build();

        }
    }

    public String extractNewName(MultivaluedMap<String, String> queryParams) {
        String newName = queryParams.containsKey(DatabaseManager.ENTITY_NAME) ? queryParams.getFirst(DatabaseManager.ENTITY_NAME) : null;
        return newName;
    }

    public Map<String, String> extractFeatures(MultivaluedMap<String, String> queryParams) {

        Map<String, String> featuresToAdd = queryParams.keySet().stream()
                .filter(key -> !key.equals(DatabaseManager.ENTITY_NAME))
                .filter(key -> !key.equals(IDUSER))
                .collect(Collectors.toMap(key -> key, key -> queryParams.getFirst(key)));

        return featuresToAdd;
    }

}
