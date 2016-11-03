/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.recommendation;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.common.Chronometer;
import delfos.common.exceptions.dataset.users.UserNotFound;
import delfos.configfile.rs.single.RecommenderSystemConfiguration;
import delfos.configfile.rs.single.RecommenderSystemConfigurationFileParser;
import delfos.dataset.basic.user.User;
import delfos.main.Main;
import delfos.main.managers.recommendation.ArgumentsRecommendation;
import delfos.main.managers.recommendation.singleuser.Recommend;
import delfos.main.managers.recommendation.singleuser.SingleUserRecommendation;
import delfos.rs.recommendation.RecommendationsToUser;
import delfos.web.DelfosWebConfiguration;
import delfos.web.json.RecommendationsJson;
import delfos.web.json.UserJson;
import javax.json.Json;
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
@Path("/Recommendation/RecommendToIndividual")
@Produces(MediaType.TEXT_PLAIN)
public class RecommendToIndividual {

    @Path("BuildModel")
    @GET
    public String buildModel_asText() throws CommandLineParametersError {
        return buildModel_asJson().toString();
    }

    @Path("BuildModel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue buildModel_asJson() throws CommandLineParametersError {
        Constants.setExitOnFail(false);

        String[] arguments = new String[]{
            SingleUserRecommendation.SINGLE_USER_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, DelfosWebConfiguration.RS_CONFIG_FILE,
            ArgumentsRecommendation.BUILD_RECOMMENDATION_MODEL,
            Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY
        };

        Chronometer chronometer = new Chronometer();
        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(arguments);
        Main.mainWithExceptions(consoleParameters);

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("timeTaken", chronometer.printTotalElapsed())
                .build();

    }

    @Path("Recommend/{" + UserJson.ID_USER + "}")
    @GET
    public String recommendToIndividual_asText(@PathParam(UserJson.ID_USER) int idUser) {
        return recommendToIndividual_asJson(idUser).toString();
    }

    @Path("Recommend/{" + UserJson.ID_USER + "}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue recommendToIndividual_asJson(@PathParam(UserJson.ID_USER) int idUser) {
        Constants.setExitOnFail(false);

        RecommenderSystemConfiguration rsc = RecommenderSystemConfigurationFileParser
                .loadConfigFile(DelfosWebConfiguration.RS_CONFIG_FILE);

        try {
            User user = rsc.datasetLoader.getUsersDataset().getUser(idUser);

            RecommendationsToUser recommendToUser = Recommend.recommendToUser(rsc, user);
            rsc.recommdendationsOutputMethod.writeRecommendations(recommendToUser);
            return RecommendationsJson.getJson(recommendToUser);
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not exists")
                    .add(UserJson.ID_USER, idUser).build();
        }

    }

}
