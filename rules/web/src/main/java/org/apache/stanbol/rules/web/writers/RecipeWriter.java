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
package org.apache.stanbol.rules.web.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


//import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.rdfjson.serializer.RdfJsonSerializingProvider;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.owl.transformation.OWLAPIToClerezzaConverter;
import org.apache.stanbol.commons.web.base.format.KRFormat;
import org.apache.stanbol.rules.base.api.Recipe;
import org.apache.stanbol.rules.base.api.Rule;
import org.apache.stanbol.rules.base.api.RuleStore;
import org.apache.stanbol.rules.base.api.Symbols;
import org.apache.stanbol.rules.base.api.util.RuleList;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author anuzzolese
 * 
 */
@Component(immediate=true)
@Service(Object.class)
@Property(name = "javax.ws.rs", boolValue = true)
@Provider
@Produces({KRFormat.RDF_XML, KRFormat.OWL_XML, KRFormat.MANCHESTER_OWL, KRFormat.FUNCTIONAL_OWL,
           KRFormat.TURTLE, KRFormat.RDF_JSON, MediaType.TEXT_PLAIN})
public class RecipeWriter implements MessageBodyWriter<Recipe> {

    @Reference
    protected Serializer serializer;
    
//    protected ServletContext servletContext;
    @Reference
    protected RuleStore ruleStore;

//    public RecipeWriter(@Context ServletContext servletContext) {
//        Logger log = LoggerFactory.getLogger(getClass());
//        this.servletContext = servletContext;
//        log.info("Setting context to " + servletContext);
//        serializer = (Serializer) this.servletContext.getAttribute(Serializer.class.getName());
//        if (serializer == null) {
//            log.error("Serializer not found in Servlet context.");
//            serializer = new Serializer();
//        }
//
//        ruleStore = (RuleStore) this.servletContext.getAttribute(RuleStore.class.getName());
//
//        if (serializer == null) {
//            log.error("RuleStore not found in Servlet context.");
//        }
//    }

