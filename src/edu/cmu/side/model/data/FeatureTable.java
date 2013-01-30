package edu.cmu.side.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.cmu.side.model.FreqMap;
import edu.cmu.side.model.feature.Feature;
import edu.cmu.side.model.feature.FeatureHit;

/**
 * A many-directional mapping of Features, FeatureHits and indexes into the DocumentList.
 *
 */
public class FeatureTable implements Serializable
{	
	private static final long serialVersionUID = 1048801132974685418L;
	private DocumentList documents;

	private Map<Feature, Collection<FeatureHit>> hitsPerFeature;
	private List<Collection<FeatureHit>> hitsPerDocument;
	
	private Map<String, double[]> numericConvertedClassValues = new HashMap<String, double[]>();
	private List<String> nominalConvertedClassValues = new ArrayList<String>();
	
	private Feature.Type type = null;
	private Integer threshold = 5;
	private String name = "no name set";

	/**
	 * Uses a sort of shoddy and roundabout catch-exception way of figuring out if the data type is nominal or numeric.
	 * @return
	 */
	public Feature.Type getClassValueType(){
		return documents.getValueType(documents.getCurrentAnnotation());
	}


	private FeatureTable(){
		this.hitsPerFeature = new HashMap<Feature, Collection<FeatureHit>>(100000); //Rough guess at capacity requirement.
		this.hitsPerDocument  = new ArrayList<Collection<FeatureHit>>();
	}

	public FeatureTable(DocumentList sdl, Collection<FeatureHit> hits, int thresh){
		this();
		Map<Feature, Set<Integer>> localFeatures = new HashMap<Feature, Set<Integer>>(100000);
		this.threshold = thresh;
		this.documents = sdl;
		
		generateConvertedClassValues();
		
		
		for(int i = 0; i < sdl.getSize(); i++){
			hitsPerDocument.add(new TreeSet<FeatureHit>());
		}
		for(FeatureHit hit : hits){
			Feature f = hit.getFeature();
			if(!localFeatures.containsKey(f)){
				localFeatures.put(f, new TreeSet<Integer>());
			}
			localFeatures.get(f).add(hit.getDocumentIndex());
		}
		for(FeatureHit hit : hits){
			if(localFeatures.get(hit.getFeature()).size() >= threshold){
				hitsPerDocument.get(hit.getDocumentIndex()).add(hit);
				if(!hitsPerFeature.containsKey(hit.getFeature())){
					hitsPerFeature.put(hit.getFeature(), new TreeSet<FeatureHit>());
				}
				hitsPerFeature.get(hit.getFeature()).add(hit);
			}
		}
	}
	
	public void generateConvertedClassValues(){
		numericConvertedClassValues.clear();
		nominalConvertedClassValues.clear();
		switch(getClassValueType()){
		case NOMINAL:
		case BOOLEAN:
			for(String s : getDocumentList().getLabelArray()){
				double[] convertedClassValues = new double[getDocumentList().getSize()];
				for(int i = 0; i < getDocumentList().getSize(); i++){
					convertedClassValues[i] = getNumericConvertedClassValue(i, s);
				}
				numericConvertedClassValues.put(s, convertedClassValues);
			}
			nominalConvertedClassValues = getDocumentList().getAnnotationArray();
			break;
		case NUMERIC:
			String target = "numeric";
			double[] convertedClassValues = new double[getDocumentList().getSize()];
			ArrayList<Double> toSort = new ArrayList<Double>();
			for(int i = 0; i < getDocumentList().getSize(); i++){
				convertedClassValues[i] = getNumericConvertedClassValue(i, target);
				toSort.add(convertedClassValues[i]);
			}
			numericConvertedClassValues.put(target, convertedClassValues);
			break;
		}
	}
	
	public double[] getNumericClassValues(String target){
		double[] out = null;
		switch(getClassValueType()){
		case NOMINAL:
		case BOOLEAN:
			out = numericConvertedClassValues.get(target);
			break;
		case NUMERIC:
			out = numericConvertedClassValues.get("numeric");
			break;
		}
		return out;	
	}

