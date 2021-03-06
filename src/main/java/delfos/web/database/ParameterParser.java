/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database;

import delfos.dataset.basic.features.FeatureType;
import delfos.main.managers.database.DatabaseManager;
import delfos.web.json.ItemJson;
import delfos.web.json.UserJson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author jcastro
 */
public class ParameterParser {

    public static boolean isFeaturesToAddWithSuffixValid(String features) {
        List<String> featuresToAddList = Arrays.stream(features.split(",")).collect(Collectors.toList());
        List<String> featureWithValueErrored = featuresToAddList.stream().filter((String featureWithValue) -> {
            if (!featureWithValue.contains("=")) {
                return true;
            }
            final String[] featureWithValueFields = featureWithValue.split("=");
            if (featureWithValueFields.length != 2) {
                return true;
            }
            String featureName = featureWithValueFields[0];
            String featureValue = featureWithValueFields[1];
            if (featureValue.isEmpty()) {
                return true;
            }
            if (featureName.isEmpty()) {
                return true;
            }
            if (DatabaseManager.ENTITY_NAME.equals(featureName)) {
                return false;
            }
            Optional<FeatureType> featureTypeFound = Arrays.stream(FeatureType.values()).filter((FeatureType featureType) -> featureName.endsWith(featureType.getSufix())).findFirst();
            return !featureTypeFound.isPresent();
        }).collect(Collectors.toList());

        return !featureWithValueErrored.isEmpty();
    }

    public static Map<String, String> extractFeatureToAdd(String features) {
        Map<String, String> featuresToAddMap = Arrays.stream(features.split(","))
                .filter((String featureWithValue) -> featureWithValue.contains("="))
                .filter((String featureWithValue) -> !featureWithValue.split("=")[0].equals(DatabaseManager.ENTITY_NAME))
                .filter((String featureWithValue) -> !featureWithValue.split("=")[0].equals(ItemJson.ID_ITEM))
                .filter((String featureWithValue) -> !featureWithValue.split("=")[0].equals(UserJson.ID_USER))
                .collect(Collectors.toMap(
                        (String key) -> key.split("=")[0],
                        (String key) -> key.split("=")[1])
                );
        return featuresToAddMap;
    }

    public static String extractNewName(String featuresToAdd) {
        return Arrays.stream(featuresToAdd.split(","))
                .filter(featureWithValue -> featureWithValue.startsWith(DatabaseManager.ENTITY_NAME))
                .map(featureWithValue -> featureWithValue.split("=")[1])
                .findFirst().orElse(null);
    }

    public static boolean isFeaturesToAddValid(String features) {
        List<String> featuresToAddList = Arrays.stream(features.split(",")).collect(Collectors.toList());
        List<String> featureWithValueErrored = featuresToAddList.stream().filter((String featureWithValue) -> {
            if (!featureWithValue.contains("=")) {
                return true;
            }
            final String[] featureWithValueFields = featureWithValue.split("=");
            if (featureWithValueFields.length != 2) {
                return true;
            }
            String featureName = featureWithValueFields[0];
            String featureValue = featureWithValueFields[1];
            if (featureValue.isEmpty()) {
                return true;
            }
            if (featureName.isEmpty()) {
                return true;
            }
            if (DatabaseManager.ENTITY_NAME.equals(featureName)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        return !featureWithValueErrored.isEmpty();
    }

    public static JsonObject errorJson(String features) {
        JsonObjectBuilder errorJson = Json.createObjectBuilder();
        errorJson.add("status", "error");
        errorJson.add("message", "Unexpected format of features''");
        errorJson.add("features", features);
        errorJson.add("info", "Expected format: ([name=newName,])+feature=value([,feature=value])*");
        return errorJson.build();

    }

}
