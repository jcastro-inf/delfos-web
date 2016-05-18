/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web.recommendation.group;

import delfos.CommandLineParametersError;
import delfos.ConsoleParameters;
import delfos.dataset.changeable.ChangeableDatasetLoader;
import delfos.main.Main;
import delfos.main.managers.database.DatabaseManager;
import delfos.main.managers.database.submanagers.DatasetPrinterManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author jcastro
 */
@WebService(serviceName = "DatabaseManagerService")
@Stateless()
public class DatabaseManagerService {

    private static final String DATABASE_CONFIG_FILE = "/home/jcastro/db-config.xml";

    /**
     * This is a sample web service operation
     *
     * @param txt
     * @return
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Adds a user to the database
     *
     * @param user
     * @return
     */
    @WebMethod(operationName = "addUser")
    public Boolean addUser(@WebParam(name = "user") Integer user) {
        try {
            Main.mainWithExceptions(new String[]{
                DatabaseManager.MODE_PARAMETER,
                DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE,
                DatabaseManager.MANAGE_RATING_DATABASE_ADD_USER, user.toString()
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Web service operation
     *
     * @return
     */
    @WebMethod(operationName = "printUsers")
    public String printUsers() {

        try {
            ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                    DatabaseManager.MODE_PARAMETER,
                    DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, DATABASE_CONFIG_FILE);

            ChangeableDatasetLoader changeableDatasetHandler = DatabaseManager.extractChangeableDatasetHandler(consoleParameters);

            return DatasetPrinterManager.getInstance().printUserSet(changeableDatasetHandler);
        } catch (CommandLineParametersError ex) {
            Logger.getLogger(DatabaseManagerService.class.getName()).log(Level.SEVERE, null, ex);

            return ex.getMessage();
        }

    }

}
