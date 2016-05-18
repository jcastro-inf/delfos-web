/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

import delfos.dataset.basic.features.Feature;
import delfos.dataset.basic.user.User;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author jcastro
 */
public class UserJson {

    public static JsonObject create(User user) {
        JsonObjectBuilder userJson = Json.createObjectBuilder();
        userJson.add("name", user.getName());
        userJson.add("id", user.getId());

        return userJson.build();
    }

    public static JsonObject createWithFeatures(User user) {
        JsonObjectBuilder userJson = Json.createObjectBuilder();
        userJson.add("name", user.getName());
        userJson.add("id", user.getId());

        JsonArrayBuilder features = Json.createArrayBuilder();
        user.getFeatures().stream().forEach((Feature feature) -> {
            final JsonObjectBuilder featureJson = Json.createObjectBuilder();
            featureJson.add("feature", feature.getExtendedName());
            Object featureValue = user.getFeatureValue(feature);
            switch (feature.getType()) {
                case Numerical:
                    Number featureNumericValue = (Number) featureValue;
                    featureJson.add("value", featureNumericValue.doubleValue());
                default:
                    featureJson.add("value", featureValue.toString());
            }
            features.add(featureJson.build());
        });
        userJson.add("features", features.build());

        return userJson.build();
    }

    public static JsonArray getUsersArray(List<User> users) {
        JsonArrayBuilder usersJsonArrayBuilder = Json.createArrayBuilder();
        users.forEach((User user) -> {
            JsonObject userJson = create(user);
            usersJsonArrayBuilder.add(userJson);
        });
        return usersJsonArrayBuilder.build();
    }

}
