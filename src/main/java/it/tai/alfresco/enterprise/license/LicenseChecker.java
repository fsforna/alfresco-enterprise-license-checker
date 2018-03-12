package it.tai.alfresco.enterprise.license;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.tai.alfresco.utils.MailUtils;
import it.tai.alfresco.utils.SpringPropertiesUtils;
import it.tool.rest.alfresco.client.AlfrescoRestUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Francesco Fornasari T.A.I Software Solution s.r.l
 */
public class LicenseChecker {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseChecker.class);
    private static AlfrescoRestUtils utils;
    private static MailUtils mailUtils;

    public static void main(String arg[]) {

        ApplicationContext context = new ClassPathXmlApplicationContext("application-bean.xml");
        utils = (AlfrescoRestUtils) context.getBean("restUtils");
        mailUtils = (MailUtils) context.getBean("mailUtils");

        DB persists = DBMaker.fileDB(SpringPropertiesUtils.getProperty("license.check.map.db.path")).checksumHeaderBypass().closeOnJvmShutdown().make();

        try {
            ConcurrentMap licenseMap = persists.hashMap("LICENSECHECKER").createOrOpen();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String repoInformation = utils.getRepositoryInformation();

            final LicenseInfo licenseInfo = objectMapper.readValue(repoInformation, LicenseInfo.class);
            LOG.info(licenseInfo.toString());

            if (!"ENTERPRISE".equals(licenseInfo.getLicenseMode())) {
                LOG.warn("This tool can be used only to check Alfresco Enterprise product versions");
                System.exit(0);
            }
            if (licenseInfo.isReadOnly()) {
                LOG.warn("The Alfresco is in read only mode - You have to install the new license");
                System.exit(0);
            }

            DateTime lastUpdate = new DateTime(licenseInfo.getLastUpdate());
            DateTime licenseValidUntil = new DateTime(licenseInfo.getLicenseValidUntil());
            int days = Integer.valueOf(SpringPropertiesUtils.getProperty("license.check.warning.limit.before.expiry"));
            int diff = Days.daysBetween(licenseValidUntil, DateTime.now()).getDays();

            if (diff >= days) {
                if (licenseMap.get(licenseInfo.getLicenseHolder()) != null) {
                    LOG.warn("License for " + licenseInfo.getLicenseHolder() + " will expire in " + diff + " days");
                } else {
                    licenseMap.put(licenseInfo.getLicenseHolder(), "advised");

                    mailUtils.sendMail("Warning the alfresco license will expire at " + licenseValidUntil, licenseInfo.getLicenseHolder());

                    LOG.info("License warning sent to " + SpringPropertiesUtils.getProperty("mail.addresses"));
                }
            } else {
                licenseMap.clear();
            }

        } catch (Exception e) {
            LOG.error("Fail to get repository usage information",e);
        } finally {
            persists.close();
        }
    }

}
