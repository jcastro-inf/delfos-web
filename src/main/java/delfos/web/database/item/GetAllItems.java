/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.item;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.item.Item;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import delfos.web.DelfosWebConfiguration;
import static delfos.web.DelfosWebConfiguration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetAllItems")
@Produces(MediaType.TEXT_PLAIN)
public class GetAllItems {

    @GET
    public String getAsText() throws CommandLineParametersError {
        return getAsJson().toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson() throws CommandLineParametersError {
        DelfosWebConfiguration.setConfiguration();

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        DatasetLoader datasetHandler = DatabaseManager.extractDatasetHandler(consoleParameters);

        List<Item> itemSet = datasetHandler.getContentDataset().stream().collect(Collectors.toList());

        JsonArray itemsJsonArray = ItemJson.getItemsArray(itemSet);

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "retrieve all items")
                .add("items", itemsJsonArray)
                .build();
    }

}
