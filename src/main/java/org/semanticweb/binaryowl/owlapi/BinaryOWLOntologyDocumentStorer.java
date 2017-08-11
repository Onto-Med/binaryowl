/*
 * This file is part of the OWL API.
 *  
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *  
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, The University of Manchester
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

package org.semanticweb.binaryowl.owlapi;

import org.semanticweb.binaryowl.BinaryOWLOntologyDocumentSerializer;
import org.semanticweb.binaryowl.doc.OWLOntologyDocument;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URI;
import java.util.Optional;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/05/2012
 */
public class BinaryOWLOntologyDocumentStorer implements OWLStorer {

	private static final long serialVersionUID = 6644223908951348911L;

	public boolean canStoreOntology(@Nonnull OWLDocumentFormat ontologyFormat) {
        return ontologyFormat instanceof BinaryOWLOntologyDocumentFormat;
    }

    @Override
    public void storeOntology(@Nonnull OWLOntology ontology, @Nonnull IRI documentIRI, @Nonnull OWLDocumentFormat format) throws OWLOntologyStorageException, IOException {
        URI uri = documentIRI.toURI();
        if(!uri.isAbsolute()) {
            throw new OWLOntologyStorageException("Document IRI is not absolute: " + documentIRI);
        }
        File file = new File(uri);
        storeOntology(ontology, new BufferedOutputStream(new FileOutputStream(file)));
    }

    @Override
    public void storeOntology(@Nonnull OWLOntology ontology, @Nonnull OWLOntologyDocumentTarget target, @Nonnull OWLDocumentFormat format) throws OWLOntologyStorageException, IOException {
        Optional<OutputStream> outputStream = target.getOutputStream();
    	
    	if (outputStream.isPresent()) {
            storeOntology(ontology, new BufferedOutputStream(outputStream.get()));
        }
        else {
            throw new OWLOntologyStorageException("Unsupported target type");
        }
    }

    private void storeOntology(OWLOntology ontology, OutputStream os) throws OWLOntologyStorageException, IOException {
        DataOutputStream dos = new DataOutputStream(os);
        BinaryOWLOntologyDocumentSerializer serializer = new BinaryOWLOntologyDocumentSerializer();
        final OWLOntologyDocument doc = new OWLOntologyWrapper(ontology);
        serializer.write(doc, dos);
    }
}
