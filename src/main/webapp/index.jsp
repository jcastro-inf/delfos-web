<%--
    Document   : index
    Created on : 03/11/2016, 8:44:57 AM
    Author     : jcastro
--%>
<%@page import="delfos.web.database.item.GetUsedValuesOfItemFeature"%>
<%@page import="delfos.web.database.item.GetItemsWith"%>
<%@page import="delfos.web.database.item.AddItemFeatures"%>
<%@page import="delfos.CommandLineParametersError"%>
<%@page import="delfos.main.managers.database.submanagers.DatabaseCaseUseSubManager"%>
<%@page import="delfos.dataset.basic.loader.types.DatasetLoader"%>
<%@page import="delfos.main.managers.database.DatabaseManager"%>
<%@page import="delfos.ConsoleParameters"%>
<%@page import="delfos.dataset.changeable.ChangeableDatasetLoader"%>
<%@page import="delfos.Constants"%>
<%@page import="delfos.web.database.item.AddItem"%>
<%@page import="delfos.web.database.item.GetItemDetails"%>
<%@page import="delfos.web.database.item.GetFeaturesOfAllItems"%>
<%@page import="org.netbeans.rest.application.config.ApplicationConfig"%>
<%@page import="delfos.web.database.item.GetAllItems"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>delfos-web says: Hello world!</h1>
        This is the delfos-web landing page.

        <%

            Constants.setExitOnFail(false);

            boolean isWritable;
            DatasetLoader datasetLoader;
            try {
                ConsoleParameters consoleParameters = ConsoleParameters.parseArguments(
                        DatabaseManager.MODE_PARAMETER,
                        DatabaseManager.MANAGE_RATING_DATABASE_CONFIG_XML, delfos.web.Configuration.DATABASE_CONFIG_FILE);

                datasetLoader = DatabaseManager.extractDatasetHandler(consoleParameters);
                isWritable = DatabaseCaseUseSubManager.isDatasetLoaderChangeable(datasetLoader);
            } catch (CommandLineParametersError ex) {
                isWritable = false;
                datasetLoader = null;
            }

        %>



        <p>This service is operating over <%=datasetLoader.getAlias()%> dataset</p>

        <table border="1" cellpadding="1">
            <thead>
                <tr>
                    <th>Method</th>
                    <th>Description</th>
                    <th>Parameters</th>
                    <th>Resource</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><%= isWritable ? "" : "<strike>"%><%=AddItem.class.getSimpleName()%><%= isWritable ? "" : "</strike>"%></td>
                    <td>Retrieves and returns all features defined for items</td>
                    <td>none</td>
                    <td><%= isWritable ? "" : "<strike>"%><a href="./webresources/Database/AddItem/1">link</a><%= isWritable ? "" : "</strike>"%></td>
                </tr>
                <tr>
                    <td><%= isWritable ? "" : "<strike>"%><%=AddItemFeatures.class.getSimpleName()%><%= isWritable ? "" : "</strike>"%></td>
                    <td>Adds the specified features to the item</td>
                    <td>id, features</td>
                    <td><%= isWritable ? "" : "<strike>"%><a href="./webresources/Database/AddItemFeatures/1/name=Toy%20Story%20(1995),year=1995">link</a><%= isWritable ? "" : "</strike>"%></td>
                </tr>
                <tr>
                    <td><%=GetAllItems.class.getSimpleName()%></td>
                    <td>Returns all items in the database (id,name)</td>
                    <td>none</td>
                    <td> <a href="./webresources/Database/GetAllItems">link</a></td>
                </tr>
                <tr>
                    <td><%=GetFeaturesOfAllItems.class.getSimpleName()%></td>
                    <td>Returns all features defined for items</td>
                    <td>none</td>
                    <td> <a href="./webresources/Database/GetFeaturesOfAllItems">link</a></td>
                </tr>
                <tr>
                    <td><%=GetItemDetails.class.getSimpleName()%></td>
                    <td>Retrieves all details of item with the specified <b>id</b></td>
                    <td>id</td>
                    <td> <a href="./webresources/Database/GetItemDetails/1">link</a></td>
                </tr>
                <tr>
                    <td><%=GetItemsWith.class.getSimpleName()%></td>
                    <td>Returns all items with the specified parameter values</td>
                    <td>features</td>
                    <td> <a href="./webresources/Database/GetItemsWith/year=1990,Drama=1">link</a></td>
                </tr>
                <tr>
                    <td><%=GetUsedValuesOfItemFeature.class.getSimpleName()%></td>
                    <td>Retrieves and returns all items in the database (id,name)</td>
                    <td>none</td>
                    <td> <a href="./webresources/Database/GetUsedValuesOfItemFeature/year">link</a></td>
                </tr>
            </tbody>
        </table>


    </body>
</html>
