package org.northshore.cbri.dict
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine
import static org.apache.uima.fit.util.JCasUtil.selectSingle
import static org.junit.Assert.*
import groovy.util.logging.Log4j

import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation
import org.apache.ctakes.typesystem.type.textspan.Sentence
import org.apache.log4j.BasicConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.uima.fit.factory.JCasFactory
import org.apache.uima.jcas.JCas
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.northshore.cbri.dsl.UIMAUtil
import org.northshore.cbri.token.TokenAnnotator

@Log4j
class UmlsDictionaryAnnotatorTest {
    static String testText = """
FINAL DIAGNOSIS:
A) Ileocecal valve, colon, polyp:
    - Colonic mucosa with a small well-circumscribed lymphoid aggregate.
B) Transverse colon polyp:
    - Adenomatous polyp.
C) Sigmoid colon:
    - Hyperplastic polyp.
    - Tubular adenoma .
"""
    AnalysisEngine tokenizer;
    JCas jcas;

    @BeforeClass
    public static void setupClass() {
        BasicConfigurator.configure()

//        File dictFile = new File('src/test/resources/dict/test-umls-dict-auto.txt')
//        UmlsDictionaryFileCreator.createDictFile(dictFile, ['C0334292', 'C0227391', 'C0001430', 'C0333983', 'C0206677'].toSet())
    }

    @Before
    public void setUp() throws Exception {
        AnalysisEngineDescription tokenDesc = AnalysisEngineFactory.createEngineDescription(
                TokenAnnotator)
        ExternalResourceFactory.createDependencyAndBind(tokenDesc,
                TokenAnnotator.TOKEN_MODEL_KEY,
                opennlp.uima.tokenize.TokenizerModelResourceImpl,
                "file:models/en-token.bin")

        this.tokenizer = AnalysisEngineFactory.createEngine(tokenDesc)
        this.jcas = JCasFactory.createJCas()
        this.jcas.setDocumentText(this.testText)
        UIMAUtil.JCas = this.jcas
        UIMAUtil.create(type:Sentence, begin:0, end:this.testText.length()-1)
        tokenizer.process(jcas)

//        UIMAUtil.select(type:BaseToken).each { token ->
//            println "Token: ${token.coveredText}"
//        }
    }

    @After
    public void tearDown() throws Exception {
    }

	@Ignore
    @Test
    public void smokeTestHandMadeDict() throws Exception {
        AnalysisEngineDescription dictDesc = AnalysisEngineFactory.createEngineDescription(
                UmlsDictionaryAnnotator,
                UmlsDictionaryAnnotator.PARAM_DICTIONARY_FILE, "/dict/test-umls-dict-manual.txt")
        ExternalResourceFactory.createDependencyAndBind(dictDesc,
                TokenAnnotator.TOKEN_MODEL_KEY,
                opennlp.uima.tokenize.TokenizerModelResourceImpl,
                "file:models/en-token.bin")
        AnalysisEngine dictionary = AnalysisEngineFactory.createEngine(dictDesc)
        dictionary.process(jcas)

        // test results
        Collection<IdentifiedAnnotation> idAnns = UIMAUtil.select(type:IdentifiedAnnotation)
        idAnns.each { println "Identified Annotation: [${it.coveredText}]" }
        assertEquals 3, idAnns.size()
        assertEquals 'Sigmoid colon', idAnns[0].coveredText
        assertEquals 'Tubular adenoma', idAnns[1].coveredText
        assertEquals 'adenoma', idAnns[2].coveredText
    }

	@Ignore
    @Test
    public void smokeTestAutoGeneratedDict() throws Exception {
        AnalysisEngineDescription dictDesc = AnalysisEngineFactory.createEngineDescription(
                UmlsDictionaryAnnotator,
                UmlsDictionaryAnnotator.PARAM_DICTIONARY_FILE, "/dict/test-umls-dict-auto.txt")
        ExternalResourceFactory.createDependencyAndBind(dictDesc,
                TokenAnnotator.TOKEN_MODEL_KEY,
                opennlp.uima.tokenize.TokenizerModelResourceImpl,
                "file:models/en-token.bin")
        AnalysisEngine dictionary = AnalysisEngineFactory.createEngine(dictDesc)
        dictionary.process(jcas)

        // test results
        Collection<IdentifiedAnnotation> idAnns = UIMAUtil.select(type:IdentifiedAnnotation)
        idAnns.each { println "Identified Annotation: [${it.coveredText}]" }
        assertEquals 5, idAnns.size()

        assert idAnns[0].coveredText == 'Adenomatous polyp'
        assert idAnns[0].class == DiseaseDisorderMention
        assert idAnns[0].ontologyConcepts[0].cui == 'C0206677'
        assert idAnns[0].ontologyConcepts[0].tui == 'T191'

        assert idAnns[1].coveredText == 'Sigmoid colon'
        assert idAnns[1].class == AnatomicalSiteMention
        assert idAnns[1].ontologyConcepts[0].cui == 'C0227391'
        assert idAnns[1].ontologyConcepts[0].tui == 'T023'

        assert idAnns[2].coveredText == 'Hyperplastic polyp'
        assert idAnns[2].class == DiseaseDisorderMention
        assert idAnns[2].ontologyConcepts[0].cui == 'C0333983'
        assert idAnns[2].ontologyConcepts[0].tui == 'T191'

        assert idAnns[3].coveredText == 'Tubular adenoma'
        assert idAnns[3].class == DiseaseDisorderMention
        assert idAnns[3].ontologyConcepts[0].cui == 'C0334292'
        assert idAnns[3].ontologyConcepts[0].tui == 'T191'

        assert idAnns[4].coveredText == 'adenoma'
        assert idAnns[4].class == DiseaseDisorderMention
        assert idAnns[4].ontologyConcepts[0].cui == 'C0001430'
        assert idAnns[4].ontologyConcepts[0].tui == 'T191'
    }
}
