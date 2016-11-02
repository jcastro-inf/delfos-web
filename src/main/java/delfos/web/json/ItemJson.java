/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

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

    public static final String ID_ITEM = "idItem";
    public static final String ITEM_NAME = "name";
    public static final String ITEM = "item";

    public static JsonObject create(Item item) {
        JsonObjectBuilder itemJson = Json.createObjectBuilder();
        itemJson.add(ITEM_NAME, item.getName());
        itemJson.add(ID_ITEM, item.getId());

        return itemJson.build();
    }

    public static JsonObject createWithFeatures(Item item) {
        JsonObjectBuilder itemJson = Json.createObjectBuilder();
        itemJson.add(ITEM_NAME, item.getName());
        itemJson.add(ID_ITEM, item.getId());

        JsonArrayBuilder features = FeatureJson.createFeaturesJson(item);
        itemJson.add(FeatureJson.FEATURES, features.build());

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
