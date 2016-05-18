/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database;

import delfos.dataset.basic.features.FeatureType;
import delfos.main.managers.database.DatabaseManager;
import delfos.web.database.item.AddItemFeatures;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author jcastro
 */
public class ParameterParser {

    public static JsonObject validateFeaturesToAdd(String featuresToAdd) {
        List<String> featuresToAddList = Arrays.stream(featuresToAdd.split(",")).collect(Collectors.toList());
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
        if (featureWithValueErrored.isEmpty()) {
            return null;
        } else {
            JsonObjectBuilder errorJson = Json.createObjectBuilder();
            errorJson.add("status", "error");
            errorJson.add("message", "Cannot read features format");
            JsonArrayBuilder messages = Json.createArrayBuilder();
            featureWithValueErrored.stream().forEach((String featureWithValueError) -> {
                messages.add(featureWithValueError);
            });
            errorJson.add("fails", messages);
            errorJson.add("info", "Expected format: ([name=newName,])+featureName_typeSuffix=value([,featureName_typeSuffix=value])*");
            return errorJson.build();
        }
    }

    public static Map<String, String> extractFeatureToAdd(String featuresToAdd) {
        Map<String, String> featuresToAddMap = Arrays.stream(featuresToAdd.split(",")).filter((String featureWithValue) -> featureWithValue.contains("=")).filter((String featureWithValue) -> !featureWithValue.split("=")[0].equals(DatabaseManager.ENTITY_NAME)).filter((String featureWithValue) -> !featureWithValue.split("=")[0].equals(AddItemFeatures.IDITEM)).collect(Collectors.toMap((String key) -> key.split("=")[0], (String key) -> key.split("=")[1]));
        return featuresToAddMap;
    }

    public static String extractNewName(String featuresToAdd) {
        return Arrays.stream(featuresToAdd.split(",")).filter((String featureWithValue) -> featureWithValue.startsWith(DatabaseManager.ENTITY_NAME)).findFirst().orElse(null);
    }

}
