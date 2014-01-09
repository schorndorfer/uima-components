

/* First created by JCasGen Fri Jan 03 13:41:43 CST 2014 */
package org.northshore.cbri.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Type defined in org.northshore.cbri.type
 * Updated by JCasGen Fri Jan 03 13:41:43 CST 2014
 * XML source: C:/WKT/git/schorndorfer/uima-components/annotator-parent/annotator-negation/src/main/resources/descriptors/NegExTypeSystem.xml
 * @generated */
public class NegationScope extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NegationScope.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NegationScope() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NegationScope(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NegationScope(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NegationScope(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    