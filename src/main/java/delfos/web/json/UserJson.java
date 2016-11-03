/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

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

    public static final String ID_USER = "idUser";
    public static final String USER_NAME = "name";
    public static String USER = "user";

    public static JsonObject create(User user) {
        JsonObjectBuilder userJson = Json.createObjectBuilder();
        userJson.add(USER_NAME, user.getName());
        userJson.add(ID_USER, user.getId());

        return userJson.build();
    }

    public static JsonObject createWithFeatures(User user) {
        JsonObjectBuilder userJson = Json.createObjectBuilder();
        userJson.add(USER_NAME, user.getName());
        userJson.add(ID_USER, user.getId());
        userJson.add(FeatureJson.FEATURES, FeatureJson.createFeaturesJson(user));

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