	public void setName(String n){
		name = n;
	}

	public void setDocumentList(DocumentList sdl){
		documents = sdl;
		generateConvertedClassValues();
	}

	public void setThreshold(int n){
		threshold = n;
	}
	
	public int getThreshold(){
		return threshold;
	}
	public String getName(){
		return name;
	}

	public DocumentList getDocumentList(){
		return documents;
	}
	
	public String getDomainName(int indx){
		return documents.getInstanceDomain(indx);
	}
	
	/**
	 * @return the set of features extracted from the documents.
	 */
	public Set<Feature> getFeatureSet() {
		return hitsPerFeature.keySet();
	}
	/**
	 * @return the set of features extracted from the documents.
	 */
	public Collection<Feature> getSortedFeatures() {        
		return new TreeSet<Feature>(hitsPerFeature.keySet());
	}

	public Collection<FeatureHit> getHitsForFeature(Feature feature) {
		return hitsPerFeature.get(feature);
	}

	public Collection<FeatureHit> getHitsForDocument(int index) {
		return hitsPerDocument.get(index);
	}
	

	/**
	 * When creating feature hits, they're done on a per-feature basis. This fills the data structure
     * that maps those hits per document instead.
    */
	private void fillHitsPerDocument(FeatureTable ft) {
		ft.hitsPerDocument  = new ArrayList<Collection<FeatureHit>>();
	    for(int i = 0; i < hitsPerDocument.size(); i++)
	    	ft.hitsPerDocument.add(new ArrayList<FeatureHit>());
	    
	    for(Feature f : hitsPerFeature.keySet()){
	    	ft.hitsPerFeature.put(f, hitsPerFeature.get(f));
	        for(FeatureHit fh : ft.hitsPerFeature.get(f))
	        	ft.hitsPerDocument.get(fh.getDocumentIndex()).add(fh);
	    }
	}
	
	public FeatureTable clone(){
		FeatureTable ft = new FeatureTable();
	    ft.setName(getName()+" (clone)");
	    ft.hitsPerFeature = new HashMap<Feature, Collection<FeatureHit>>(30000); //Rough guess at capacity requirement.
	    ft.threshold = threshold;
	    fillHitsPerDocument(ft);
	    return ft;
	}
	
	public int numInstances(){
		return documents.getSize();
	}
	
	
    public void deleteFeatureSet(Set<Feature> f){
    	for(int i = 0; i < hitsPerDocument.size(); i++){
    		Collection<FeatureHit> tmphits = new ArrayList<FeatureHit>();
	        for(FeatureHit hit : hitsPerDocument.get(i))
	        	if(!f.contains(hit.getFeature())) tmphits.add(hit);
	        hitsPerDocument.set(i, tmphits);
	     }
	     for (Feature fe : f)
	    	 hitsPerFeature.remove(fe);
    }
	
	public Double getNumericConvertedClassValue(int i, String target){
		if (getClassValueType() == Feature.Type.NUMERIC){
			return Double.parseDouble(documents.getAnnotationArray().get(i));			
		}else if (getClassValueType() == Feature.Type.BOOLEAN){
			return (documents.getAnnotationArray().get(i).equals(Boolean.TRUE.toString()))?1.0:0.0;
		}else {
			return (documents.getAnnotationArray().get(i).equals(target))?1.0:0.0;
		}
	}      
	
	public String[] getNominalLabelArray(){
		return getDocumentList().getLabelArray();
	}
	
	public String[] getLabelArray(){
		String[] result = null;
		switch(getClassValueType()){
		case NOMINAL:
		case BOOLEAN:
		case STRING:
			result = getNominalLabelArray();
			break;
		case NUMERIC:
			result = new String[]{"numeric"};
			break;
		}
		return result;
	}
}