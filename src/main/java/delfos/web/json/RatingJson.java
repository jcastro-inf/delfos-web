/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

import delfos.dataset.basic.rating.Rating;
import java.util.Collection;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author jcastro
 */
public class RatingJson {

    public static final String RATING = "rating";
    public static final String RATINGS = "ratings";
    public static final String RATING_VALUE = "ratingValue";

    public static JsonValue getRatingsJson(Collection<? extends Rating> ratings) {

        JsonArrayBuilder ratingsArrayBuilder = Json.createArrayBuilder();

        ratings.forEach((rating) -> {
            ratingsArrayBuilder.add(getRatingJson(rating));
        });

        return ratingsArrayBuilder.build();
    }

    public static JsonObject getRatingJson(Rating rating) {
        return Json.createObjectBuilder()
                .add(UserJson.ID_USER, rating.getIdUser())
                .add(ItemJson.ID_ITEM, rating.getIdItem())
                .add(RatingJson.RATING_VALUE, rating.getRatingValue().doubleValue())
                .build();
    }

}
