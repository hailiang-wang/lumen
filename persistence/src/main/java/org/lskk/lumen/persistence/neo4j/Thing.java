package org.lskk.lumen.persistence.neo4j;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Indexes:
 * CREATE INDEX ON :schema_Thing(_partition);
 * CREATE INDEX ON :schema_Thing(nn);
 * CREATE INDEX ON :schema_Thing(prefLabel);
 *
 * Created by ceefour on 14/02/2016.
 */
@NodeEntity(label = "schema_Thing")
public class Thing implements Serializable {
    @GraphId
    private Long gid;
    @Property(name = "nn")
    private String nn;
    private String prefLabel;
    private String prefLabelLang;
    @Property(name = "_partition")
    private String partition;
    @Relationship(type = "rdf_type")
    private Set<Thing> types = new HashSet<>();
    @Relationship(type = "rdfs_subClassOf")
    private Set<Thing> superClasses = new HashSet<>();

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    /**
     * For YAGO things, the partition is
     * "lumen_yago".
     * For user-modifiable things, the partition is
     * "lumen_var".
     * Why not with [tenantEnv]? To make it easy to import/export.
     * @return
     */
    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getNn() {
        return nn;
    }

    public void setNn(String nn) {
        this.nn = nn;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    /**
     * Language of the {@link #getPrefLabel()} (if set).
     * {@code null} means unknown.
     * The default is {@link java.util.Locale#US} and things should have this set.
     * @return
     */
    public String getPrefLabelLang() {
        return prefLabelLang;
    }

    public void setPrefLabelLang(String prefLabelLang) {
        this.prefLabelLang = prefLabelLang;
    }

    public Set<Thing> getTypes() {
        return types;
    }

    public Set<Thing> getSuperClasses() {
        return superClasses;
    }

}
