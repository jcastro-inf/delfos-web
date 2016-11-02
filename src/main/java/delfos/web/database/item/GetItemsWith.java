/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.item;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.Constants;
import delfos.dataset.basic.features.EntityWithFeatures;
import delfos.dataset.basic.features.Feature;
import delfos.dataset.basic.item.Item;
import delfos.dataset.basic.loader.types.DatasetLoader;
import delfos.main.managers.database.DatabaseManager;
import static delfos.web.Configuration.DATABASE_CONFIG_FILE;
import delfos.web.database.ParameterParser;
import delfos.web.database.feature.FilterByFeatureValue;
import delfos.web.json.ItemJson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
@Path("/Database/GetItemsWith")
@Produces(MediaType.TEXT_PLAIN)
public class GetItemsWith {

    @GET
    public String getAsPlainNoParameters() {
        return getAsJsonNoParameters().toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJsonNoParameters() {
        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "specify a filter parameter")
                .add("info", "Expected format: (featureName=value([,featureName=value])*")
                .build();
    }

    @Path("{featuresToHave}")
    @GET
    public String getAsPlain(@PathParam("featuresToHave") String featuresToHave) {
        return getAsJson(featuresToHave).toString();
    }

    @Path("{featuresToHave}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAsJson(@PathParam("featuresToHave") String featuresToHave) {
        Constants.setExitOnFail(false);

        DatasetLoader datasetLoader;
        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);

        } catch (CommandLineParametersError ex) {
            Logger.getLogger(GetItemsWith.class.getName()).log(Level.SEVERE, null, ex);
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Malformed command line parameters")
                    .add("info", "Expected format: (featureName=value([,featureName=value])*")
                    .add("featuresToHave", featuresToHave).build();
        }

        Stream<Item> candidateItems = datasetLoader.getContentDataset().parallelStream();

        Map<String, String> featuresToHaveMap = ParameterParser.extractFeatureToAdd(featuresToHave);

        String name = ParameterParser.extractNewName(featuresToHave);
        if (name != null) {
            candidateItems = filterContainingNameIgnoreCase(candidateItems, name);
        }

        for (Map.Entry<String, String> entry : featuresToHaveMap.entrySet()) {

            Feature feature = Arrays.asList(datasetLoader.getContentDataset().getFeatures()).parallelStream()
                    .filter(featureInner -> featureInner.getName().equals(entry.getKey()))
                    .findFirst().orElse(null);

            Object featureValue = feature.getType().parseFeatureValue(entry.getValue());

            candidateItems = candidateItems.filter(new FilterByFeatureValue(feature, featureValue));
        }

        List<Item> items = candidateItems.sorted().collect(Collectors.toList());

        JsonArrayBuilder itemsArrayJson = Json.createArrayBuilder();
        items.forEach(item -> itemsArrayJson.add(ItemJson.create(item)));

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("message", "requested items with'" + featuresToHave + "'")
                .add("numItems", items.size())
                .add("items", itemsArrayJson)
                .build();

    }

    private static <Entity extends EntityWithFeatures> Stream<Entity> filterContainingNameIgnoreCase(Stream<Entity> candidateItems, String name) {
        return candidateItems.filter(entity -> entity.getName().toLowerCase().contains(name.toLowerCase()));
    }
}
