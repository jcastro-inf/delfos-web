/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.recommendation;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
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
import delfos.web.DelfosWebConfiguration;
import delfos.web.json.RecommendationsJson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.stream.Collectors;
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
@Path("/Recommendation/Group")
@Produces(MediaType.TEXT_PLAIN)
public class RecommendToGroup {

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
            GroupRecommendation.GROUP_MODE,
            ArgumentsRecommendation.BUILD_RECOMMENDATION_MODEL,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, DelfosWebConfiguration.GRS_CONFIG_FILE,
            Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY};

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

    @Path("Recommend/{groupMembers}")
    @GET
    public String recommendToGroup_asText(@PathParam("groupMembers") String groupMembers) throws CommandLineParametersError {
        return recommendToGroup_asJson(groupMembers).toString();
    }

    @Path("Recommend/{groupMembers}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonValue recommendToGroup_asJson(@PathParam("groupMembers") String groupMembers) throws CommandLineParametersError {
        DelfosWebConfiguration.setConfiguration();
        String[] configuration = new String[]{
            GroupRecommendation.GROUP_MODE,
            ArgumentsRecommendation.RECOMMENDER_SYSTEM_CONFIGURATION_FILE, DelfosWebConfiguration.GRS_CONFIG_FILE,
            GroupRecommendation.TARGET_GROUP,
            Constants.LIBRARY_CONFIGURATION_DIRECTORY, DelfosWebConfiguration.LIBRARY_CONFIGURATION_DIRECTORY};

        String[] members = groupMembers.split(",");

        String[] arguments = Arrays.asList(configuration, members).stream()
                .flatMap(values -> Arrays.stream(values))
                .collect(Collectors.toList())
                .toArray(new String[0]);

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

    }

}
