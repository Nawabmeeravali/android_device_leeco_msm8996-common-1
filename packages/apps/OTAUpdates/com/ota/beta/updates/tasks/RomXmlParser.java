package com.ota.beta.updates.tasks;

import android.content.Context;
import android.util.Log;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RomXmlParser extends DefaultHandler implements Constants {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    boolean tagAddonUrl = false;
    boolean tagAddonsCount = false;
    boolean tagAndroid = false;
    boolean tagBitCoinUrl = false;
    boolean tagDeveloper = false;
    boolean tagDirectUrl = false;
    boolean tagDonateUrl = false;
    boolean tagFileSize = false;
    boolean tagHttpUrl = false;
    boolean tagLog = false;
    boolean tagMD5 = false;
    boolean tagRomName = false;
    boolean tagVersionName = false;
    boolean tagVersionNumber = false;
    boolean tagWebsite = false;
    private StringBuffer value = new StringBuffer();

    public void parse(File xmlFile, Context context) throws IOException {
        this.mContext = context;
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(xmlFile, this);
            Utils.setUpdateAvailability(context);
        } catch (ParserConfigurationException ex) {
            Log.e(this.TAG, "", ex);
        } catch (SAXException ex2) {
            Log.e(this.TAG, "", ex2);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.value.setLength(0);
        if (attributes.getLength() > 0) {
            String tag = "<" + qName;
            for (int i = 0; i < attributes.getLength(); i++) {
                tag = new StringBuilder(String.valueOf(tag)).append(" ").append(attributes.getLocalName(i)).append("=").append(attributes.getValue(i)).toString();
            }
            new StringBuilder(String.valueOf(tag)).append(">").toString();
        }
        if (qName.equalsIgnoreCase("romname")) {
            this.tagRomName = true;
        }
        if (qName.equalsIgnoreCase("versionname")) {
            this.tagVersionName = true;
        }
        if (qName.equalsIgnoreCase("versionnumber")) {
            this.tagVersionNumber = true;
        }
        if (qName.equalsIgnoreCase("directurl")) {
            this.tagDirectUrl = true;
        }
        if (qName.equalsIgnoreCase("httpurl")) {
            this.tagHttpUrl = true;
        }
        if (qName.equalsIgnoreCase("android")) {
            this.tagAndroid = true;
        }
        if (qName.equalsIgnoreCase("checkmd5")) {
            this.tagMD5 = true;
        }
        if (qName.equalsIgnoreCase("filesize")) {
            this.tagFileSize = true;
        }
        if (qName.equalsIgnoreCase("developer")) {
            this.tagDeveloper = true;
        }
        if (qName.equalsIgnoreCase("websiteurl")) {
            this.tagWebsite = true;
        }
        if (qName.equalsIgnoreCase("donateurl")) {
            this.tagDonateUrl = true;
        }
        if (qName.equalsIgnoreCase("bitcoinaddress")) {
            this.tagBitCoinUrl = true;
        }
        if (qName.equalsIgnoreCase("changelog")) {
            this.tagLog = true;
        }
        if (qName.equalsIgnoreCase("addoncount")) {
            this.tagAddonsCount = true;
        }
        if (qName.equalsIgnoreCase("addonsurl")) {
            this.tagAddonUrl = true;
        }
    }

    public void characters(char[] buffer, int start, int length) throws SAXException {
        this.value.append(buffer, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        String input = this.value.toString().trim();
        if (this.tagRomName) {
            RomUpdate.setRomName(this.mContext, input);
            this.tagRomName = false;
        }
        if (this.tagVersionName) {
            RomUpdate.setVersionName(this.mContext, input);
            this.tagVersionName = false;
        }
        if (this.tagVersionNumber) {
            RomUpdate.setVersionNumber(this.mContext, Integer.parseInt(input));
            this.tagVersionNumber = false;
        }
        if (this.tagDirectUrl) {
            if (input.isEmpty()) {
                RomUpdate.setDirectUrl(this.mContext, "null");
            } else {
                RomUpdate.setDirectUrl(this.mContext, input);
                setUrlDomain(input);
            }
            RomUpdate.setDirectUrl(this.mContext, input);
            this.tagDirectUrl = false;
        }
        if (this.tagHttpUrl) {
            if (input.isEmpty()) {
                RomUpdate.setHttpUrl(this.mContext, "null");
            } else {
                RomUpdate.setHttpUrl(this.mContext, input);
                setUrlDomain(input);
            }
            this.tagHttpUrl = false;
        }
        if (this.tagAndroid) {
            RomUpdate.setAndroidVersion(this.mContext, input);
            this.tagAndroid = false;
        }
        if (this.tagMD5) {
            RomUpdate.setMd5(this.mContext, input);
            this.tagMD5 = false;
        }
        if (this.tagFileSize) {
            RomUpdate.setFileSize(this.mContext, Integer.parseInt(input));
            this.tagFileSize = false;
        }
        if (this.tagDeveloper) {
            RomUpdate.setDeveloper(this.mContext, input);
            this.tagDeveloper = false;
        }
        if (this.tagWebsite) {
            if (input.isEmpty()) {
                RomUpdate.setWebsite(this.mContext, "null");
            } else {
                RomUpdate.setWebsite(this.mContext, input);
            }
            this.tagWebsite = false;
        }
        if (this.tagDonateUrl) {
            if (input.isEmpty()) {
                RomUpdate.setDonateLink(this.mContext, "null");
            } else {
                RomUpdate.setDonateLink(this.mContext, input);
            }
            this.tagDonateUrl = false;
        }
        if (this.tagBitCoinUrl) {
            if (input.contains("bitcoin:")) {
                RomUpdate.setBitCoinLink(this.mContext, input);
            } else if (input.isEmpty()) {
                RomUpdate.setBitCoinLink(this.mContext, "null");
            } else {
                RomUpdate.setBitCoinLink(this.mContext, "bitcoin:" + input);
            }
            this.tagBitCoinUrl = false;
        }
        if (this.tagLog) {
            RomUpdate.setChangelog(this.mContext, input);
            this.tagLog = false;
        }
        if (this.tagAddonsCount) {
            RomUpdate.setAddonsCount(this.mContext, Integer.parseInt(input));
            this.tagAddonsCount = false;
        }
        if (this.tagAddonUrl) {
            RomUpdate.setAddonsUrl(this.mContext, input);
            this.tagAddonUrl = false;
        }
    }

    private void setUrlDomain(String input) {
        try {
            String domain = new URI(input).getHost();
            Context context = this.mContext;
            if (domain.startsWith("www.")) {
                domain = domain.substring(4);
            }
            RomUpdate.setUrlDomain(context, domain);
        } catch (URISyntaxException e) {
            Log.e(this.TAG, e.getMessage());
            RomUpdate.setUrlDomain(this.mContext, "Error");
        }
    }
}
