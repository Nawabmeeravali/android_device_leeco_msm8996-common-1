package com.ota.beta.updates.tasks;

import android.util.Log;
import com.ota.beta.updates.Addon;
import com.ota.beta.updates.utils.Constants;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class AddonXmlParser extends DefaultHandler implements Constants {
    private static final String TAG = "AddonXmlParser";

    public static ArrayList<Addon> parse(File xmlFile) {
        ArrayList<Addon> addons = null;
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            AddonsSaxHandler saxHandler = new AddonsSaxHandler();
            saxParser.parse(xmlFile, saxHandler);
            addons = saxHandler.getAddons();
        } catch (Exception e) {
            Log.d(TAG, "SAXXMLParser: parse() failed");
        }
        return addons;
    }
}
