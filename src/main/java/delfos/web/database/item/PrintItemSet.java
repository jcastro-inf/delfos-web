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
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
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
@Path("/Database/PrintItemSet")
@Produces(MediaType.TEXT_PLAIN)
public class PrintItemSet {

    @GET
    public String getAsText() {
        List<Item> itemSet = getItemSet();
        JsonArray items = ItemJson.getItemsArray(itemSet);

        return items.toString();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsJson() {
        Constants.setExitOnFail(false);

        List<Item> itemSet = getItemSet();
        JsonArray items = ItemJson.getItemsArray(itemSet);

        return items.toString();
    }

    public List<Item> getItemSet() {
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            DatasetLoader datasetHandler = DatabaseManager.extractDatasetHandler(consoleParameters);

            return datasetHandler.getContentDataset().stream().sorted().collect(Collectors.toList());
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(PrintItemSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.EMPTY_LIST;
    }
}
