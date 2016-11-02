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
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
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
@Path("/Database/GetItemDetails")
@Produces(MediaType.TEXT_PLAIN)
public class GetItemDetails {

    @Path("{" + ItemJson.ID_ITEM + "}")
    @GET
    public String getAsPlain(@PathParam(ItemJson.ID_ITEM) int idItem) throws CommandLineParametersError {
        return getAsJson(idItem).toString();
    }

    @Path("{" + ItemJson.ID_ITEM + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam(ItemJson.ID_ITEM) int idItem) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

        datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        try {
            Item item = datasetLoader.getContentDataset().getItem(idItem);
            return Json.createObjectBuilder()
                    .add("message", "item with id " + idItem)
                    .add(ItemJson.ITEM, ItemJson.createWithFeatures(item))
                    .build();
        } catch (ItemNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not found")
                    .build();
        }

    }
}
