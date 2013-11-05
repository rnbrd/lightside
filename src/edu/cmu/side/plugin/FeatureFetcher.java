package edu.cmu.side.plugin;

import java.util.List;

import edu.cmu.side.model.feature.Feature;

public interface FeatureFetcher
{
	abstract class AbstractFeatureFetcherPlugin extends SIDEPlugin implements FeatureFetcher
	{
		@Override
		public boolean isTokenized(Feature f)
		{
			return false;
		}
		
		@Override
		public List<String> tokenize(Feature f, String s)
		{
			throw new UnsupportedOperationException("This FeatureFetcher does not tokenize");
		}
		
		//TODO: move stanfordTokenizing here?
	}
	
	/**
	 * 
	 * @param f a feature produced by this fetcher. 
	 * @return whether this particular feature is generated from a tokenized version of the document text, 
	 * (meaning that any LocalFeatureHits generated by this fetcher use token-relative indices)
	 */
	boolean isTokenized(Feature f);
	
	/**
	 * 
	 * @param s the raw String for an instance
	 * @param f the Feature whose tokenization method should be reproduced
	 * @return the tokenized list for the input string
	 * @throws UnsupportedOperationException if this FeatureFetcher doesn't tokenize for this Feature.
	 */
	List<String> tokenize(Feature f, String s);
}
