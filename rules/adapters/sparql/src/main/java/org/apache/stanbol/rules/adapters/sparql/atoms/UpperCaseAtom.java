/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.rules.adapters.sparql.atoms;

import org.apache.stanbol.rules.adapters.AbstractAdaptableAtom;
import org.apache.stanbol.rules.adapters.sparql.SPARQLFunction;
import org.apache.stanbol.rules.base.api.RuleAtom;
import org.apache.stanbol.rules.base.api.RuleAtomCallExeption;
import org.apache.stanbol.rules.base.api.SPARQLObject;
import org.apache.stanbol.rules.base.api.UnavailableRuleObjectException;
import org.apache.stanbol.rules.base.api.UnsupportedTypeForExportException;
import org.apache.stanbol.rules.manager.atoms.StringFunctionAtom;

/**
 * It adapts any UpperCaseAtom to the xPath function <http://www.w3.org/2005/xpath-functions#upper-case>.
 * 
 * @author anuzzolese
 * 
 */
public class UpperCaseAtom extends AbstractAdaptableAtom {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T adapt(RuleAtom ruleAtom) throws RuleAtomCallExeption {

        StringFunctionAtom argument = ((org.apache.stanbol.rules.manager.atoms.UpperCaseAtom) ruleAtom)
                .getStringFunctionAtom();

        try {
            SPARQLObject sparqlArgument = adapter.adaptTo(argument, SPARQLObject.class);

            String sparql = "<http://www.w3.org/2005/xpath-functions#upper-case> ("
                            + sparqlArgument.getObject() + ")";

            return (T) new SPARQLFunction(sparql);

        } catch (UnsupportedTypeForExportException e) {
            throw new org.apache.stanbol.rules.base.api.RuleAtomCallExeption(getClass());
        } catch (UnavailableRuleObjectException e) {
            throw new org.apache.stanbol.rules.base.api.RuleAtomCallExeption(getClass());
        }

    }

}
