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
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.json.FeatureJson;
import delfos.web.json.ItemJson;
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
@Path("/Database/AddItemFeatures")
@Produces(MediaType.TEXT_PLAIN)
public class AddItemFeatures {

    @Path("{idItem}/{features}")
    @GET
    public String getAsPlain(@PathParam(ItemJson.ID_ITEM) int idItem, @PathParam(FeatureJson.FEATURES) String features) throws CommandLineParametersError {
        return getAsJson(idItem, features).toString();
    }

    @Path("{idItem}/{features}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(ItemJson.ID_ITEM) int idItem, @PathParam(FeatureJson.FEATURES) String features) throws CommandLineParametersError {
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
            Item item = changeableDatasetLoader.getChangeableContentDataset().getItem(idItem);
            delfos.main.managers.database.submanagers.AddItemFeatures.getInstance()
                    .addItemFeatures(
                            changeableDatasetLoader, item, newName, featuresMap
                    );

            changeableDatasetLoader.commitChangesInPersistence();
            item = changeableDatasetLoader.getChangeableContentDataset().get(idItem);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("item", ItemJson.createWithFeatures(item))
                    .build();
        } catch (ItemNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not found")
                    .build();
        }

    }
}
