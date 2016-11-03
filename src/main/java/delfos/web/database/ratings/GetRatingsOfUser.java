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
import delfos.web.json.UserJson;
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
@Path("/Database/GetRatingsOfUser")
@Produces(MediaType.TEXT_PLAIN)
public class GetRatingsOfUser {

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    public String getAsText(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        return getAsJson(idUser).toString();
    }

    @Path("{" + UserJson.ID_USER + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue getAsJson(@PathParam(UserJson.ID_USER) int idUser) throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML,
                Configuration.DATABASE_CONFIG_FILE);

        DatasetLoader<? extends Rating> datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        Collection<? extends Rating> ratings = datasetLoader.getRatingsDataset().getUserRatingsRated(idUser).values();

        JsonObjectBuilder responseJson = Json.createObjectBuilder();
        responseJson.add("status", "ok");
        responseJson.add("message", "Ratings of user " + idUser);
        responseJson.add(RatingJson.RATINGS, RatingJson.getRatingsJson(ratings));

        return responseJson
                .build();
    }

}
