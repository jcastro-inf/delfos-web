/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.ratings;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.common.exceptions.dataset.items.ItemNotFound;
import delfos.common.exceptions.dataset.users.UserNotFound;
import delfos.dataset.basic.item.Item;
import delfos.dataset.basic.rating.Rating;
import delfos.dataset.basic.user.User;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.json.ItemJson;
import delfos.web.json.UserJson;
import java.util.Arrays;
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
@Path("/Database/AddRating")
public class AddRating {

    @Path("{idUser}/{idItem}/{value}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAsText(@PathParam("idUser") int idUser,
            @PathParam("idItem") int idItem,
            @PathParam("value") double value) {
        return getAsJson(idUser, idItem, value).toString();
    }

    @Path("{idUser}/{idItem}/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(
            @PathParam("idUser") int idUser,
            @PathParam("idItem") int idItem,
            @PathParam("value") double value) {

        ChangeableDatasetLoader changeableDatasetLoader;
        {
            String[] consoleParametersArray = new String[]{
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE
            };
            try {
                ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(consoleParametersArray);
                changeableDatasetLoader = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);
            } catch (CommandLineParametersError ex) {
                Logger.getLogger(AddRating.class.getName()).log(Level.SEVERE, null, ex);
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Malformed command line parameters")
                        .add("commandline", Arrays.toString(consoleParametersArray))
                        .build();
            }
        }

        try {
            User user = changeableDatasetLoader.getUsersDataset().getUser(idUser);
            Item item = changeableDatasetLoader.getContentDataset().getItem(idItem);

            Rating rating = changeableDatasetLoader.getRatingsDataset().getRating(idUser, idItem);
            if (rating != null) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Rating already exists")
                        .add("idUser", idUser)
                        .add("idItem", idItem)
                        .build();
            }

            delfos.main.managers.database.submanagers.AddRating.getInstance().addRating(changeableDatasetLoader, idUser, idItem, value);
            changeableDatasetLoader.commitChangesInPersistence();

            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("user", UserJson.createWithFeatures(user))
                    .add("item", ItemJson.createWithFeatures(item))
                    .add("ratingValue", value)
                    .build();
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not exists")
                    .add("idUser", idUser)
                    .build();
        } catch (ItemNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Item not exists")
                    .add("idItem", idItem)
                    .build();
        }
    }
}