    @Override
    public long getSize(Recipe arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Recipe.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(Recipe recipe,
                        Class<?> arg1,
                        Type arg2,
                        Annotation[] arg3,
                        MediaType mediaType,
                        MultivaluedMap<String,Object> arg5,
                        OutputStream out) throws IOException, WebApplicationException {

        Logger log = LoggerFactory.getLogger(getClass());

        log.debug("Rendering the list of recipes.");

        if(mediaType.toString().equals(MediaType.TEXT_PLAIN)){
            String recipeString = recipe.toString();
            
            out.write(recipeString.getBytes());
        }
        else if(mediaType.toString().equals(MediaType.TEXT_HTML)){
            String recipeString = recipe.toString();
            
            out.write(recipeString.getBytes());
        }
        
        else{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = OWLManager.getOWLDataFactory();
    
            OWLOntology ontology;
            try {
                ontology = manager.createOntology();
    
                RuleList rules = recipe.getRuleList();
    
                UriRef recipeID = recipe.getRecipeID();
    
                String recipeURI = recipeID.toString().replace("<", "").replace(">", "");
                IRI recipeIRI = IRI.create(recipeURI);
                OWLIndividual recipeIndividual = factory.getOWLNamedIndividual(recipeIRI);
    
                String descriptionURI = Symbols.description.toString().replace("<", "").replace(">", "");
                IRI descriptionIRI = IRI.create(descriptionURI);
                OWLDataProperty descriptionProperty = factory.getOWLDataProperty(descriptionIRI);
                
                OWLAxiom axiom; 
                
                String recipeDescription = recipe.getRecipeDescription();
                if(recipeDescription != null){
                    axiom = factory.getOWLDataPropertyAssertionAxiom(descriptionProperty, recipeIndividual,
                        recipeDescription);
                    manager.addAxiom(ontology, axiom);
                }
                
                if(rules != null){
                    for (Rule rule : rules) {
                        UriRef ruleID = rule.getRuleID();
                        String ruleName = rule.getRuleName();
                        String ruleDescription = rule.getDescription();
        
                        String ruleURI = ruleID.toString().replace("<", "").replace(">", "");
        
                        String ruleNameURI = Symbols.ruleName.toString().replace("<", "").replace(">", "");
        
                        String ruleBodyURI = Symbols.ruleBody.toString().replace("<", "").replace(">", "");
                        String ruleHeadURI = Symbols.ruleHead.toString().replace("<", "").replace(">", "");
                        String hasRuleURI = Symbols.hasRule.toString().replace("<", "").replace(">", "");
        
                        String ruleContent = rule.toString();
        
                        String[] ruleParts = ruleContent.split("\\->");
        
                        IRI ruleIRI = IRI.create(ruleURI);
        
                        IRI ruleNameIRI = IRI.create(ruleNameURI);
                        IRI ruleBodyIRI = IRI.create(ruleBodyURI);
                        IRI ruleHeadIRI = IRI.create(ruleHeadURI);
                        IRI hasRuleIRI = IRI.create(hasRuleURI);
        
                        OWLIndividual ruleIndividual = factory.getOWLNamedIndividual(ruleIRI);
        
                        OWLObjectProperty hasRule = factory.getOWLObjectProperty(hasRuleIRI);
                        OWLDataProperty nameProperty = factory.getOWLDataProperty(ruleNameIRI);
                        OWLDataProperty ruleBodyProperty = factory.getOWLDataProperty(ruleBodyIRI);
                        OWLDataProperty ruleHeadProperty = factory.getOWLDataProperty(ruleHeadIRI);
        
                        // add the name to the rule individual
                        axiom = factory.getOWLDataPropertyAssertionAxiom(nameProperty, ruleIndividual, ruleName);
                        manager.addAxiom(ontology, axiom);
        
                        // add the description to the rule individual
                        if(ruleDescription != null){
                            axiom = factory.getOWLDataPropertyAssertionAxiom(descriptionProperty, ruleIndividual,
                                ruleDescription);
                            manager.addAxiom(ontology, axiom);
                        }
        
                        // add the rule body to the rule individual
                        axiom = factory.getOWLDataPropertyAssertionAxiom(ruleBodyProperty, ruleIndividual,
                            ruleParts[0]);
                        manager.addAxiom(ontology, axiom);
        
                        // add the rule head to the rule individual
                        axiom = factory.getOWLDataPropertyAssertionAxiom(ruleHeadProperty, ruleIndividual,
                            ruleParts[1]);
                        manager.addAxiom(ontology, axiom);
        
                        // bind the rule to the recipe
                        axiom = factory.getOWLObjectPropertyAssertionAxiom(hasRule, recipeIndividual, ruleIndividual);
                        manager.addAxiom(ontology, axiom);
        
                    }
                }
    
                /*
                 * Write the ontology with the list of recipes
                 */
    
                if (mediaType.toString().equals(KRFormat.RDF_XML)) {
    
                    try {
                        manager.saveOntology(ontology, new RDFXMLOntologyFormat(), out);
                    } catch (OWLOntologyStorageException e) {
                        log.error("Failed to store ontology for rendering.", e);
                    }
                } else if (mediaType.toString().equals(KRFormat.OWL_XML)) {
                    try {
                        manager.saveOntology(ontology, new OWLXMLOntologyFormat(), out);
                    } catch (OWLOntologyStorageException e) {
                        log.error("Failed to store ontology for rendering.", e);
                    }
                } else if (mediaType.toString().equals(KRFormat.MANCHESTER_OWL)) {
                    try {
                        manager.saveOntology(ontology, new ManchesterOWLSyntaxOntologyFormat(), out);
                    } catch (OWLOntologyStorageException e) {
                        log.error("Failed to store ontology for rendering.", e);
                    }
                } else if (mediaType.toString().equals(KRFormat.FUNCTIONAL_OWL)) {
                    try {
                        manager.saveOntology(ontology, new OWLFunctionalSyntaxOntologyFormat(), out);
                    } catch (OWLOntologyStorageException e) {
                        log.error("Failed to store ontology for rendering.", e);
                    }
                } else if (mediaType.toString().equals(KRFormat.TURTLE)) {
                    try {
                        manager.saveOntology(ontology, new TurtleOntologyFormat(), out);
                    } catch (OWLOntologyStorageException e) {
                        log.error("Failed to store ontology for rendering.", e);
                    }
                } else if (mediaType.toString().equals(KRFormat.RDF_JSON)) {
    
                    TripleCollection mGraph = OWLAPIToClerezzaConverter.owlOntologyToClerezzaMGraph(ontology);
    
                    RdfJsonSerializingProvider provider = new RdfJsonSerializingProvider();
                    provider.serialize(out, mGraph, SupportedFormat.RDF_JSON);
    
                }
            } catch (OWLOntologyCreationException e1) {
                log.error("An error occurred.", e1);
            }
        }

        out.flush();
    }

}
