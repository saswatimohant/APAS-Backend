package com.misboi.apas.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class ProcessLauncher {

	@Autowired
	private static Environment env;
	private static ProcessInstance procInst;
	private static Map<String, Object> objMap = new HashMap<String, Object>();
	private static JSONObject procInstInfo;
	private static Map<String, Object> g_inputData = null;
	private final static Logger LOGGER = Logger.getLogger("Employee Onboarding");
	
	public static String getProperty(String prop){
	    return env.getProperty(prop);
	}
	
	public static Map<String, Object> prepareDefaultInput()
	{
		g_inputData = new HashMap<String, Object>();
		g_inputData.put("empid", "1");
		g_inputData.put("empname", "Shivanshu Agrahari");
		g_inputData.put("empemail", "shivanshu.agrahari@misboi.com");
		g_inputData.put("empcont", "0987654321");
		
		return g_inputData;
	}
	
	public static Map<String, Object> prepareInputData(JSONObject inputData)
	{
		g_inputData = new HashMap<String, Object>();
		Set<String> keys = inputData.keySet();
		Iterator<String> keyItr = keys.iterator();
		String key = null;
		
		while(keyItr.hasNext()) {
			key = keyItr.next();
			g_inputData.put(key, inputData.get(key));
		}
		return g_inputData;
	}
	
	public static JSONObject launchProcess(RuntimeService runtimeService, String procKey, JSONObject inputData)
	{	
		LOGGER.info("Process Key: " + procKey + " : " + runtimeService.toString());
		System.out.println("Inside ProcessLauncher inputData: " + procKey + " : " + inputData.toJSONString());
		
		if(inputData == null) {
			procInst = runtimeService.startProcessInstanceByKey(procKey, prepareDefaultInput());
		} else {
			procInst = runtimeService.startProcessInstanceByKey(procKey, prepareInputData(inputData));
		}
		objMap.put("businessKey", procInst.getBusinessKey());
		objMap.put("processInstanceId", procInst.getProcessInstanceId());
		procInstInfo = new JSONObject(objMap);
		System.out.println("Inside ProcessLauncher procInstInfo: " + procInstInfo.toJSONString());
		return procInstInfo;
	}
	
	public static JSONObject completeTask(TaskService taskService, JSONObject inputData)
	{
		
		taskService.complete(taskService.createTaskQuery()
	            .processInstanceId(inputData.get("procKey").toString())
	            .taskDefinitionKey(inputData.get("taskName").toString())
	            .active().singleResult().getId());
		
		return procInstInfo;
	}
	
}
