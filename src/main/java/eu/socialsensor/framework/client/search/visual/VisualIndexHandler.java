package eu.socialsensor.framework.client.search.visual;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.*;

import eu.socialsensor.framework.client.dao.MediaItemDAO;
import eu.socialsensor.framework.client.dao.impl.MediaItemDAOImpl;
import eu.socialsensor.framework.client.search.visual.JsonResultSet.JsonResult;
import eu.socialsensor.framework.common.domain.MediaItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Client for Visual Indexer.
 *
 * @author Schinas Manos
 * @email manosetro@iti.gr
 */
public class VisualIndexHandler {

    private static double default_threshold = 0.75;
    
    private Logger _logger = Logger.getLogger(VisualIndexHandler.class);
    
    private String webServiceHost;
    private String collectionName;
    private HttpClient httpClient;

    public VisualIndexHandler(String webServiceHost, String collectionName) {
        this.webServiceHost = webServiceHost;
        this.collectionName = collectionName;
        
        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setMaxTotalConnections(100);
		params.setDefaultMaxConnectionsPerHost(20);
		params.setConnectionTimeout(5000);
        cm.setParams(params);
        this.httpClient = new HttpClient(cm);
    }

    public JsonResultSet getSimilarImages(String imageId) {
        return getSimilarImages(imageId, default_threshold);
    }

