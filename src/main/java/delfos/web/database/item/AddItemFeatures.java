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
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import delfos.main.managers.database.submanagers.DatabaseCaseUseSubManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.json.FeatureJson;
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
@Produces(MediaType.TEXT_PLAIN)
public class AddItemFeatures {

    @Path("{idItem}/{featuresToAdd}")
    @GET
    public String getAsPlain(@PathParam(ItemJson.ID_ITEM) int idItem, @PathParam(FeatureJson.FEATURES) String featuresToAdd) {
        return getAsJson(idItem, featuresToAdd).toString();
    }

    @Path("{idItem}/{featuresToAdd}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(ItemJson.ID_ITEM) int idItem, @PathParam(FeatureJson.FEATURES) String featuresToAdd) {
        Constants.setExitOnFail(false);

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

            DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
            changeableDatasetLoader = DatabaseCaseUseSubManager.viewDatasetLoaderAsChangeable(datasetLoader);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddItemFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add(ItemJson.ID_ITEM, idItem).build();
        }

        Item item;
        try {
            item = changeableDatasetLoader.getChangeableContentDataset().getItem(idItem);
        } catch (ItemNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not exists")
                    .add(ItemJson.ID_ITEM, idItem).build();
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
