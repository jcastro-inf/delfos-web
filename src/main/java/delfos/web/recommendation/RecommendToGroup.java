/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.recommendation;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.ERROR_CODES;
import delfos.common.Chronometer;
import delfos.configfile.rs.single.RecommenderSystemConfiguration;
import delfos.configfile.rs.single.RecommenderSystemConfigurationFileParser;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.dataset.basic.rating.Rating;
import delfos.group.groupsofusers.GroupOfUsers;
import delfos.group.grs.recommendations.GroupRecommendations;
import delfos.main.Main;
import delfos.main.managers.recommendation.ArgumentsRecommendation;
import delfos.main.managers.recommendation.group.GroupRecommendation;
import delfos.main.managers.recommendation.group.Recommend;
import delfos.web.Configuration;
import delfos.web.database.user.AddUserFeatures;
import delfos.web.json.RecommendationsJson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author jcastro
 */
@Path("/Recommendation/RecommendToGroup")
public class RecommendToGroup {

    @Path("BuildModel")
    @GET
    @Produces("application/json")
    public JsonValue buildModel() {

        String[] arguments = new String[]{
            GroupRecommendation.GROUP_MODE,
            ArgumentsRecommendation.BUILD_RECOMMENDATION_MODEL,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, Configuration.GRS_CONFIG_FILE};
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

    @Path("Recommend/{groupMembers}")
    @GET
    @Produces("application/json")
    public JsonValue recommendToGroup(@PathParam("groupMembers") String groupMembers) {
        String[] configuration = new String[]{
            GroupRecommendation.GROUP_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, Configuration.GRS_CONFIG_FILE,
            GroupRecommendation.TARGET_GROUP};

        String[] members = groupMembers.split(",");

        String[] arguments = Arrays.asList(configuration, members).stream()
                .flatMap(values -> Arrays.stream(values))
                .collect(Collectors.toList())
                .toArray(new String[0]);
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    arguments
            );

            String configurationFile = ArgumentsRecommendation.extractConfigurationFile(consoleParameters);
            if (!new File(configurationFile).exists()) {
                ERROR_CODES.CONFIG_FILE_NOT_EXISTS.exit(new FileNotFoundException("Configuration file '" + configurationFile + "' not found"));
            }

            RecommenderSystemConfiguration rsc = RecommenderSystemConfigurationFileParser.loadConfigFile(configurationFile);
            DatasetLoader<? extends Rating> datasetLoader = rsc.datasetLoader;
            GroupOfUsers targetGroup = Recommend.extractTargetGroup(consoleParameters, datasetLoader);
            GroupRecommendations recommendToGroup = Recommend.recommendToGroup(rsc, targetGroup);

            rsc.recommdendationsOutputMethod.writeRecommendations(recommendToGroup);
            return RecommendationsJson.getJson(recommendToGroup);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(AddUserFeatures.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters: " + Arrays.toString(arguments))
                    .build();
        }

    }

}
