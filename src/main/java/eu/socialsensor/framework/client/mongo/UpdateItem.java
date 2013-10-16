package eu.socialsensor.framework.client.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.socialsensor.framework.common.domain.JSONable;

import java.util.HashMap;
import java.util.Map;


public class UpdateItem implements JSONable {
	
	Gson gson = new GsonBuilder()
    	.excludeFieldsWithoutExposeAnnotation()
    	.create();
	
	@Expose
	@SerializedName(value = "$set")
    protected Map<Object, Object> set = null;
	
	@Expose
	@SerializedName(value = "$inc")
    protected Map<Object, Object> inc = null;
	
	@Expose
	@SerializedName(value = "$addToSet")
    protected Map<Object, Map<Object,Object[]>> addToSet = null;
	
	@Expose
	@SerializedName(value = "$or")
	protected Map<Object, Object> select = null;
	
	public void setField(String json) {
		DBObject fields = gson.fromJson(json, BasicDBObject.class);
		if(set == null)
			set = new HashMap<Object, Object>();
		for(String key :fields.keySet()) {
			Object value = fields.get(key);
			set.put(key, value);
		}
	}
	
	public void setField(Object name, Object value) {
		if(set == null) {
			set = new HashMap<Object, Object>();
		}
		set.put(name, value);
	}
	
	public void incField(Object name, int value) {
		if(inc == null) {
			inc = new HashMap<Object, Object>();
		}
		inc.put(name, value);
	}
	
	public void addValues(Object field, Object[] values) {
		if(addToSet == null) {
                addToSet = new HashMap<Object, Map<Object, Object[]>>();
            }
		Map<Object, Object[]> each = new HashMap<Object, Object[]>();
		each.put("$each", values);
		addToSet.put(field, each);
	}
	
	public static void main(String[] args) {
		UpdateItem update = new UpdateItem();
		update.setField("likes", "1327");
		update.setField("shares", "349");
		
		String[] c = new String[2];
		c[0] = "21"; c[1] = "13";
		
		update.addValues("comments", c);
		
		System.out.println(update.toJSONString());
	}
	
    public String toJSONString() {
        
        return gson.toJson(this);
    }

}
