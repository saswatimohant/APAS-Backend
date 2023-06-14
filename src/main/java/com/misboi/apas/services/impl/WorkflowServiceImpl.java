package com.misboi.apas.services.impl;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.misboi.apas.helper.ProcessLauncher;
import com.misboi.apas.services.WorkflowService;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

	// Launch a new workflow
		public JSONObject launchWorkflow(RuntimeService runtimeService, JSONObject reqData) throws Exception
		{
			JSONObject resData = ProcessLauncher.launchProcess(runtimeService, reqData.get("processName").toString(), reqData);
			System.out.println("Inside WorkflowServiceImpl launchWorkflow: " + reqData.toJSONString());
			System.out.println("Inside WorkflowServiceImpl launchWorkflow: " + resData.toJSONString());
			return resData;
		}
		
		public JSONObject completeTask(TaskService taskService, JSONObject reqData) throws Exception
		{
			System.out.println("Inside WorkflowServiceImpl taskService: " + taskService.toString());
			System.out.println("Inside WorkflowServiceImpl procKey: " + reqData.get("procKey").toString());
			System.out.println("Inside WorkflowServiceImpl taskName: " + reqData.get("taskName").toString());
			System.out.println("Inside WorkflowServiceImpl taskId: " + taskService.createTaskQuery()
	            .processInstanceId(reqData.get("procKey").toString())
	            .taskDefinitionKey(reqData.get("taskName").toString()).toString());
	            //.singleResult().getId());
			Task task = taskService.createTaskQuery().processInstanceId(reqData.get("procKey").toString()).singleResult();
			System.out.println("Inside WorkflowServiceImpl taskId: " + task.getId());
		    taskService.complete(task.getId());
			//System.out.println("Inside WorkflowServiceImpl completeTask: " + reqData.toJSONString());
			//JSONObject resData = ProcessLauncher.completeTask(taskService, reqData);
			//System.out.println("Inside WorkflowServiceImpl completeTask: " + resData.toJSONString());
			return reqData;
		}
	// Find workflow by workflow id
		public JSONObject findByWorkflowId(String wfId) throws Exception
		{
			
			return null;
		}
		
	// Find workflow by workflow id
			public void deleteByWorkflowId(String wfId) throws Exception
			{
				
			}

}
