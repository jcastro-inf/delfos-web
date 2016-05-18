/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.item;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.dataset.basic.item.Item;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
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
@Path("/Database/AddItemFeatures")
public class AddItemFeatures {

    public static final String IDITEM = "idItem";

    @GET
    @Produces("application/json")
    public JsonObject addItemFeatures(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

        if (!queryParams.containsKey(IDITEM)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not specified")
                    .build();
        }

        int idItem;
        {
            final String idItemString = queryParams.get(IDITEM).get(0);
            try {
                idItem = new Integer(idItemString);
            } catch (NumberFormatException ex) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Item id is not an integer")
                        .add(IDITEM, idItemString).build();
            }
        }

        ChangeableDatasetLoader changeableDatasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);
            changeableDatasetLoader = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddItemFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add(IDITEM, idItem).build();
        }

        String newName = extractNewName(queryParams);
        Map<String, String> featuresToAdd = extractFeatures(queryParams);

        if (!changeableDatasetLoader.getChangeableContentDataset().allIDs().contains(idItem)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not exists")
                    .add(IDITEM, idItem).build();
        } else {

            Item item = changeableDatasetLoader.getChangeableContentDataset().get(idItem);

            delfos.main.managers.database.submanagers.AddItemFeatures.getInstance().addItemFeatures(
                    featuresToAdd, newName, changeableDatasetLoader, item
            );

            changeableDatasetLoader.commitChangesInPersistence();
            item = changeableDatasetLoader.getChangeableContentDataset().get(idItem);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("item", ItemJson.createWithFeatures(item))
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
                .filter(key -> !key.equals(IDITEM))
                .collect(Collectors.toMap(key -> key, key -> queryParams.getFirst(key)));

        return featuresToAdd;
    }

}
