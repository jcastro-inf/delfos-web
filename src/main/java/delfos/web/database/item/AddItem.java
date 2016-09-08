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
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import delfos.main.managers.database.submanagers.DatabaseCaseUseSubManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/AddItem")
@Produces(MediaType.TEXT_PLAIN)
public class AddItem {

    public static final String IDITEM = "idItem";

    @Path("{idItem}")
    @GET
    public String getAsText(@PathParam("idItem") int idItem) {
        return getAsJSon(idItem);
    }

    @Path("{idItem}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsJSon(@PathParam("idItem") int idItem) {
        Constants.setExitOnFail(false);

        ChangeableDatasetLoader changeableDatasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            DatasetLoader datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
            changeableDatasetLoader = DatabaseCaseUseSubManager.viewDatasetLoaderAsChangeable(datasetLoader);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddItem.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add("idItem", idItem)
                    .build().toString();

        }

        if (changeableDatasetLoader.getChangeableContentDataset().allIDs().contains(idItem)) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item already exists")
                    .add("idItem", idItem).build().toString();
        } else {
            final Item item = new Item(idItem);
            changeableDatasetLoader.getChangeableContentDataset().addItem(item);
            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("item", ItemJson.createWithFeatures(item))
                    .build().toString();

        }

    }

}
