/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web;

import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author jcastro
 */
@Path("/dummy")
@Produces("text/plain")
public class DummyMethod {

    @GET
    public String getAsPlain() {
        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("date", new Date().toString())
                .add("info", "The dummy method has been executed properly, the delfos-web RESTful API seems accessible")
                .build()
                .toString();

    }

    @GET
    @Produces("application/json")
    public JsonObject dummy() {

        return Json.createObjectBuilder()
                .add("status", "ok")
                .add("info", "The dummy method has been executed properly, the delfos-web RESTful API seems accessible")
                .build();

    }

}
