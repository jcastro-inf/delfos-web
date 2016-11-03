/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.ratings;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.dataset.basic.rating.Rating;
import delfos.main.managers.database.DatabaseManager;
import delfos.web.DelfosWebConfiguration;
import delfos.web.json.ItemJson;
import delfos.web.json.RatingJson;
import java.util.Collection;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetRatingsOfItem")
@Produces(MediaType.TEXT_PLAIN)
public class GetRatingsOfItem {

    @Path("{" + ItemJson.ID_ITEM + "}")
    @GET
    public String getAsText(@PathParam(ItemJson.ID_ITEM) int idItem) throws CommandLineParametersError {
        return getAsJson(idItem).toString();
    }

    @Path("{" + ItemJson.ID_ITEM + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue getAsJson(@PathParam(ItemJson.ID_ITEM) int idItem) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML,
                DelfosWebConfiguration.DATABASE_CONFIG_FILE,
                Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY);

        DatasetLoader<? extends Rating> datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        Collection<? extends Rating> ratings = datasetLoader.getRatingsDataset().getItemRatingsRated(idItem).values();

        JsonObjectBuilder responseJson = Json.createObjectBuilder();
        responseJson.add("status", "ok");
        responseJson.add("message", "Ratings of item " + idItem);
        responseJson.add(RatingJson.RATINGS, RatingJson.getRatingsJson(ratings));

        return responseJson
                .build();
    }

}
