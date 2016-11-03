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
import delfos.web.Configuration;
import delfos.web.json.RatingJson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jcastro
 */
@Path("/Database/GetAllRatings")
@Produces(MediaType.TEXT_PLAIN)
public class GetAllRatings {

    public static final int LIMIT = 10000;

    @GET
    public String getAsText() throws CommandLineParametersError {
        return getAsJson().toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue getAsJson() throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML,
                Configuration.DATABASE_CONFIG_FILE);

        DatasetLoader<? extends Rating> datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        Iterator<? extends Rating> iterator = datasetLoader.getRatingsDataset().iterator();

        Collection<Rating> ratings = new ArrayList<>();

        boolean isLimitExceded;
        {
            int i = 0;
            while (iterator.hasNext() && i < LIMIT) {
                ratings.add(iterator.next());
            }
            isLimitExceded = i >= LIMIT;
        }

        JsonObjectBuilder responseJson = Json.createObjectBuilder();
        responseJson.add("status", "ok");
        if (isLimitExceded) {
            responseJson.add("info", "limited to " + LIMIT + " elements");
        }
        responseJson.add(RatingJson.RATINGS, RatingJson.getRatingsJson(ratings));

        return responseJson
                .build();
    }

}
