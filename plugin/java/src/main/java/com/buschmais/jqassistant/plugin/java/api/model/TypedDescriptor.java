package com.buschmais.jqassistant.plugin.java.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Interface for value descriptors which provide a type information.
 */
public interface TypedDescriptor {

    @Relation("OF_TYPE")
    TypeDescriptor getType();

    void setType(TypeDescriptor type);
}
