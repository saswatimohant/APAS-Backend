package com.misboi.apas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "misboi.connector.email")
public class EmailConfig {

    private String HOST, USERNAME, PASSWORD, PROTOCOL, SAVEDIRECTORY, KEYWORD;
    private int PORT;

    public EmailConfig(String HOST, String USERNAME, String PASSWORD, String PROTOCOL, String SAVEDIRECTORY, String KEYWORD, int PORT) {
        super();
        this.HOST = HOST;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.PROTOCOL = PROTOCOL;
        this.SAVEDIRECTORY = SAVEDIRECTORY;
        this.KEYWORD = KEYWORD;
        this.PORT = PORT;
    }

    public EmailConfig() {
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getPROTOCOL() {
        return PROTOCOL;
    }

    public void setPROTOCOL(String PROTOCOL) {
        this.PROTOCOL = PROTOCOL;
    }

    public String getSAVEDIRECTORY() {
        return SAVEDIRECTORY;
    }

    public void setSAVEDIRECTORY(String SAVEDIRECTORY) {
        this.SAVEDIRECTORY = SAVEDIRECTORY;
    }

    public String getKEYWORD() {
        return KEYWORD;
    }

    public void setKEYWORD(String KEYWORD) {
        this.KEYWORD = KEYWORD;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    @Override
    public String toString() {
        return "EmailConfig{" +
                "HOST='" + HOST + '\'' +
                ", USERNAME='" + USERNAME + '\'' +
                ", PASSWORD='" + PASSWORD + '\'' +
                ", PROTOCOL='" + PROTOCOL + '\'' +
                ", SAVEDIRECTORY='" + SAVEDIRECTORY + '\'' +
                ", KEYWORD='" + KEYWORD + '\'' +
                ", PORT=" + PORT +
                '}';
    }
}
