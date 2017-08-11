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

package org.semanticweb.binaryowl.change;

import org.semanticweb.binaryowl.BinaryOWLMetadata;
import org.semanticweb.binaryowl.BinaryOWLParseException;
import org.semanticweb.binaryowl.chunk.ChunkUtil;
import org.semanticweb.binaryowl.chunk.TimeStampedMetadataChunk;
import org.semanticweb.binaryowl.stream.BinaryOWLInputStream;
import org.semanticweb.binaryowl.stream.BinaryOWLOutputStream;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 27/04/2012
 */
public class OntologyChangeDataList implements TimeStampedMetadataChunk, Iterable<OWLOntologyChangeData> {

    public static final int CHUNK_TYPE = ChunkUtil.toInt("ochi");

    private long timestamp;

    private List<OWLOntologyChangeData> list;

    private BinaryOWLMetadata metadata;
    
    public OntologyChangeDataList(List<OWLOntologyChange> ontologyChanges, OWLOntologyID targetOntology, long timeStamp, BinaryOWLMetadata metadata) {
        List<OWLOntologyChangeData> infoList = new ArrayList<OWLOntologyChangeData>();
        for(OWLOntologyChange change : ontologyChanges) {
            if(change.getOntology().getOntologyID().equals(targetOntology)) {
                infoList.add(change.getChangeData());
            }
        }
        this.timestamp = timeStamp;
        this.metadata = metadata;
        this.list = Collections.unmodifiableList(infoList);
    }

    public OntologyChangeDataList(List<OWLOntologyChangeData> list, long timestamp, BinaryOWLMetadata metadata) {
        this.timestamp = timestamp;
        this.list = Collections.unmodifiableList(new ArrayList<OWLOntologyChangeData>(list));
        this.metadata = metadata;
    }

    public OntologyChangeDataList(List<OWLOntologyChangeData> list, long timestamp) {
        this(list, timestamp, BinaryOWLMetadata.emptyMetadata());
    }


    public OntologyChangeDataList(BinaryOWLInputStream inputStream) throws IOException, BinaryOWLParseException {
        read(inputStream);
    }



    public long getTimestamp() {
        return timestamp;
    }

    public BinaryOWLMetadata getMetadata() {
        return metadata;
    }

    public int getChunkType() {
        return CHUNK_TYPE;
    }

    public int size() {
        return list.size();
    }

    public Iterator<OWLOntologyChangeData> iterator() {
        return list.iterator();
    }
    
    public List<OWLOntologyChange> getOntologyChanges(OWLOntology ontology) {
        List<OWLOntologyChange> result = new ArrayList<OWLOntologyChange>();
        for(OWLOntologyChangeData info : list) {
            OWLOntologyChange change = info.createOntologyChange(ontology);
            result.add(change);
        }
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void write(BinaryOWLOutputStream dos) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BinaryOWLOutputStream buffer = new BinaryOWLOutputStream(bos, dos.getVersion());
        buffer.writeLong(timestamp);
        writeMetadata(buffer);
        writeChangeRecordData(buffer);

        dos.writeInt(bos.size());
        dos.writeInt(CHUNK_TYPE);
        bos.writeTo(dos);
    }



    private void read(BinaryOWLInputStream inputStream) throws IOException, BinaryOWLParseException {
    	inputStream.readInt();
        int chunkType = inputStream.readInt();
        if(chunkType != CHUNK_TYPE) {
            throw new BinaryOWLParseException("Expected change record chunk marker (0x" + Integer.toHexString(CHUNK_TYPE) + ") but found 0x" + Integer.toHexString(chunkType));
        }
        // Timestamp
        timestamp = inputStream.readLong();
        // Metadata
        metadata = readMetadata(inputStream);
        // Change list
        list = Collections.unmodifiableList(readChangeRecordData(inputStream));
    }

    private List<OWLOntologyChangeData> readChangeRecordData(BinaryOWLInputStream inputStream) throws IOException, BinaryOWLParseException {
    	inputStream.readInt();
        int listSize = inputStream.readInt();
        List<OWLOntologyChangeData> list = new ArrayList<OWLOntologyChangeData>(listSize + 2);
        for(int i = 0; i < listSize; i++) {
            OWLOntologyChangeData representative = OntologyChangeDataType.read(inputStream);
            list.add(representative);
        }
        return list;
    }


    private BinaryOWLMetadata readMetadata(BinaryOWLInputStream inputStream) throws IOException, BinaryOWLParseException {
    	inputStream.readInt();
        return new BinaryOWLMetadata(inputStream);

    }

    private void writeMetadata(BinaryOWLOutputStream mainOutputStream) throws IOException {
        ByteArrayOutputStream metadataByteOutputStream = new ByteArrayOutputStream();
        DataOutputStream metaDataOutputStream = new DataOutputStream(metadataByteOutputStream);
        BinaryOWLOutputStream metadataOWLOutputStream = new BinaryOWLOutputStream(metaDataOutputStream, mainOutputStream.getVersion());
        metadata.write(metadataOWLOutputStream);

        // Size of metadata in bytes
        int recordSize = metaDataOutputStream.size();
        mainOutputStream.writeInt(recordSize);
        // Actual metadata
        metadataByteOutputStream.writeTo(mainOutputStream);
    }



    private void writeChangeRecordData(BinaryOWLOutputStream mainOutputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream changeRecordDataOutputStream = new DataOutputStream(byteArrayOutputStream);
        BinaryOWLOutputStream dataOWLOutputStream = new BinaryOWLOutputStream(changeRecordDataOutputStream, mainOutputStream.getVersion());
        // Change List
        changeRecordDataOutputStream.writeInt(list.size());
        for(OWLOntologyChangeData Data : list) {
            OntologyChangeDataType.write(Data, dataOWLOutputStream);
        }

        // Size of changes in bytes
        int size = changeRecordDataOutputStream.size();
        mainOutputStream.writeInt(size);
        // Actual changes
        byteArrayOutputStream.writeTo(mainOutputStream);
    }

}
