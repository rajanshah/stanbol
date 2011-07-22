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
package org.apache.stanbol.ontologymanager.ontonet.impl.registry;

import org.apache.stanbol.ontologymanager.ontonet.api.registry.RegistryItemFactory;
import org.apache.stanbol.ontologymanager.ontonet.api.registry.models.Library;
import org.apache.stanbol.ontologymanager.ontonet.api.registry.models.Registry;
import org.apache.stanbol.ontologymanager.ontonet.api.registry.models.RegistryOntology;
import org.apache.stanbol.ontologymanager.ontonet.impl.registry.model.LibraryImpl;
import org.apache.stanbol.ontologymanager.ontonet.impl.registry.model.RegistryImpl;
import org.apache.stanbol.ontologymanager.ontonet.impl.registry.model.RegistryOntologyImpl;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Default implementation of a registry item factory.
 */
public class RegistryItemFactoryImpl implements RegistryItemFactory {

    /**
     * Creates a new instance of {@link RegistryItemFactoryImpl}.
     */
    public RegistryItemFactoryImpl() {}

    @Override
    public Library createLibrary(OWLNamedIndividual ind) {
        return new LibraryImpl(ind.getIRI(), ind.getIRI().getFragment());
    }

    @Override
    public Registry createRegistry(OWLOntology o) {
        return o.isAnonymous() ? null : new RegistryImpl(o.getOntologyID().getOntologyIRI(), o
                .getOntologyID().toString());
    }

    @Override
    public RegistryOntology createRegistryOntology(OWLNamedIndividual ind) {
        return new RegistryOntologyImpl(ind.getIRI(), ind.getIRI().getFragment());
    }

}
