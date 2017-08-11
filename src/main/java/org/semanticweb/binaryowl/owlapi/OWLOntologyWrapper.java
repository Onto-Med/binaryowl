package org.semanticweb.binaryowl.owlapi;

import org.semanticweb.binaryowl.doc.OWLOntologyDocument;
import org.semanticweb.owlapi.model.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/07/2013
 */
public class OWLOntologyWrapper implements OWLOntologyDocument {

    private OWLOntology delegate;

    public OWLOntologyWrapper(OWLOntology delegate) {
        this.delegate = delegate;
    }

    @Override
    public OWLOntologyID getOntologyID() {
        return delegate.getOntologyID();
    }

    @Override
    public Set<OWLAnnotation> getAnnotations() {
        return delegate.annotations().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLImportsDeclaration> getImportsDeclarations() {
        return delegate.importsDeclarations().collect(Collectors.toSet());
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
        return delegate.axioms(axiomType).collect(Collectors.toSet());
    }

    @Override
    public Set<OWLClass> getClassesInSignature() {
        return delegate.classesInSignature().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
        return delegate.objectPropertiesInSignature().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature() {
        return delegate.dataPropertiesInSignature().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
        return delegate.annotationPropertiesInSignature().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature() {
        return delegate.individualsInSignature().collect(Collectors.toSet());
    }

    @Override
    public Set<OWLDatatype> getDatatypesInSignature() {
        return delegate.datatypesInSignature().collect(Collectors.toSet());
    }
}
