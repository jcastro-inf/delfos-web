/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.json;

import delfos.common.DateCollapse;
import delfos.rs.recommendation.DetailField;
import delfos.rs.recommendation.Recommendation;
import delfos.rs.recommendation.Recommendations;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author jcastro
 */
public class RecommendationsJson {

    public static JsonObject getJson(Recommendations recommendations) {

        JsonObjectBuilder recommendationsJson = Json.createObjectBuilder()
                .add("status", "ok");

        if (recommendations.getRecommendationComputationDetails().detailFieldSet().contains(DetailField.TimeTaken)) {
            final String timeTaken = (String) recommendations.getRecommendationComputationDetails().getDetailFieldValue(DetailField.TimeTaken);
            recommendationsJson.add("timeTaken", DateCollapse.collapse(new Long(timeTaken)));
        }

        recommendationsJson.add("target", recommendations.getTargetIdentifier());
        List<Recommendation> recommendationsList = recommendations.getRecommendations().stream().collect(Collectors.toList());

        JsonArrayBuilder recommendationsListJson = Json.createArrayBuilder();
        recommendationsList.forEach((Recommendation recommendation) -> {
            JsonObjectBuilder recommendationJson = Json.createObjectBuilder();
            recommendationJson.add(ItemJson.ID_ITEM, recommendation.getItem().getId());
            if (!Recommendation.NON_COVERAGE_FAILURES.test(recommendation)) {
                recommendationJson.add("preference", "NaN");
            } else {
                recommendationJson.add("preference", recommendation.getPreference().doubleValue());
            }
            recommendationsListJson.add(recommendationJson);
        });
        recommendationsJson.add("recommendations", recommendationsListJson);
        return recommendationsJson.build();
    }

}
