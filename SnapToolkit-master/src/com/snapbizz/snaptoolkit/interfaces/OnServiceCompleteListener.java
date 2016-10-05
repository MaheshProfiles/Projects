package com.snapbizz.snaptoolkit.interfaces;

import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.utils.RequestCodes;

public interface OnServiceCompleteListener {

   public void onSuccess(ResponseContainer response);
   public void onError(ResponseContainer response, RequestCodes requestCode);

}