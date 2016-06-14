/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.item;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.common.exceptions.dataset.items.ItemNotFound;
import delfos.dataset.basic.item.Item;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.json.ItemJson;
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
@Path("/Database/AddItemFeatures")
public class AddItemFeatures {

    static {
        Constants.setExitOnFail(false);
    }
    public static final String IDITEM = AddItem.IDITEM;

    @Path("{idItem}/{featuresToAdd}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAsPlain(@PathParam("idItem") int idItem, @PathParam("featuresToAdd") String featuresToAdd) {
        return getAsJson(idItem, featuresToAdd).toString();
    }

    @Path("{idItem}/{featuresToAdd}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam("idItem") int idItem, @PathParam("featuresToAdd") String featuresToAdd) {

        JsonObject errorMessage = ParameterParser.validateFeaturesToAdd(featuresToAdd);
        if (errorMessage != null) {
            return errorMessage;
        }

        Map<String, String> featuresToAddMap = ParameterParser.extractFeatureToAdd(featuresToAdd);
        String newName = ParameterParser.extractNewName(featuresToAdd);

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

        Item item;
        try {
            item = changeableDatasetLoader.getChangeableContentDataset().getItem(idItem);
        } catch (ItemNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not exists")
                    .add(IDITEM, idItem).build();
        }

        delfos.main.managers.database.submanagers.AddItemFeatures.getInstance()
                .addItemFeatures(
                        changeableDatasetLoader, item, newName, featuresToAddMap
                );

        changeableDatasetLoader.commitChangesInPersistence();
        item = changeableDatasetLoader.getChangeableContentDataset().get(idItem);
        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("item", ItemJson.createWithFeatures(item))
                .build();

    }
}
