package org.lskk.lumen.reasoner.interaction;

import com.google.common.collect.ImmutableMap;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lskk.lumen.core.LumenCoreConfig;
import org.lskk.lumen.core.LumenLocale;
import org.lskk.lumen.persistence.neo4j.Literal;
import org.lskk.lumen.persistence.neo4j.PartitionKey;
import org.lskk.lumen.persistence.neo4j.Thing;
import org.lskk.lumen.persistence.neo4j.ThingLabel;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by ceefour on 18/02/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = JavaScriptIntentTest.IntentConfig.class)
@SpringApplicationConfiguration(PromptTaskTest.Config.class)
public class PromptTaskTest {

    @SpringBootApplication(scanBasePackageClasses = {PromptTask.class},
        exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class, JmxAutoConfiguration.class, CamelAutoConfiguration.class, GroovyTemplateAutoConfiguration.class})
    @Import({LumenCoreConfig.class})
//    @Configuration
//    @ComponentScan(basePackageClasses = {IntentExecutor.class, ThingRepository.class, YagoTypeRepository.class, FactServiceImpl.class})
//    @EnableJpaRepositories(basePackageClasses = YagoTypeRepository.class)
//    @EnableNeo4jRepositories(basePackageClasses = ThingRepository.class)
    public static class Config {

    }

    @Inject
    private PromptTaskRepository promptTaskRepo;

    @Test
    public void promptBirthDate() {
        final PromptTask promptBirthdate = promptTaskRepo.create("promptBirthdate");
        assertThat(promptBirthdate.getId(), equalTo("promptBirthdate"));
        assertThat(promptBirthdate.getAskSsmls(), hasSize(greaterThan(1)));
        assertThat(promptBirthdate.getUtterancePatterns(), hasSize(greaterThan(1)));
        assertThat(promptBirthdate.getProperty(), equalTo("yago:wasBornOnDate"));
        assertThat(promptBirthdate.getExpectedTypes(), contains("xs:date"));

//        assertThat(promptBirthdate.getPendingPropositions(LumenLocale.INDONESIAN).peek().getObject(), containsString("lahir"));
//        assertThat(promptBirthdate.getPendingPropositions(Locale.US).get().getObject(), containsString("born"));

        final List<UtterancePattern> matches = promptBirthdate.matchUtterance(LumenLocale.INDONESIAN, "Aku lahir tanggal 14 Desember 1983.",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(2));
        assertThat(matches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("birthdate", "14 Desember 1983")));
        assertThat(matches.get(1).getSlotStrings(), equalTo(ImmutableMap.of("birthdate", "14 Desember 1983")));
        assertThat(matches.get(0).getSlotValues(), equalTo(ImmutableMap.of("birthdate", new LocalDate("1983-12-14"))));
        assertThat(matches.get(1).getSlotValues(), equalTo(ImmutableMap.of("birthdate", new LocalDate("1983-12-14"))));
    }

    @Test
    public void promptNameTask() {
        final PromptTask promptName = promptTaskRepo.create("promptName");
        assertThat(promptName, instanceOf(PromptNameTask.class));
        assertThat(promptName.getId(), equalTo("promptName"));
        assertThat(promptName.getAskSsmls(), hasSize(greaterThan(1)));
        assertThat(promptName.getUtterancePatterns(), hasSize(greaterThan(1)));
        assertThat(promptName.getProperty(), equalTo("rdfs:label"));
        assertThat(promptName.getExpectedTypes(), contains("xsd:string"));

//        assertThat(promptName.getPendingPropositions(LumenLocale.INDONESIAN).get().getObject(), containsString("nama"));
//        assertThat(promptName.getPendingPropositions(Locale.US).get().getObject(), containsString("name"));
    }

    @Test
    public void promptNameHendyIrawan() {
        final PromptTask promptName = promptTaskRepo.create("promptName");

        final List<UtterancePattern> matches = promptName.matchUtterance(LumenLocale.INDONESIAN, "Namaku Hendy Irawan",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(2)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("name", "Hendy Irawan")));
        assertThat(confidentMatches.get(0).getSlotValues(), equalTo(ImmutableMap.of("name", "Hendy Irawan")));

        final List<ThingLabel> labels = promptName.generateLabelsToAssert(matches);
        assertThat(labels, hasSize(greaterThanOrEqualTo(3)));
        final ThingLabel skos_prefLabel = labels.stream().filter(it -> "skos:prefLabel".equals(it.getPropertyQName())).findFirst().get();
        assertThat(skos_prefLabel.getValue(), equalTo("Hendy Irawan"));
        assertThat(skos_prefLabel.getMetaphone(), equalTo("HNTRWN"));
        assertThat(skos_prefLabel.getInLanguage(), equalTo("id-ID"));
        assertThat(skos_prefLabel.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(skos_prefLabel.getConfidence(), lessThan(1f));
        final ThingLabel rdfs_label = labels.stream().filter(it -> "rdfs:label".equals(it.getPropertyQName())).findFirst().get();
        assertThat(rdfs_label.getValue(), equalTo("Hendy Irawan"));
        assertThat(rdfs_label.getMetaphone(), equalTo("HNTRWN"));
        assertThat(rdfs_label.getInLanguage(), equalTo("id-ID"));
        assertThat(rdfs_label.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(rdfs_label.getConfidence(), equalTo(1f));
        final ThingLabel yago_hasGivenName = labels.stream().filter(it -> "yago:hasGivenName".equals(it.getPropertyQName())).findFirst().get();
        assertThat(yago_hasGivenName.getValue(), equalTo("Hendy"));
        assertThat(yago_hasGivenName.getMetaphone(), equalTo("HNT"));
        assertThat(yago_hasGivenName.getInLanguage(), equalTo("id-ID"));
        assertThat(yago_hasGivenName.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(yago_hasGivenName.getConfidence(), equalTo(1f));
        final ThingLabel yago_hasFamilyName = labels.stream().filter(it -> "yago:hasFamilyName".equals(it.getPropertyQName())).findFirst().get();
        assertThat(yago_hasFamilyName.getValue(), equalTo("Irawan"));
        assertThat(yago_hasFamilyName.getMetaphone(), equalTo("IRWN"));
        assertThat(yago_hasFamilyName.getInLanguage(), equalTo("id-ID"));
        assertThat(yago_hasFamilyName.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(yago_hasFamilyName.getConfidence(), equalTo(1f));
    }

    @Test
    public void promptNameSigitAriWijanarko() {
        final PromptTask promptName = promptTaskRepo.create("promptName");

        final List<UtterancePattern> matches = promptName.matchUtterance(Locale.US, "I am Sigit Ari Wijanarko",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(2)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("name", "Sigit Ari Wijanarko")));
        assertThat(confidentMatches.get(0).getSlotValues(), equalTo(ImmutableMap.of("name", "Sigit Ari Wijanarko")));

        final List<ThingLabel> labels = promptName.generateLabelsToAssert(matches);
        assertThat(labels, hasSize(greaterThanOrEqualTo(3)));
        final ThingLabel skos_prefLabel = labels.stream().filter(it -> "skos:prefLabel".equals(it.getPropertyQName())).findFirst().get();
        assertThat(skos_prefLabel.getValue(), equalTo("Sigit Ari Wijanarko"));
        assertThat(skos_prefLabel.getMetaphone(), equalTo("SJTRWJNRK"));
        assertThat(skos_prefLabel.getInLanguage(), equalTo("en-US"));
        assertThat(skos_prefLabel.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(skos_prefLabel.getConfidence(), lessThan(1f));
        final ThingLabel rdfs_label = labels.stream().filter(it -> "rdfs:label".equals(it.getPropertyQName())).findFirst().get();
        assertThat(rdfs_label.getValue(), equalTo("Sigit Ari Wijanarko"));
        assertThat(rdfs_label.getMetaphone(), equalTo("SJTRWJNRK"));
        assertThat(rdfs_label.getInLanguage(), equalTo("en-US"));
        assertThat(rdfs_label.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(rdfs_label.getConfidence(), equalTo(1f));
        final ThingLabel yago_hasGivenName = labels.stream().filter(it -> "yago:hasGivenName".equals(it.getPropertyQName())).findFirst().get();
        assertThat(yago_hasGivenName.getValue(), equalTo("Sigit Ari"));
        assertThat(yago_hasGivenName.getMetaphone(), equalTo("SJTR"));
        assertThat(yago_hasGivenName.getInLanguage(), equalTo("en-US"));
        assertThat(yago_hasGivenName.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(yago_hasGivenName.getConfidence(), equalTo(1f));
        final ThingLabel yago_hasFamilyName = labels.stream().filter(it -> "yago:hasFamilyName".equals(it.getPropertyQName())).findFirst().get();
        assertThat(yago_hasFamilyName.getValue(), equalTo("Wijanarko"));
        assertThat(yago_hasFamilyName.getMetaphone(), equalTo("WJNRK"));
        assertThat(yago_hasFamilyName.getInLanguage(), equalTo("en-US"));
        assertThat(yago_hasFamilyName.getPartition(), equalTo(PartitionKey.lumen_var));
        assertThat(yago_hasFamilyName.getConfidence(), equalTo(1f));
    }

    @Test
    public void promptGenderTask() {
        final PromptTask promptGender = promptTaskRepo.create("promptGender");
        assertThat(promptGender, instanceOf(PromptGenderTask.class));
        assertThat(promptGender.getId(), equalTo("promptGender"));
        assertThat(promptGender.getAskSsmls(), hasSize(greaterThan(1)));
        assertThat(promptGender.getUtterancePatterns(), hasSize(greaterThan(1)));
        assertThat(promptGender.getProperty(), equalTo("yago:hasGender"));
        assertThat(promptGender.getExpectedTypes(), contains("yago:wordnet_sex_105006898"));

//        assertThat(promptGender.getProposition(LumenLocale.INDONESIAN).getObject(), containsString("nama"));
//        assertThat(promptGender.getProposition(Locale.US).getObject(), containsString("name"));
    }

    @Test
    public void promptGenderIndonesianMale() {
        final PromptTask promptGender = promptTaskRepo.create("promptGender");

        final List<UtterancePattern> matches = promptGender.matchUtterance(LumenLocale.INDONESIAN, "Gue cowok",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("gender", "cowok")));
        final Thing gender = (Thing) confidentMatches.get(0).getSlotValues().get("gender");
        assertThat(gender.getNn(), equalTo("yago:male"));
        assertThat(gender.getPrefLabelLang(), equalTo("id-ID"));
    }

    @Test
    public void promptGenderIndonesianFemale() {
        final PromptTask promptGender = promptTaskRepo.create("promptGender");

        final List<UtterancePattern> matches = promptGender.matchUtterance(LumenLocale.INDONESIAN, "Aku gadis",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("gender", "gadis")));
        final Thing gender = (Thing) confidentMatches.get(0).getSlotValues().get("gender");
        assertThat(gender.getNn(), equalTo("yago:female"));
        assertThat(gender.getPrefLabelLang(), equalTo("id-ID"));
    }

    @Test
    public void promptGenderEnglishMale() {
        final PromptTask promptGender = promptTaskRepo.create("promptGender");

        final List<UtterancePattern> matches = promptGender.matchUtterance(Locale.US, "I am a man",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("gender", "a man")));
        final Thing gender = (Thing) confidentMatches.get(0).getSlotValues().get("gender");
        assertThat(gender.getNn(), equalTo("yago:male"));
        assertThat(gender.getPrefLabelLang(), equalTo("en-US"));
    }

    @Test
    public void promptGenderEnglishFemale() {
        final PromptTask promptGender = promptTaskRepo.create("promptGender");

        final List<UtterancePattern> matches = promptGender.matchUtterance(Locale.US, "I am female",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream().filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("gender", "female")));
        final Thing gender = (Thing) confidentMatches.get(0).getSlotValues().get("gender");
        assertThat(gender.getNn(), equalTo("yago:female"));
        assertThat(gender.getPrefLabelLang(), equalTo("en-US"));
    }

    @Test
    public void promptReligionTask() {
        final PromptTask promptReligion = promptTaskRepo.create("promptReligion");
        assertThat(promptReligion, instanceOf(PromptReligionTask.class));
        assertThat(promptReligion.getId(), equalTo("promptReligion"));
        assertThat(promptReligion.getAskSsmls(), hasSize(greaterThan(1)));
        assertThat(promptReligion.getUtterancePatterns(), hasSize(greaterThan(1)));
        assertThat(promptReligion.getProperty(), equalTo("lumen:hasReligion"));
        assertThat(promptReligion.getExpectedTypes(), contains("yago:wordnet_religion_105946687"));

//        assertThat(promptGender.getProposition(LumenLocale.INDONESIAN).getObject(), containsString("nama"));
//        assertThat(promptGender.getProposition(Locale.US).getObject(), containsString("name"));
    }

    @Test
    public void promptReligionIndonesian() {
        final PromptTask promptReligion = promptTaskRepo.create("promptReligion");

        final List<UtterancePattern> matches = promptReligion.matchUtterance(LumenLocale.INDONESIAN, "Saya muslimah",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream()
                .filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("religion", "muslimah")));
        final Thing religion = (Thing) confidentMatches.get(0).getSlotValues().get("religion");
        assertThat(religion.getNn(), equalTo(PromptReligionTask.Religion.ISLAM.getHref()));
        assertThat(religion.getPrefLabelLang(), equalTo("id-ID"));
    }

    @Test
    public void promptReligionEnglish() {
        final PromptTask promptReligion = promptTaskRepo.create("promptReligion");

        final List<UtterancePattern> matches = promptReligion.matchUtterance(Locale.US, "I believe in Protestant",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream()
                .filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("religion", "Protestant")));
        final Thing religion = (Thing) confidentMatches.get(0).getSlotValues().get("religion");
        assertThat(religion.getNn(), equalTo(PromptReligionTask.Religion.PROTESTANTISM.getHref()));
        assertThat(religion.getPrefLabelLang(), equalTo("en-US"));
    }


    @Test
    public void promptAgeTask() {
        final PromptTask promptAge = promptTaskRepo.create("promptAge");
        assertThat(promptAge, instanceOf(PromptAgeTask.class));
        assertThat(promptAge.getId(), equalTo("promptAge"));
        assertThat(promptAge.getAskSsmls(), hasSize(greaterThan(1)));
        assertThat(promptAge.getUtterancePatterns(), hasSize(greaterThan(1)));
        assertThat(promptAge.getProperty(), equalTo("lumen:hasBirthYear"));
        assertThat(promptAge.getExpectedTypes(), contains("xsd:integer"));

//        assertThat(promptGender.getProposition(LumenLocale.INDONESIAN).getObject(), containsString("nama"));
//        assertThat(promptGender.getProposition(Locale.US).getObject(), containsString("name"));
    }

    @Test
    public void promptAgeIndonesian() {
        final PromptTask promptAge = promptTaskRepo.create("promptAge");

        final List<UtterancePattern> matches = promptAge.matchUtterance(LumenLocale.INDONESIAN, "Saya berusia 45th",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream()
                .filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("age", "45")));
        final Integer age = (Integer) confidentMatches.get(0).getSlotValues().get("age");
        assertThat(age, equalTo(45));
        final List<Literal> literals = promptAge.generateLiteralsToAssert(matches);
        assertThat(literals, hasSize(1));
        assertThat(literals.get(0).getValue(), equalTo(1971));
        assertThat(literals.get(0).getType(), equalTo("xsd:integer"));
        assertThat(literals.get(0).getPredicate().getNn(), equalTo("lumen:hasBirthYear"));
    }

    @Test
    public void promptAgeEnglish() {
        final PromptTask promptAge = promptTaskRepo.create("promptAge");

        final List<UtterancePattern> matches = promptAge.matchUtterance(Locale.US, "I'm 16 yrs old",
                UtterancePattern.Scope.ANY);
        assertThat(matches, hasSize(greaterThanOrEqualTo(1)));
        final List<UtterancePattern> confidentMatches = matches.stream()
                .filter(it -> 1f == it.getConfidence()).collect(Collectors.toList());
        assertThat(confidentMatches, hasSize(greaterThanOrEqualTo(1)));
        assertThat(confidentMatches.get(0).getSlotStrings(), equalTo(ImmutableMap.of("age", "16")));
        final Integer age = (Integer) confidentMatches.get(0).getSlotValues().get("age");
        assertThat(age, equalTo(16));
        final List<Literal> literals = promptAge.generateLiteralsToAssert(matches);
        assertThat(literals, hasSize(1));
        assertThat(literals.get(0).getValue(), equalTo(2000));
        assertThat(literals.get(0).getType(), equalTo("xsd:integer"));
        assertThat(literals.get(0).getPredicate().getNn(), equalTo("lumen:hasBirthYear"));
    }

}
