package org.northshore.cbri

import groovy.util.logging.Log4j

import org.apache.ctakes.typesystem.type.relation.UMLSRelation
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.resource.ResourceInitializationException

@Log4j
abstract class BratAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_ANN_FILE = "annFileName"
	public static final String PARAM_VIEW_NAME = "viewName"
	
	@ConfigurationParameter(name = "annFileName", mandatory = true, description = "File holding BRAT annotations")
	private String annFileName

	@ConfigurationParameter(name = "viewName", mandatory = true, description = "Name of JCas view")
	private String viewName

	private BratDocument bratDoc

	@Override
	public void initialize(UimaContext context)
	throws ResourceInitializationException {
		super.initialize(context);
		InputStream annIn = BratAnnotator.class.getResourceAsStream(this.annFileName)
		this.bratDoc = BratDocument.parseDocument(annIn)
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas view = jcas.createView(this.viewName)
		UIMAUtil.jcas = view
		view.setDocumentText(jcas.getDocumentText())
		Map<String, Annotation> annMap = new HashMap<>()
		this.bratDoc.getSpanAnnotations().each { key, value ->
			Annotation ann = mapSpanAnnotation(view, value)
			annMap.put(key, ann)
		}
		this.bratDoc.getRelAnnotations().values().each { value ->
			IdentifiedAnnotation arg1 = annMap.get(value.arg1)
			IdentifiedAnnotation arg2 = annMap.get(value.arg2)
			UMLSRelation rel = createUMLSRelation(view, arg1, arg2, value)
		}
	}

	abstract protected Annotation mapSpanAnnotation(JCas jcas, SpanAnnotation span);

	abstract protected UMLSRelation createUMLSRelation(JCas jcas, 
		IdentifiedAnnotation arg1,	IdentifiedAnnotation arg2,
		RelationAnnotation rel)
}
