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
package com.ibm.websphere.samples.daytrader.direct;

import com.ibm.cardinal.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.websphere.samples.daytrader.util.KeyBlock;
import com.ibm.websphere.samples.daytrader.util.Log;
import com.ibm.websphere.samples.daytrader.util.TradeConfig;

public class KeySequenceDirect {

    private static HashMap<String, Collection<?>> keyMap = new HashMap<String, Collection<?>>();

    public static synchronized Integer getNextID(Connection conn, String keyName, boolean inSession, boolean inGlobalTxn) throws Exception {
		throw new CardinalException("ERROR: dummy function called at daytrader-ee7-ejb/src/main/java/com/ibm/websphere/samples/daytrader/direct/KeySequenceDirect.java:KeySequenceDirect:getNextID");
    }

    private static Collection<?> allocNewBlock(Connection conn, String keyName, boolean inSession, boolean inGlobalTxn) throws Exception {
		throw new CardinalException("ERROR: dummy function called at daytrader-ee7-ejb/src/main/java/com/ibm/websphere/samples/daytrader/direct/KeySequenceDirect.java:KeySequenceDirect:allocNewBlock");
    }

    private static final String getKeyForUpdateSQL = "select * from keygenejb kg where kg.keyname = ?  for update";

    private static final String createKeySQL = "insert into keygenejb " + "( keyname, keyval ) " + "VALUES (  ?  ,  ? )";

    private static final String updateKeyValueSQL = "update keygenejb set keyval = ? " + "where keyname = ?";

}