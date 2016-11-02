/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.database.feature;

import delfos.dataset.basic.features.EntityWithFeatures;
import delfos.dataset.basic.features.Feature;
import java.util.function.Predicate;

/**
 *
 * @author jcastro
 */
public class FilterByFeatureValue implements Predicate<EntityWithFeatures> {

    private final Feature feature;
    private final Object featureValue;

    public FilterByFeatureValue(Feature feature, Object featureValue) {
        this.feature = feature;
        this.featureValue = featureValue;
    }

    @Override
    public boolean test(EntityWithFeatures entityWithFeatures) {
        if (!entityWithFeatures.getFeatures().contains(feature)) {
            return false;
        }

        final Object featureValueOfItem = entityWithFeatures.getFeatureValue(feature);

        switch (feature.getType()) {
            case Numerical:
                double valueOfItemDouble = ((Number) featureValueOfItem).doubleValue();
                double valueSpecifiedDouble = ((Number) featureValue).doubleValue();

                boolean equals = Double.compare(valueOfItemDouble, valueSpecifiedDouble) == 0;

                if (equals) {
                    return true;
                } else {
                    return false;
                }

            default:

                return featureValueOfItem.equals(featureValue);
        }

    }
}
