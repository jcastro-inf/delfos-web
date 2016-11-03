/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delfos.web;

/**
 *
 * @author jcastro
 */
public class Index {

    /**
     * Returns the configuration of the columns widths for the methods documentation tables.
     *
     * @return
     */
    public static final String getColWidths() {
        String ret = "<col width=\"20%\"/>\n"
                + "<col width=\"50%\"/>\n"
                + "<col width=\"10%\"/>\n"
                + "<col width=\"10%\"/>\n";
        return ret;
    }

}
