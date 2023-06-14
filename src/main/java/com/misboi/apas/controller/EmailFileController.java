package com.misboi.apas.controller;

import com.misboi.apas.config.EmailConfig;
import com.misboi.apas.entities.DatabaseFile;
import com.misboi.apas.repository.DatabaseFileRepository;
import com.misboi.apas.services.impl.DruAuthServiceImpl;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static com.misboi.apas.entities.AuthDetails.*;
import static java.nio.file.Paths.get;

@Service
public class EmailFileController {

    @Autowired
    private EmailConfig emailConfig;

    private String saveDirectory;

    @Autowired
    private DatabaseFileRepository dbFileRepo;

    @Autowired
    private DruAuthServiceImpl druAuthService;

    public void setSaveDirectory(String dir){
        this.saveDirectory = dir;
    }

    private Properties getServerProperties(String protocol, String host, int port){
        Properties properties = new Properties();

        //server setting
        properties.put(String.format("mail.%s.host", emailConfig.getPROTOCOL()),emailConfig.getHOST());
        properties.put(String.format("mail.%s.port",emailConfig.getPROTOCOL()),emailConfig.getPORT());

        //SSL Settings
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", emailConfig.getPROTOCOL()),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", emailConfig.getPROTOCOL()),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", emailConfig.getPROTOCOL()),
                String.valueOf(emailConfig.getPORT()));

        return properties;
    }
    public void downloadEmails(String protocol, String host, int port, String username, String password, final String keyword){
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(username, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);


            /// creates a search criterion
            SearchTerm searchCondition = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {
                        if(message.getSubject().contains(keyword)) {
                            return true;
                        }
                    }catch(MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };
            // fetches new messages from server
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen,false);
            Message[] messages = folderInbox.search(unseenFlagTerm);
            Message[] unreadMessages = messages;
            unreadMessages = folderInbox.search(searchCondition, unreadMessages);


            for (int i = 0; i < unreadMessages.length; i++) {
                Message msg = unreadMessages[i];

                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg
                        .getRecipients(MimeMessage.RecipientType.TO));
                String ccList = parseAddresses(msg
                        .getRecipients(MimeMessage.RecipientType.CC));
                String sentDate = msg.getSentDate().toString();

                String contentType = msg.getContentType();
                String messageContent = "";

                // to store attachment file name, separated by comma
                String attachFiles="";
                Path FILESTORAGE= null;

                if (contentType.contains("multipart")&& from.contains("babuagrahari12@gmail.com")) {
                    // content may contain attachments
                	System.out.println("Email Identified and being downloaded . . . . . .");
                    Multipart multiPart = (Multipart) msg.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();
                            Path filestorage = get(emailConfig.getSAVEDIRECTORY(),fileName).toAbsolutePath().normalize();
                            FILESTORAGE=filestorage;

                            attachFiles += fileName + ", ";
                            part.saveFile(saveDirectory + File.separator + fileName);
                            JSONObject processResponse = druAuthService.processDocument(docid,String.valueOf(filestorage),SESSIONTOKEN,processurl);
                           DRUSIGHT_DATA = processResponse;
                           DatabaseFile dbFile = new DatabaseFile(fileName,part.getContentType(),DRUSIGHT_DATA);
                            dbFileRepo.save(dbFile);
                           System.out.println("Document Processed Successfully . . . . ." + DRUSIGHT_DATA.toJSONString());
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = msg.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t To: " + toList);
                System.out.println("\t CC: " + ccList);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
                System.out.println("\t Attachments: "+attachFiles);
                System.out.println("\t File Storage: "+FILESTORAGE);
               // System.out.println("\t File Storage: "+DRUSIGHT_DATA);
            }

            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }
}
