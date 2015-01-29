package id.ac.itb.lumen.persistence

import groovy.transform.CompileStatic
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by Budhi on 21/01/2015.
 */
@CompileStatic
@NodeEntity
@Deprecated
class Person {
    @GraphId
    Long nodeId
    @Indexed(unique = true)
    String href
    String prefLabel
}
