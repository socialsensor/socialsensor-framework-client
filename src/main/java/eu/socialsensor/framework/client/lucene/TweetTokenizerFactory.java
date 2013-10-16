package eu.socialsensor.framework.client.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class TweetTokenizerFactory extends TokenizerFactory {
	@Override
	public Tokenizer create(Reader input) {
		TweetTokenizer tokenizer = new TweetTokenizer(luceneMatchVersion, input); 
	    return tokenizer;
	}
}
