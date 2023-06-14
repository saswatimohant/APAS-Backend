package com.misboi.apas.services;

import com.misboi.apas.entities.AuthDetails;
import org.json.simple.JSONObject;

import java.text.ParseException;

public interface DruAuthService {


    public JSONObject authenticateDruSight(String authurl,String userid,String password) throws ParseException, org.json.simple.parser.ParseException;

    public JSONObject processDocument(String docid, String filepath, String SESSIONTOKEN, String processurl)throws Exception;



}
