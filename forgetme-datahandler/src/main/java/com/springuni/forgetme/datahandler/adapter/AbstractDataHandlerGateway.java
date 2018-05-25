package com.springuni.forgetme.datahandler.adapter;

import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;

public abstract class AbstractDataHandlerGateway {

  public abstract ForgetResponse handleForget(ForgetRequest forgetRequest);

}
