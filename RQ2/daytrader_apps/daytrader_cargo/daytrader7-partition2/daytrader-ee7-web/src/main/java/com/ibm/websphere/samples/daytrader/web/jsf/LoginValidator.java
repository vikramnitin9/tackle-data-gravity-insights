/**
 * (C) Copyright IBM Corporation 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.websphere.samples.daytrader.web.jsf;

import com.ibm.cardinal.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.ibm.websphere.samples.daytrader.util.Log;


public class LoginValidator implements Validator{

  static String loginRegex = "uid:\\d+";
  static Pattern pattern = Pattern.compile(loginRegex);
  static Matcher matcher;

  // Simple JSF validator to make sure username starts with uid: and at least 1 number.
  public LoginValidator() {
		throw new CardinalException("ERROR: dummy function called at daytrader-ee7-web/src/main/java/com/ibm/websphere/samples/daytrader/web/jsf/LoginValidator.java:LoginValidator:LoginValidator");
    }

  
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		throw new CardinalException("ERROR: dummy function called at daytrader-ee7-web/src/main/java/com/ibm/websphere/samples/daytrader/web/jsf/LoginValidator.java:LoginValidator:validate");
    }
}