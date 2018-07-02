package com.springuni.forgetme.core.adapter;

import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;

public interface DataHandlerGateway {

  ForgetResponse handleForget(ForgetRequest forgetRequest);

}
