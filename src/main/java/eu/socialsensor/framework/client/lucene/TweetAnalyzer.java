package eu.socialsensor.framework.client.lucene;

import java.io.Reader;
import java.util.Set;

import org.apache.log4j.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class TweetAnalyzer extends StopwordAnalyzerBase {
  
  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
  public static final int DEFAULT_NGRAMS = 3;
  private static int ngrams;
  
  private Logger  logger = Logger.getLogger(TweetAnalyzer.class);

  public TweetAnalyzer(Version matchVersion,Set<?> stopwordsSet, int ngrams) {
	  super(matchVersion, new CharArraySet(Version.LUCENE_40, stopwordsSet, true));
	  TweetAnalyzer.ngrams = ngrams;
	  logger.info("Tweet analizer: stopwords, "+TweetAnalyzer.ngrams+"-grams");
  }
  
  public TweetAnalyzer(Version matchVersion) {
	  super(matchVersion, StandardAnalyzer.STOP_WORDS_SET);
	  TweetAnalyzer.ngrams = DEFAULT_NGRAMS;
	  logger.info("Tweet analizer: stopwords, "+TweetAnalyzer.ngrams+"-grams");
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
	// Tweet tokenizer
	  
	//logger.info("Create components for analyzer");
	  
	Tokenizer source = new TweetTokenizer(matchVersion, reader);
	
    // lower-casing and stemming
    //TokenStream result = new PorterStemFilter(new LowerCaseFilter(matchVersion, source));
	TokenStream result = new LowerCaseFilter(matchVersion, source);
    
    // stop-wording
    result = new StopFilter(matchVersion, result, stopwords);
    
    // n-gram filter
    if (ngrams>1)
    {
    	ShingleFilter sF = new ShingleFilter(result,2,ngrams);
    	sF.setOutputUnigrams(true);
    	//result = sF;
    	// done
        return new Analyzer.TokenStreamComponents(source, sF);
    }
    
    // done
    return new Analyzer.TokenStreamComponents(source, result);
}

}
