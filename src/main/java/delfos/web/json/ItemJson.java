/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

import delfos.dataset.basic.features.Feature;
import delfos.dataset.basic.item.Item;
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
public class ItemJson {

    public static JsonObject create(Item item) {
        JsonObjectBuilder itemJson = Json.createObjectBuilder();
        itemJson.add("name", item.getName());
        itemJson.add("id", item.getId());

        return itemJson.build();
    }

    public static JsonObject createWithFeatures(Item item) {
        JsonObjectBuilder itemJson = Json.createObjectBuilder();
        itemJson.add("name", item.getName());
        itemJson.add("id", item.getId());

        JsonArrayBuilder features = Json.createArrayBuilder();
        item.getFeatures().stream().forEach((Feature feature) -> {
            final JsonObjectBuilder featureJson = Json.createObjectBuilder();
            featureJson.add("feature", feature.getExtendedName());
            Object featureValue = item.getFeatureValue(feature);
            switch (feature.getType()) {
                case Numerical:
                    Number featureNumericValue = (Number) featureValue;
                    featureJson.add("value", featureNumericValue.doubleValue());
                default:
                    featureJson.add("value", featureValue.toString());
            }
            features.add(featureJson.build());
        });
        itemJson.add("features", features.build());

        return itemJson.build();
    }

    public static JsonArray getItemsArray(List<Item> items) {
        JsonArrayBuilder itemsJsonArrayBuilder = Json.createArrayBuilder();
        items.forEach((Item item) -> {
            JsonObject itemJson = create(item);
            itemsJsonArrayBuilder.add(itemJson);
        });
        return itemsJsonArrayBuilder.build();
    }

}
