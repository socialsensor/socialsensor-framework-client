package eu.socialsensor.framework.client.search.visual;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearestImage {

	@Expose
    @SerializedName(value = "score")
	public float similarity;
	@Expose
    @SerializedName(value = "name")
    public String id;
	
	@Expose
    @SerializedName(value = "rank")
	public int rank;

    public NearestImage(String id, float  similarity) {
    	this.id = id;
        this.similarity = similarity;
    }

    public String toString() {
    	return "{ " + id + " : " + similarity + " }";
    }
}