    /**
     * Get similar images for a specific media item
     *
     * @param imageId
     * @param threshold
     * @return
     */
    public JsonResultSet getSimilarImages(String imageId, double threshold) {

    	JsonResultSet similar = new JsonResultSet();
        PostMethod queryMethod = null;
        String response = null;
        try {

            Part[] parts = {
                new StringPart("id", imageId),
                new StringPart("threshold", String.valueOf(threshold))
            };

            queryMethod = new PostMethod(webServiceHost + "/rest/visual/query_id/" + collectionName);
            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
            int code = httpClient.executeMethod(queryMethod);
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();

                queryMethod.releaseConnection();

                similar = parseResponse(response);
            }
            else {
            	_logger.error("Http returned code: " + code);
            }
        } catch (Exception e) {
        	_logger.error("Exception for ID: " + imageId, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }

    /**
     * Get similar images for a specific media item
     *
     * @param imageId
     * @param threshold
     * @return
     */
    public JsonResultSet getSimilarImages(String imageId, int page, int numResults) {

    	JsonResultSet similar = new JsonResultSet();
        PostMethod queryMethod = null;
        String response = null;
        try {

            Part[] parts = {
                new StringPart("id", imageId),
                new StringPart("threshold", String.valueOf(default_threshold)),
                new StringPart("page", String.valueOf(page)),
                new StringPart("numResults", String.valueOf(numResults))
            };

            queryMethod = new PostMethod(webServiceHost + "/rest/visual/query_id/" + collectionName);
            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
            int code = httpClient.executeMethod(queryMethod);
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();

                queryMethod.releaseConnection();

                similar = parseResponse(response);
            }
            else {
            	_logger.error("Http returned code: " + code);
            }
        } catch (Exception e) {
        	_logger.error("Exception for ID: " + imageId, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }
    
    public JsonResultSet getSimilarImages(URL url) {
    	return getSimilarImages(url, default_threshold);
    }
    
    public JsonResultSet getSimilarImages(URL url, double threshold) {
    	JsonResultSet similar = new JsonResultSet();
        GetMethod queryMethod = null;
        String response = null;
        try {

            queryMethod = new GetMethod(webServiceHost + "/rest/visual/query_url/" + collectionName);   
            queryMethod.setQueryString("url="+url+"&threshold="+threshold);

            int code = httpClient.executeMethod(queryMethod);
            
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();

                similar = parseResponse(response);
                
            }
            else {
            	System.out.println(code);
            	InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();
                System.out.println(response);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        	_logger.error("Exception for URL: " + url, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }

    public JsonResultSet getSimilarImagesAndIndex(String id, URL url) {
    	return getSimilarImagesAndIndex(id, url, default_threshold);
    }
    
    public JsonResultSet getSimilarImagesAndIndex(String id, URL url, double threshold) {
    	JsonResultSet similar = new JsonResultSet();
        PostMethod queryMethod = null;
        String response = null;
        try {
            Part[] parts = {
            	new StringPart("id", id),
                new StringPart("url", url.toString()),
                new StringPart("threshold", String.valueOf(threshold))
            };

            queryMethod = new PostMethod(webServiceHost + "/rest/visual/qindex_url/" + collectionName);
            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
            
            int code = httpClient.executeMethod(queryMethod);
            
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();
                similar = parseResponse(response);
            }
            else {
            	InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();
                System.out.println(code);
                System.out.println(response);
                
            	//return getSimilarImages(id, threshold);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        	_logger.error("Exception for URL: " + url, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }
    
    /**
     * Get similar images by vector
     *
     * @param vector
     * @param threshold
     * @return
     */
    public JsonResultSet getSimilarImagesAndIndex(String id, double[] vector, double threshold) {

    	JsonResultSet similar = new JsonResultSet();

        byte[] vectorInBytes = new byte[8 * vector.length];
        ByteBuffer bbuf = ByteBuffer.wrap(vectorInBytes);
        for (double value : vector) {
            bbuf.putDouble(value);
        }

        PostMethod queryMethod = null;
        String response = null;
        try {
            ByteArrayPartSource source = new ByteArrayPartSource("bytes", vectorInBytes);
            Part[] parts = {
            	new StringPart("id", id),
                new FilePart("vector", source),
                new StringPart("threshold", String.valueOf(threshold))
            };

            queryMethod = new PostMethod(webServiceHost + "/rest/visual/qindex/" + collectionName);
            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
            int code = httpClient.executeMethod(queryMethod);
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();

                similar = parseResponse(response);
            }
        } catch (Exception e) {
        	_logger.error("Exception for vector of length " + vector.length, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }
    
    /**
     * Get similar images by vector
     *
     * @param vector
     * @param threshold
     * @return
     */
    public JsonResultSet getSimilarImages(double[] vector, double threshold) {

    	JsonResultSet similar = new JsonResultSet();

        byte[] vectorInBytes = new byte[8 * vector.length];
        ByteBuffer bbuf = ByteBuffer.wrap(vectorInBytes);
        for (double value : vector) {
            bbuf.putDouble(value);
        }

        PostMethod queryMethod = null;
        String response = null;
        try {
            ByteArrayPartSource source = new ByteArrayPartSource("bytes", vectorInBytes);
            Part[] parts = {
                new FilePart("vector", source),
                new StringPart("threshold", String.valueOf(threshold))
            };

            queryMethod = new PostMethod(webServiceHost + "/rest/visual/query_vector/" + collectionName);
            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
            int code = httpClient.executeMethod(queryMethod);
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
                queryMethod.releaseConnection();

                similar = parseResponse(response);
            }
        } catch (Exception e) {
        	_logger.error("Exception for vector of length " + vector.length, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }

    public boolean index(String id, double[] vector) {
        byte[] vectorInBytes = new byte[8 * vector.length];
        ByteBuffer bbuf = ByteBuffer.wrap(vectorInBytes);
        for (double value : vector) {
            bbuf.putDouble(value);
        }
        
        boolean success = false;
        PostMethod indexMethod = null;
        try {
            ByteArrayPartSource source = new ByteArrayPartSource("bytes", vectorInBytes);
            Part[] parts = {
                new StringPart("id", id),
                new FilePart("vector", source)
            };
            indexMethod = new PostMethod(webServiceHost + "/rest/visual/index/" + collectionName);
            indexMethod.setRequestEntity(new MultipartRequestEntity(parts, indexMethod.getParams()));
            
            int code = httpClient.executeMethod(indexMethod);
            if (code == 200) {
                JsonParser parser = new JsonParser();
                InputStream responseStream = indexMethod.getResponseBodyAsStream();
                String rawJson = IOUtils.toString(responseStream);
                JsonObject o = (JsonObject) parser.parse(rawJson);
                JsonElement e = o.get("success");
                if (e!=null && !e.isJsonNull()) {
                    success = e.getAsBoolean();
                }
                
                if(!success) {
                	_logger.error("Indexing failed: " + rawJson);
                }
            }
            else {
            	_logger.error("Http returned code: " + code);
            }
        } catch (Exception e) {
        	_logger.error("Exception for id: " + id, e);
        	e.printStackTrace();
        } finally {
            if (indexMethod != null) {
                indexMethod.releaseConnection();
            }
        }
        return success;
    }

    public Double[] getVector(String id) {
        GetMethod queryMethod = null;
        String response = null;
        Double[] vector = null;
        try {

            queryMethod = new GetMethod(webServiceHost + "/rest/visual/vector/" + collectionName);   
            queryMethod.setQueryString("id="+id);

            int code = httpClient.executeMethod(queryMethod);
            
            if (code == 200) {
                InputStream inputStream = queryMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();

                Gson gson = new GsonBuilder()
                	.excludeFieldsWithoutExposeAnnotation()
                	.create();

                vector = gson.fromJson(response, Double[].class);

            }
            
            
        } catch (Exception e) {
        	e.printStackTrace();
        	_logger.error("Exception for id: " + id, e);
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        
        return vector;
    }
    
    public String uploadImage(String id, BufferedImage image, String type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageInByte = null;
        try {
            ImageIO.write(image, type, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            return e.getMessage();
        }

        PostMethod uploadMethod = null;
        String response = null;
        try {
            ByteArrayPartSource source = new ByteArrayPartSource(id, imageInByte);
            Part[] parts = {
                new StringPart("id", id),
                new FilePart("photo", source)
            };
            uploadMethod = new PostMethod(webServiceHost + "rest/images/upload");
            uploadMethod.setRequestEntity(new MultipartRequestEntity(parts, uploadMethod.getParams()));
            int code = httpClient.executeMethod(uploadMethod);
            if (code == 200) {
                InputStream inputStream = uploadMethod.getResponseBodyAsStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                response = writer.toString();
            }
        } catch (Exception e) {
        } finally {
            if (uploadMethod != null) {
                uploadMethod.releaseConnection();
            }
        }
        return response;
    }

    private static JsonResultSet parseResponse(String response) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        try {
        	JsonResultSet indexResults = gson.fromJson(response, JsonResultSet.class);
        	if (indexResults == null) {
            	return new JsonResultSet();
        	}
        	 return (indexResults.results==null)?(new JsonResultSet()):indexResults;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.out.println(response);
        	return new JsonResultSet();
        }
       
    }

    public static void main(String[] args) throws IOException {
    	
    	
    	VisualIndexHandler handler = new VisualIndexHandler("http://xxx.xxx.xxx.xxx:8080/VisualIndexService", "Prototype");
    	
    	MediaItemDAO dao = null;
		try {
			dao = new MediaItemDAOImpl("xxx.xxx.xxx.xxx", "Prototype", "MediaItems");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	List<MediaItem> mediaItems = dao.getLastMediaItems(-1);
    	
    	int k = 0;
    	for(MediaItem mediaItem : mediaItems) {
    		String id = mediaItem.getId();
    		String url = mediaItem.getUrl();

    		try {
    			JsonResultSet results = handler.getSimilarImages(id, 0.8);
    			List<JsonResult> list = results.results;
    			if(list.size()>0) {
    				System.out.println(results.toJSON());
    				System.out.println("============================");
    			}
    			
    			results = handler.getSimilarImages(new URL(url));
    			list = results.results;
    			if(list.size()>0) {
    				System.out.println(results.toJSON());
    				System.out.println("============================");
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	System.out.println("Total items with results: " + k);
    	
    }
   
}
