package com.misboi.apas.services;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface WorkflowService {

	// Launch a new workflow
	public JSONObject launchWorkflow(RuntimeService runtimeService, JSONObject reqData) throws Exception;
	
	public JSONObject completeTask(TaskService taskService, JSONObject reqData) throws Exception;

	public JSONObject findByWorkflowId(String wfId) throws Exception;
	
	public void deleteByWorkflowId(String wfId) throws Exception;
}

