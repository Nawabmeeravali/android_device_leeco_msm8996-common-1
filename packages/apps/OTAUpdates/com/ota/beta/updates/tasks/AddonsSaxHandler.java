package com.ota.beta.updates.tasks;

import com.google.android.gms.plus.PlusShare;
import com.ota.beta.updates.Addon;
import com.ota.beta.updates.utils.Constants;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AddonsSaxHandler extends DefaultHandler implements Constants {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<Addon> addons = new ArrayList();
    private Addon tempAddon;
    private String tempVal;

    public ArrayList<Addon> getAddons() {
        return this.addons;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.tempVal = "";
        if (qName.equalsIgnoreCase("addon")) {
            this.tempAddon = new Addon();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        this.tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("addon")) {
            this.addons.add(this.tempAddon);
        } else if (qName.equalsIgnoreCase("name")) {
            this.tempAddon.setTitle(this.tempVal);
        } else if (qName.equalsIgnoreCase(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION)) {
            this.tempAddon.setDesc(this.tempVal);
        } else if (qName.equalsIgnoreCase("published-at")) {
            this.tempAddon.setPublishedAt(this.tempVal.split("T")[0]);
        } else if (qName.equalsIgnoreCase("download-link")) {
            this.tempAddon.setDownloadLink(this.tempVal);
        } else if (qName.equalsIgnoreCase("size")) {
            this.tempAddon.setFilesize(Integer.parseInt(this.tempVal));
        } else if (qName.equalsIgnoreCase("id")) {
            this.tempAddon.setId(Integer.parseInt(this.tempVal));
        }
    }
}
