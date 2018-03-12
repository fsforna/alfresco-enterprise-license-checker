/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tai.alfresco.utils;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 *
 * @author Francesco Fornasari T.A.I Software Solution s.r.l
 */
public final class MailUtils {

    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);
    private JavaMailSender mailSender;

    /**
     * @return the mailSender
     */
    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send the fatture attive zip file to configured mail addresses
     *
     * @param bodyText
     */
    public void sendMail(final String bodyText, final String customer) throws Exception {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws MessagingException {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                    String[] addrs = SpringPropertiesUtils.getProperty("mail.addresses").split("#");
                    InternetAddress[] addresses = new InternetAddress[addrs.length];
                    for (int i = 0; i < addrs.length; i++) {
                        addresses[i] = new InternetAddress(addrs[i]);
                    }
                    helper.setTo(addresses);
                    helper.setFrom(new InternetAddress(SpringPropertiesUtils.getProperty("mail.from")));
                    helper.setSubject(SpringPropertiesUtils.getProperty("mail.subject").replace("#1", customer));

                    StringBuilder message = new StringBuilder();
                    message.append(bodyText);

                    helper.setText(message.toString());

                }
            };

            preparator.prepare(message);

            mailSender.send(preparator);



    }
}
