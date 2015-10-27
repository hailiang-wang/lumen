package org.lskk.lumen.reasoner.nlp;

import java.io.Serializable;

/**
 * Created by ceefour on 27/10/2015.
 */
public class NounClause implements Serializable {

    public static final NounClause I = new NounClause(Pronoun.I);
    public static final NounClause YOU = new NounClause(Pronoun.YOU);
    public static final NounClause WE = new NounClause(Pronoun.WE);
    public static final NounClause THEY = new NounClause(Pronoun.THEY);
    public static final NounClause HE = new NounClause(Pronoun.HE);
    public static final NounClause SHE = new NounClause(Pronoun.SHE);

    private String name;
    private String href;
    private Pronoun pronoun;
    private NounClause owner;

    public NounClause(Pronoun pronoun) {
        this.pronoun = pronoun;
    }

    /**
     * Mentions an exact name, directly to be uttered.
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * QName of this noun, if known.
     * @return
     */
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * If pronoun is used, {@link #getName()} and {@link #getHref()} are optional.
     * @return
     */
    public Pronoun getPronoun() {
        return pronoun;
    }

    public void setPronoun(Pronoun pronoun) {
        this.pronoun = pronoun;
    }

    /**
     * Owner, i.e. "my" or "Hendy's".
     * @return
     */
    public NounClause getOwner() {
        return owner;
    }

    public void setOwner(NounClause owner) {
        this.owner = owner;
    }
}