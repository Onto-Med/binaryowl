package org.semanticweb.binaryowl.owlobject.serializer;

import org.semanticweb.binaryowl.BinaryOWLParseException;
import org.semanticweb.binaryowl.stream.BinaryOWLInputStream;
import org.semanticweb.binaryowl.stream.BinaryOWLOutputStream;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLRule;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/07/2013
 */
public class SWRLRuleSerializer extends AbstractAxiomSerializer<SWRLRule> {

    @Override
    protected void writeAxiom(SWRLRule axiom, BinaryOWLOutputStream outputStream) throws IOException {
        outputStream.writeOWLObjects(axiom.body().collect(Collectors.toSet()));
        outputStream.writeOWLObjects(axiom.head().collect(Collectors.toSet()));
    }

    @Override
    protected SWRLRule readAxiom(BinaryOWLInputStream inputStream, Set<OWLAnnotation> annotations) throws IOException, BinaryOWLParseException {
        Set<SWRLAtom> body = inputStream.readOWLObjects();
        Set<SWRLAtom> head = inputStream.readOWLObjects();
        return inputStream.getDataFactory().getSWRLRule(body, head, annotations);
    }
}
