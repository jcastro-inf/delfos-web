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

    public static final String RECOMMENDATION = "recommendation";
    public static final String RECOMMENDATIONS = "recommendations";

    public static final String PREFERENCE = "preference";
    public static final String TARGET = "target";
    public static final String TIME_TAKEN = "timeTaken";

    public static JsonObject getJson(Recommendations recommendations) {

        JsonObjectBuilder recommendationsJson = Json.createObjectBuilder();

        if (recommendations.getRecommendationComputationDetails().detailFieldSet().contains(DetailField.TimeTaken)) {
            final String timeTaken = (String) recommendations.getRecommendationComputationDetails().getDetailFieldValue(DetailField.TimeTaken);
            recommendationsJson.add(TIME_TAKEN, DateCollapse.collapse(new Long(timeTaken)));
        }

        recommendationsJson.add(TARGET, recommendations.getTargetIdentifier());
        List<Recommendation> recommendationsList = recommendations.getRecommendations().stream().collect(Collectors.toList());

        JsonArrayBuilder recommendationsListJson = Json.createArrayBuilder();

        recommendationsList.forEach((Recommendation recommendation) -> {
            JsonObjectBuilder recommendationJson = getJson(recommendation);
            recommendationsListJson.add(recommendationJson);
        });
        recommendationsJson.add(RECOMMENDATIONS, recommendationsListJson);
        return recommendationsJson.build();
    }

    public static JsonObjectBuilder getJson(Recommendation recommendation) {
        JsonObjectBuilder recommendationJson = Json.createObjectBuilder();
        recommendationJson.add(ItemJson.ID_ITEM, recommendation.getItem().getId());
        if (!Recommendation.NON_COVERAGE_FAILURES.test(recommendation)) {
            recommendationJson.add(PREFERENCE, "NaN");
        } else {
            recommendationJson.add(PREFERENCE, recommendation.getPreference().doubleValue());
        }
        return recommendationJson;
    }

}
