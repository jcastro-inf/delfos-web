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
import delfos.configfile.rs.single.RecommenderSystemConfiguration;
import delfos.configfile.rs.single.RecommenderSystemConfigurationFileParser;
import delfos.dataset.basic.user.User;
import delfos.main.Main;
import delfos.main.managers.recommendation.ArgumentsRecommendation;
import delfos.main.managers.recommendation.nonpersonalised.NonPersonalisedRecommendation;
import delfos.main.managers.recommendation.nonpersonalised.Recommend;
import delfos.rs.recommendation.RecommendationsToUser;
import delfos.web.DelfosWebConfiguration;
import delfos.web.json.RecommendationsJson;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
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
@Path("/Recommendation/NonPersonalised")
@Produces(MediaType.TEXT_PLAIN)
public class RecommendNonPersonalised {

    @Path("BuildModel")
    @GET
    public String buildModel_asText() throws CommandLineParametersError {
        return buildModel_asJson().toString();
    }

    @Path("BuildModel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue buildModel_asJson() throws CommandLineParametersError {
        DelfosWebConfiguration.setConfiguration();

        String[] arguments = new String[]{
            NonPersonalisedRecommendation.NON_PERSONALISED_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, DelfosWebConfiguration.NON_PERSONALISED_CONFIG_FILE,
            ArgumentsRecommendation.BUILD_RECOMMENDATION_MODEL,
            Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY
        };

        Chronometer chronometer = new Chronometer();
        ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                arguments
        );
        Main.mainWithExceptions(consoleParameters);

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("timeTaken", chronometer.printTotalElapsed())
                .build();

    }

    @Path("Recommend")
    @GET
    public String recommendToNonPersonalised_asText() {
        return recommendToNonPersonalised_asJson().toString();
    }

    @Path("Recommend")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue recommendToNonPersonalised_asJson() {
        DelfosWebConfiguration.setConfiguration();

        RecommenderSystemConfiguration rsc = RecommenderSystemConfigurationFileParser
                .loadConfigFile(DelfosWebConfiguration.NON_PERSONALISED_CONFIG_FILE);

        User user = User.ANONYMOUS_USER;

        RecommendationsToUser recommendToUser = Recommend.computeRecommendations(rsc, user);

        JsonObject recommendationsJson = RecommendationsJson.getJson(recommendToUser);
        JsonObjectBuilder responseJson = Json.createObjectBuilder().add("status", "ok");
        try {
            rsc.recommdendationsOutputMethod.writeRecommendations(recommendToUser);
        } catch (Exception ex) {
            responseJson.add("warning", "Could not write recommendations, reason: " + ex.getMessage());
        }
        responseJson.add(RecommendationsJson.RECOMMENDATION, recommendationsJson);
        return responseJson.build();

    }

    @Path("Configuration")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String configuration() throws CommandLineParametersError, FileNotFoundException, IOException {
        return DelfosWebConfiguration.printXML(DelfosWebConfiguration.NON_PERSONALISED_CONFIG_FILE);
    }
}
