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
        resources.add(delfos.web.database.item.PrintItemSet.class);
        resources.add(delfos.web.database.ratings.AddRating.class);
        resources.add(delfos.web.database.ratings.PrintRatings.class);
        resources.add(delfos.web.database.user.AddUser.class);
        resources.add(delfos.web.database.user.AddUserFeatures.class);
        resources.add(delfos.web.database.user.PrintUserSet.class);
        resources.add(delfos.web.recommendation.RecommendNonPersonalised.class);
        resources.add(delfos.web.recommendation.RecommendToGroup.class);
        resources.add(delfos.web.recommendation.RecommendToIndividual.class);
    }

}
