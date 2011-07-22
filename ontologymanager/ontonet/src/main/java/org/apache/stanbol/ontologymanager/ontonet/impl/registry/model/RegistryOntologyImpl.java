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
package org.apache.stanbol.ontologymanager.ontonet.impl.registry.model;

import org.apache.stanbol.ontologymanager.ontonet.api.registry.RegistryOntologyNotLoadedException;
import org.apache.stanbol.ontologymanager.ontonet.api.registry.models.RegistryOntology;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

public class RegistryOntologyImpl extends AbstractRegistryItem implements RegistryOntology {

    private OWLOntology owl;

    public RegistryOntologyImpl(IRI iri) {
        super(iri);
    }

    public RegistryOntologyImpl(IRI iri, String name) {
        super(iri, name);
    }

    @Override
    public OWLOntology asOWLOntology() throws RegistryOntologyNotLoadedException {
        fireContentRequested(this);
        return owl;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setOWLOntology(OWLOntology owl) {
        this.owl = owl;
    }

}
