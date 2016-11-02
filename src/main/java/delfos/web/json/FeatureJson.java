/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

import delfos.dataset.basic.features.CollectionOfEntitiesWithFeatures;
import delfos.dataset.basic.features.EntityWithFeatures;
import delfos.dataset.basic.features.Feature;
import java.util.Arrays;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 *
 * @author jcastro
 */
public class FeatureJson {

    public static final String FEATURE = "feature";
    public static final String FEATURE_TYPE = "type";
    public static final String FEATURE_VALUE = "value";
    public static final String FEATURES = "features";
    public static final String FEATURE_NAME = "name";
    public static final String FEATURE_VALUES = "values";

    public static JsonArrayBuilder createFeaturesJson(EntityWithFeatures entityWithFeatures) {
        JsonArrayBuilder features = Json.createArrayBuilder();
        entityWithFeatures.getFeatures().stream().forEach((Feature feature) -> {
            final JsonObjectBuilder featureJson = Json.createObjectBuilder();
            featureJson.add(FeatureJson.FEATURE, feature.getName());
            Object featureValue = entityWithFeatures.getFeatureValue(feature);
            switch (feature.getType()) {
                case Numerical:
                    Number featureNumericValue = (Number) featureValue;
                    featureJson.add(FeatureJson.FEATURE_VALUE, featureNumericValue.doubleValue());
                default:
                    featureJson.add(FeatureJson.FEATURE_VALUE, featureValue.toString());
            }
            features.add(featureJson.build());
        });
        return features;
    }

    public static JsonArray getFeaturesJson(Feature[] features) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        Arrays.asList(features).stream().forEach((Feature feature) -> {
            jsonArrayBuilder.add(getFeatureJson(feature));
        });
        return jsonArrayBuilder.build();
    }

    private static JsonObject getFeatureJson(Feature feature) {
        return Json.createObjectBuilder()
                .add(FEATURE_NAME, feature.getName())
                .build();
    }

    public static JsonValue createFeatureDetailsJson(Feature feature, CollectionOfEntitiesWithFeatures<? extends EntityWithFeatures> contentDataset) {

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        final Set<Object> usedFeatureValues = contentDataset.getAllFeatureValues(feature);
        usedFeatureValues.stream()
                .map(featureValue -> featureValue.toString())
                .sorted()
                .forEach(featureValue -> jsonArrayBuilder.add(featureValue));

        return Json.createObjectBuilder()
                .add(FEATURE_NAME, feature.getName())
                .add(FEATURE_TYPE, feature.getType().name())
                .add(FEATURE_VALUES, jsonArrayBuilder.build()).build();
    }

}
