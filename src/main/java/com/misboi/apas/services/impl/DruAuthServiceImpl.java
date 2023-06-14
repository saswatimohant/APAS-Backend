package com.misboi.apas.services.impl;

import com.misboi.apas.entities.AuthDetails;
import com.misboi.apas.services.DruAuthService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@Service
public class DruAuthServiceImpl implements DruAuthService {
    private static final Logger log = LoggerFactory.getLogger(DruAuthServiceImpl.class);

    @Override
    public JSONObject authenticateDruSight(String authurl, String userid, String password) throws ParseException, org.json.simple.parser.ParseException {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

        map.add("username",userid);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(authurl,request,String.class);

        JSONObject resp = (JSONObject) new JSONParser().parse(response.getBody());

        System.out.println(resp);
        log.debug("Service Ended : authenticateDruSight");

        return resp;
    }

    @Override
    public JSONObject processDocument(String docid, String filepath, String SESSIONTOKEN, String processurl) throws Exception {
        log.debug("Service Execution Started: processDocument");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        header.add("docid",docid);
        header.add("batchmode","false");
        header.add("Cookie","com.drubus.sessionToken="+SESSIONTOKEN);
        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("file",new FileSystemResource(filepath));

        HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, header);
        RestTemplate restTemplate = new RestTemplate();

        log.info("Process URL: "+processurl);
        ResponseEntity<String> response = restTemplate.postForEntity(processurl, requestEntity, String.class);
        String value = response.getBody();
        JSONObject drusight_data=(JSONObject) new JSONParser().parse(value);
        log.info("drusight_data: "+drusight_data);

        return drusight_data;
    }
    
    public static void main(String[] args)
    {
    	DruAuthServiceImpl dip = new DruAuthServiceImpl();
    	JSONObject authInfo = null, docInfo = null;
    	
    	try {
			authInfo = dip.authenticateDruSight(AuthDetails.authurl, AuthDetails.userid, AuthDetails.password);
			System.out.println("SessionToken: " + authInfo.get("SessionToken"));
			docInfo = dip.processDocument("100001", AuthDetails.filepath, (String)authInfo.get("SessionToken"), AuthDetails.processurl);
			System.out.println("Document Data: " + docInfo.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
