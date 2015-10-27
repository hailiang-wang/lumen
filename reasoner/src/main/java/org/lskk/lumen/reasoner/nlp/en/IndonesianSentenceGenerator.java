package org.lskk.lumen.reasoner.nlp.en;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.SynsetID;
import org.apache.commons.lang3.StringUtils;
import org.lskk.lumen.core.CommunicateAction;
import org.lskk.lumen.reasoner.ReasonerException;
import org.lskk.lumen.reasoner.expression.Greeting;
import org.lskk.lumen.reasoner.expression.SpoAdj;
import org.lskk.lumen.reasoner.expression.SpoNoun;
import org.lskk.lumen.reasoner.nlp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Natural language generation for Indonesian.
 * Created by ceefour on 27/10/2015.
 *
 * @see <a href="http://wiki.opencog.org/w/SegSim">SegSim | OpenCog</a>
 */
@Service
@NaturalLanguage("id")
public class IndonesianSentenceGenerator extends SentenceGenerator {
    private static Logger log = LoggerFactory.getLogger(IndonesianSentenceGenerator.class);
    public static final Locale INDONESIAN = Locale.forLanguageTag("id-ID");

    @Inject
    private PronounMapper pronounMapper;
    @Inject @NaturalLanguage("id")
    private Multimap<String, String> wordNet;

    /**
     * Generates a clause (not a proper sentence). The first word is not capitalized.
     * @param locale
     * @param expression
     * @return
     */
    @Override
    public CommunicateAction generate(Locale locale, Object expression) {
        Preconditions.checkNotNull(expression, "expression not null");
        final CommunicateAction action = new CommunicateAction();
        String msg = null;
        if (expression instanceof Greeting) {
            Greeting greeting = (Greeting) expression;
            msg = "Selamat " + greeting.getTimeOfDay();
            if (Pronoun.YOU != greeting.getToPronoun()) {
                // TODO: this is weird
                msg += ", " + pronounMapper.getPronounLabel(INDONESIAN, greeting.getToPronoun(), PronounCase.OBJECT).get();
            }
        } else if (expression instanceof SpoNoun) {
            final SpoNoun spo = (SpoNoun) expression;
            msg = toText(locale, spo.getSubject(), PronounCase.SUBJECT) + " ";
            final Pronoun pronoun = Optional.ofNullable(spo.getSubject().getPronoun()).orElse(Pronoun.IT);
            msg += toText(locale, spo.getPredicate(), pronoun.getPerson(), pronoun.getNumber()) + " ";
            msg += toText(locale, spo.getObject(), PronounCase.OBJECT);
        } else if (expression instanceof SpoAdj) {
            final SpoAdj spo = (SpoAdj) expression;
            msg = toText(locale, spo.getSubject(), PronounCase.SUBJECT) + " ";
            final Pronoun pronoun = Optional.ofNullable(spo.getSubject().getPronoun()).orElse(Pronoun.IT);
            msg += toText(locale, spo.getPredicate(), pronoun.getPerson(), pronoun.getNumber()) + " ";
            msg += toText(locale, spo.getObject());
        } else {
            log.warn("Unknown expression class: {}", expression.getClass().getName());
        }
        action.setObject(msg);
        return action;
    }

    public String toText(Locale locale, NounClause noun, PronounCase pronounCase) {
        String result = "";
        if (noun.getName() != null) {
            result += noun.getName();
        } else if (noun.getPronoun() != null) {
            result += pronounMapper.getPronounLabel(INDONESIAN, noun.getPronoun(), pronounCase).get();
        } else if (noun.getHref() != null) {
//            result += noun.getHref();
            result += getSynsetLemma(noun.getHref());
        } else {
            throw new ReasonerException("Invalid noun: " + noun);
        }
        if (noun.getOwner() != null) {
            result += " " + toText(locale, noun.getOwner(), PronounCase.POSSESSIVE_ADJ);
        }
        return result;
    }

    public String toText(Locale locale, Adjective adj) {
        String result = "";
        if (adj.getHref() != null) {
//            result += adj.getHref();
            result += getSynsetLemma(adj.getHref());
        } else {
            throw new ReasonerException("Invalid adjective: " + adj);
        }
        return result;
    }

    public String toText(Locale locale, Verb verb, PronounPerson person, PronounNumber number) {
        String result = "";
        if (verb.getHref() != null) {
//            result += verb.getHref();
            result += getSynsetLemma(verb.getHref());
        } else {
            throw new ReasonerException("Invalid verb: " + verb);
        }
        return result;
    }

    public String makeSentence(List<String> clauses, SentenceMood mood) {
        String sentence = clauses.stream().collect(Collectors.joining(", "));
        sentence = StringUtils.capitalize(sentence);
        switch (mood) {
            case STATEMENT: sentence += "."; break;
            case EXCLAMATION: sentence += "!"; break;
            case QUESTION: sentence += "?"; break;
            case DANGLING: sentence += "..."; break;
            case CONFUSED: sentence += "?!?!?!"; break;
            case HYPERBOLIC: sentence += "!!!!!!"; break;
            default:
                throw new ReasonerException("Unknown sentence mood: " + mood);
        }
        return sentence;
    }

    protected String getSynsetLemma(String href) {
        final String digits9 = StringUtils.substringAfterLast(href, "_");
        final char numeric = digits9.charAt(0);
        final char pos;
        switch (numeric) {
            case '1': pos = 'n'; break;
            case '2': pos = 'v'; break;
            case '3': pos = 'a'; break; // TODO: can be 'a' or 's' !
            case '4': pos = 'r'; break; // TODO: can be 'r' or 'p' !
            default:
                throw new ReasonerException("Unknown WordNet QName: " + href);
        }
        final String synsetId = digits9.substring(1, digits9.length()) + "-" + pos;
        final Collection<String> lemmas = Preconditions.checkNotNull(wordNet.get(synsetId),
                "Cannot get Indonesian WordNet lemma(s) for %s (from %s)", synsetId, href);
        return lemmas.iterator().next();
    }

}
