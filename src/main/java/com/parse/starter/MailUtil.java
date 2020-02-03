package com.parse.starter;

import android.os.AsyncTask;

import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil extends AsyncTask<String, Void, String> {

    private static final String SMTP_HOST_NAME = Data.adminHost;
    private static final String SMTP_AUTH_USER = Data.adminEmail;
    private static final String SMTP_AUTH_PWD = Data.adminPass;

    @Override
    protected String doInBackground(String... strings) {

        String email = strings[0];
        String subject = strings[1];
        String content = strings[2];


        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smpt");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", SMTP_HOST_NAME);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.user", SMTP_AUTH_USER);
        properties.put("mail.password", SMTP_AUTH_PWD);


        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
            }
        };
        Session session = Session.getInstance(properties, auth);

        session.setDebug(true);
        try {
            // creates a new e-mail message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(SMTP_AUTH_USER));
            InternetAddress[] toAddresses = {new InternetAddress(email)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setText(content + "\r\n\r\n" + "Greetings,\r\n Team Insta" + "\r\n" + LocalDate.now());
            msg.setSentDate(new Date());

            Transport.send(msg);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            System.out.println(mex.getMessage());
        }
        return "0";
    }
}