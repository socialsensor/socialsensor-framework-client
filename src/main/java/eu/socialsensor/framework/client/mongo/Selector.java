package eu.socialsensor.framework.client.mongo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eu.socialsensor.framework.common.domain.JSONable;

public class Selector implements JSONable {
	
	Map<Object, Object> and = null;
	
	public void select(Object field, Object value) {
		if(and == null)
			and = new HashMap<Object, Object>();
		and.put(field, value);
	}
	
	public void exists(Object field) {
		if(and == null)
			and = new HashMap<Object, Object>();
		Map<String, Boolean> temp = new HashMap<String, Boolean>();
		temp.put("$exists", Boolean.TRUE);
		and.put(field, temp);
	}
	
	public void selectGreaterThan(Object field, Object value) {
		if(and == null)
			and = new HashMap<Object, Object>();
		@SuppressWarnings("unchecked")
		Map<Object, Object> op = (Map<Object, Object>) and.get(field);
		if(op == null)
			op = new HashMap<Object, Object>();
		op.put("$gt", value);
		and.put(field, op);
	}
	
	public void selectLessThan(Object field, Object value) {
		if(and == null)
			and = new HashMap<Object, Object>();
		@SuppressWarnings("unchecked")
		Map<Object, Object> op = (Map<Object, Object>) and.get(field);
		if(op == null)
			op = new HashMap<Object, Object>();
		op.put("$lt", value);
		and.put(field, op);
	}
	
        
	
	@Override
	public String toJSONString() {
		Gson gson = new GsonBuilder()
        	.excludeFieldsWithoutExposeAnnotation()
        	.registerTypeAdapter(Selector.class, new SelectorSerializer())
        	.create();
		return gson.toJson(this);
	}
	
	public static void main(String[] args) {
		Selector selector = new Selector();
		selector.select("id", "32123437523234788965");
		selector.select("timeslotId", "34glppjv02vpd");
		selector.select("source", "Twitter");
		selector.selectGreaterThan("pubDate", "12-01-2012");
		selector.selectLessThan("pubDate", "12-12-2012");
		System.out.println(selector.toJSONString());
	}
	
	public class SelectorSerializer extends TypeAdapter<Selector> {
	    @Override
	    public void write(JsonWriter out, Selector selector) throws IOException {
	    	out.beginObject();
	    	Map<Object, Object> fields = selector.and;
	    	if(fields==null){
	    		out.endObject();
	    		return;
	    	}
	    	for(Entry<Object, Object> entry : fields.entrySet()) {
	    		Object key = entry.getKey();
	    		Object value = entry.getValue()	;	
	    		
	    		out.name(key.toString());
	    		if(value instanceof String) {
    				out.value(value.toString());
	    		} else if(value instanceof Long) {
    				out.value((Long)value);
    			}
    			else if (value instanceof Boolean) {
    				out.value((Boolean)value);
	    		} else if(value instanceof Map){
	    			out.beginObject();
	    			@SuppressWarnings("unchecked")
					Map<Object, Object> op = (Map<Object, Object>) value;
	    			
	    			for(Entry<Object, Object> f : op.entrySet()) {
	    				out.name(f.getKey().toString());
	    				Object v = f.getValue();
	    				if(v instanceof String) 
	    					out.value(v.toString());
	    				else if(v instanceof Long)
	    					out.value((Long)v);
	    				else if (v instanceof Boolean)
	    					out.value((Boolean)v);
	    			}
	    			out.endObject();
	    		}
	    	}	
	    	out.endObject();
	    }

	    @Override
	    public Selector read(JsonReader in) throws IOException {
	        return null;
	    }
	}
}
