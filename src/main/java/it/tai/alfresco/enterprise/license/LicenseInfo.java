package it.tai.alfresco.enterprise.license;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * @author Francesco Fornasari T.A.I Software Solution s.r.l
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class LicenseInfo {
    private String licenseMode;
    private long licenseValidUntil;
    private String licenseHolder;
    private boolean readOnly;
    private long lastUpdate;


    public String getLicenseMode() {
        return licenseMode;
    }

    public long getLicenseValidUntil() {
        return licenseValidUntil;
    }

    public String getLicenseHolder() {
        return licenseHolder;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String toString() {

        return "License Info: " + licenseMode
                + "\n Valid Until " + new Date(licenseValidUntil)
                + "\n Last Update " + new Date(lastUpdate)
                + "\n Read Only " + readOnly;

    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
