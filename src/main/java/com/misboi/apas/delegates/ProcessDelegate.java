package com.misboi.apas.delegates;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ProcessDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("APAS Invoice Processing");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Invoice received with id '" + execution.getVariable("invid") + "' from vendor name '" + execution.getVariable("vndrname") + "'.....");
	}
}