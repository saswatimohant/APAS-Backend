package com.misboi.apas.delegates;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class FetchInvDetails implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("Invoice Approval");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Invoice received with id '" + execution.getVariable("invid") + "' from the vendor '" + execution.getVariable("vndrname") + "'.....");
	}
}