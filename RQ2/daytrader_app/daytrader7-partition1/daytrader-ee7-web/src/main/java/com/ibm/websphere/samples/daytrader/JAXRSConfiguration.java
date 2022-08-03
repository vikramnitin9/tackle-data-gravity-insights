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

package com.ibm.websphere.samples.daytrader;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ibm.websphere.samples.daytrader.TradeActionService;
import com.ibm.websphere.samples.daytrader.beans.MarketSummaryDataBeanService;
import com.ibm.websphere.samples.daytrader.entities.AccountDataBeanService;
import com.ibm.websphere.samples.daytrader.entities.AccountProfileDataBeanService;
import com.ibm.websphere.samples.daytrader.entities.HoldingDataBeanService;
import com.ibm.websphere.samples.daytrader.entities.OrderDataBeanService;
import com.ibm.websphere.samples.daytrader.entities.QuoteDataBeanService;
import com.ibm.websphere.samples.daytrader.util.LogService;
import com.ibm.websphere.samples.daytrader.util.TradeConfigService;
import com.ibm.websphere.samples.daytrader.util.FinancialUtilsService;

/**
 * Configures a JAX-RS rest endpoints. 
 */
@ApplicationPath("rest")
public class JAXRSConfiguration extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(TradeActionService.class);
        classes.add(MarketSummaryDataBeanService.class);
        classes.add(AccountDataBeanService.class);
        classes.add(AccountProfileDataBeanService.class);
        classes.add(HoldingDataBeanService.class);
        classes.add(OrderDataBeanService.class);
        classes.add(QuoteDataBeanService.class);
        classes.add(LogService.class);
        classes.add(TradeConfigService.class);
        classes.add(FinancialUtilsService.class);
        return classes;
    }
}