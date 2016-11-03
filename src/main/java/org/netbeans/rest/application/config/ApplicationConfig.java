/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author jcastro
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically populated with all resources defined in the
     * project. If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(delfos.web.DummyMethod.class);
        resources.add(delfos.web.database.item.AddItem.class);
        resources.add(delfos.web.database.item.AddItemFeatures.class);
        resources.add(delfos.web.database.item.GetAllItems.class);
        resources.add(delfos.web.database.item.GetFeaturesOfAllItems.class);
        resources.add(delfos.web.database.item.GetItemDetails.class);
        resources.add(delfos.web.database.item.GetItemsWith.class);
        resources.add(delfos.web.database.item.GetUsedValuesOfItemFeature.class);
        resources.add(delfos.web.database.ratings.AddRating.class);
        resources.add(delfos.web.database.ratings.GetAllRatings.class);
        resources.add(delfos.web.database.ratings.GetRatingsOfItem.class);
        resources.add(delfos.web.database.ratings.GetRatingsOfUser.class);
        resources.add(delfos.web.database.user.AddUser.class);
        resources.add(delfos.web.database.user.AddUserFeatures.class);
        resources.add(delfos.web.database.user.GetAllUsers.class);
        resources.add(delfos.web.database.user.GetFeaturesOfAllUsers.class);
        resources.add(delfos.web.database.user.GetUsedValuesOfUserFeature.class);
        resources.add(delfos.web.database.user.GetUserDetails.class);
        resources.add(delfos.web.database.user.GetUsersWith.class);
        resources.add(delfos.web.recommendation.RecommendNonPersonalised.class);
        resources.add(delfos.web.recommendation.RecommendToGroup.class);
        resources.add(delfos.web.recommendation.RecommendToIndividual.class);
    }

}
