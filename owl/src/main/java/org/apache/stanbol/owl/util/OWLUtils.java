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
package org.apache.stanbol.owl.util;

import java.util.Iterator;

import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.ontologies.OWL;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of utility methods for the manipulation of OWL API objects.
 */
public class OWLUtils {

    private static Logger log = LoggerFactory.getLogger(OWLUtils.class);

    public static UriRef guessOntologyIdentifier(Object g) {
        if (g instanceof TripleCollection) return guessOntologyIdentifier((TripleCollection) g);
        else if (g instanceof OWLOntology) return URIUtils
                .createUriRef(guessOntologyIdentifier((OWLOntology) g));
        else throw new IllegalArgumentException("Cannot guess ontology identifier for objects of type "
                                                + g.getClass());
    }

    /**
     * If the ontology is named, this method will return its logical ID, otherwise it will return the location
     * it was retrieved from (which is still unique).
     * 
     * @param o
     * @return
     */
    public static IRI guessOntologyIdentifier(OWLOntology o) {
        String iri;
        // For named OWL ontologies it is their ontology ID. For anonymous ontologies, it is the URI they were
        // fetched from, if any.
        if (o.isAnonymous()) iri = o.getOWLOntologyManager().getOntologyDocumentIRI(o).toString();
        else iri = o.getOntologyID().getOntologyIRI().toString();
        // Strip fragment or query tokens. TODO do proper URL Encoding.
        while (iri.endsWith("#") || iri.endsWith("?"))
            iri = iri.substring(0, iri.length() - 1);
        // try {
        // if (originalIri.endsWith("#")) originalIri = originalIri.substring(0,
        // originalIri.length() - 1) + URLEncoder.encode("#", "UTF-8");
        // else if (originalIri.endsWith("?")) originalIri = originalIri.substring(0,
        // originalIri.length() - 1)
        // + URLEncoder.encode("?", "UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // // That cannot be.
        // }
        return IRI.create(iri);
    }

    public static final String NS_STANBOL = "http://stanbol.apache.org/";

    public static UriRef guessOntologyIdentifier(TripleCollection g) {
        Iterator<Triple> it = g.filter(null, RDF.type, OWL.Ontology);
        if (it.hasNext()) {
            NonLiteral subj = it.next().getSubject();
            if (it.hasNext()) log.warn(
                "RDF Graph {} has multiple OWL ontology definitions! Ignoring all but {}", g, subj);
            if (subj instanceof UriRef) return (UriRef) subj;
        }
        return new UriRef(NS_STANBOL + System.currentTimeMillis());
    }
}
