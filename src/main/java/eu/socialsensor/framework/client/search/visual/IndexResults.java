package eu.socialsensor.framework.client.search.visual;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndexResults {

	@Expose
    @SerializedName(value = "@numResults")
	int numResults;
	
	@Expose
    @SerializedName(value = "results")
	NearestImage[] results;
}
