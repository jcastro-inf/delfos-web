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
import delfos.web.Configuration;
import delfos.web.database.user.AddUserFeatures;
import static delfos.web.database.user.AddUserFeatures.IDUSER;
import delfos.web.json.RecommendationsJson;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class RecommendToIndividual {

    @Path("BuildModel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String buildModel_asText() {
        return buildModel_asJson().toString();
    }

    @Path("BuildModel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue buildModel_asJson() {
        Constants.setExitOnFail(false);

        String[] arguments = new String[]{
            SingleUserRecommendation.SINGLE_USER_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, Configuration.RS_CONFIG_FILE,
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

    @Path("Recommend/{idUser}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String recommendToIndividual_asText(@PathParam("idUser") int idUser) {
        return recommendToIndividual_asJson(idUser).toString();
    }

    @Path("Recommend/{idUser}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue recommendToIndividual_asJson(@PathParam("idUser") int idUser) {
        Constants.setExitOnFail(false);

        RecommenderSystemConfiguration rsc;
        try {
            rsc = RecommenderSystemConfigurationFileParser.loadConfigFile(Configuration.RS_CONFIG_FILE);
        } catch (Exception ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", ex.getMessage())
                    .add(IDUSER, idUser).build();
        }
        User user;
        try {
            user = rsc.datasetLoader.getUsersDataset().getUser(idUser);
        } catch (UserNotFound ex) {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "User not exists")
                    .add(IDUSER, idUser).build();
        }

        RecommendationsToUser recommendToUser = Recommend.recommendToUser(rsc, user);
        rsc.recommdendationsOutputMethod.writeRecommendations(recommendToUser);
        return RecommendationsJson.getJson(recommendToUser);
    }

}
