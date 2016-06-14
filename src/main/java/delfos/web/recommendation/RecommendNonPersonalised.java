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
import delfos.web.Configuration;
import delfos.web.database.user.AddUserFeatures;
import delfos.web.json.RecommendationsJson;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
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
    public String buildModel_asText() {
        return buildModel_asJson().toString();
    }

    @Path("BuildModel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue buildModel_asJson() {
        Constants.setExitOnFail(false);

        String[] arguments = new String[]{
            NonPersonalisedRecommendation.NON_PERSONALISED_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, Configuration.NON_PERSONALISED_CONFIG_FILE,
            ArgumentsRecommendation.BUILD_RECOMMENDATION_MODEL
        };
        try {

            Chronometer chronometer = new Chronometer();
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    arguments
            );
            Main.mainWithExceptions(consoleParameters);

            return Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("timeTaken", chronometer.printTotalElapsed())
                    .build();
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddUserFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters: " + Arrays.toString(arguments))
                    .build();
        }

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
        Constants.setExitOnFail(false);

        RecommenderSystemConfiguration rsc;
        try {
            rsc = RecommenderSystemConfigurationFileParser.loadConfigFile(Configuration.NON_PERSONALISED_CONFIG_FILE);
        } catch (Exception ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", ex.getMessage())
                    .build();
        }
        User user = User.ANONYMOUS_USER;

        RecommendationsToUser recommendToUser = Recommend.computeRecommendations(rsc, user);
        rsc.recommdendationsOutputMethod.writeRecommendations(recommendToUser);
        return RecommendationsJson.getJson(recommendToUser);
    }

}
