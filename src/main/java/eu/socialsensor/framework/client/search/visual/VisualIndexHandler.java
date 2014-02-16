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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.socialsensor.framework.client.dao.MediaItemDAO;
import eu.socialsensor.framework.client.dao.impl.MediaItemDAOImpl;
import eu.socialsensor.framework.client.search.visual.JsonResultSet.JsonResult;
import eu.socialsensor.framework.common.domain.MediaItem;

/**
 * Client for Visual Indexer.
 *
 * @author Schinas Manos
 * @email manosetro@iti.gr
 */
public class VisualIndexHandler {

    private static double default_threshold = 0.75;
    
    private String webServiceHost;
    private String collectionName;
    private HttpClient httpClient;

    public VisualIndexHandler(String webServiceHost, String collectionName) {
        this.webServiceHost = webServiceHost;
        this.collectionName = collectionName;
        this.httpClient = new HttpClient();
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
                //System.out.println(response);
                queryMethod.releaseConnection();

                similar = parseResponse(response);
            }
            else {
            	System.out.println(code);
            }
        } catch (Exception e) {
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
            response = null;
        } finally {
            if (queryMethod != null) {
                queryMethod.releaseConnection();
            }
        }
        return similar;
    }
    
//    public NearestImage[] getSimilarImages(URL url, double threshold) {
//        NearestImage[] similar = new NearestImage[0];
//        PostMethod queryMethod = null;
//        String response = null;
//        try {
//
//            Part[] parts = {
//                new StringPart("url", url.toString()),
//                new StringPart("threshold", String.valueOf(threshold))
//            };
//
//            queryMethod = new PostMethod(webServiceHost + "/rest/visual/query_url/" + collectionName);
//            queryMethod.setRequestEntity(new MultipartRequestEntity(parts, queryMethod.getParams()));
//            int code = httpClient.executeMethod(queryMethod);
//            
//            if (code == 200) {
//                InputStream inputStream = queryMethod.getResponseBodyAsStream();
//                StringWriter writer = new StringWriter();
//                IOUtils.copy(inputStream, writer);
//                response = writer.toString();
//                queryMethod.releaseConnection();
//
//                similar = parseResponse(response);
//                
//            }
//            else {
//            	System.out.println(code);
//            	InputStream inputStream = queryMethod.getResponseBodyAsStream();
//                StringWriter writer = new StringWriter();
//                IOUtils.copy(inputStream, writer);
//                response = writer.toString();
//                queryMethod.releaseConnection();
//                System.out.println(response);
//            }
//            
//        } catch (Exception e) {
//        	e.printStackTrace();
//            response = null;
//        } finally {
//            if (queryMethod != null) {
//                queryMethod.releaseConnection();
//            }
//        }
//        return similar;
//    }
    
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
                success = true;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (indexMethod != null) {
                indexMethod.releaseConnection();
            }
        }
        return success;
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
    	
    	
    	VisualIndexHandler handler = new VisualIndexHandler("http://160.40.50.207:8080/VisualIndex", "prototype");
    	
    	MediaItemDAO dao = new MediaItemDAOImpl("160.40.50.207", "Streams", "MediaItemsFromWP_boilerpipe");
    	List<MediaItem> mediaItems = dao.getLastMediaItems(-1);
    	
    	int k = 0;
    	for(MediaItem mediaItem : mediaItems) {
    		String id = mediaItem.getId();
    		String url = mediaItem.getUrl();

    		try {
    			//handler.getSimilarImagesAndIndex(id, new URL(url));
    			JsonResultSet results = handler.getSimilarImages(id, 0.8);
    			
    			List<JsonResult> list = results.results;
    			if(list.size()>0) {
    				System.out.println(results.toJSON());
    				k++;
    				//for(JsonResult nn : list) 
    				//	System.out.println(nn.toString());
    			
    				System.out.println("============================");
    			}
    			
    			results = handler.getSimilarImages(new URL(url));
    			list = results.results;
    			if(list.size()>0) {
    				System.out.println(results.toJSON());
    				k++;
    				//for(JsonResult nn : list) 
    				//	System.out.println(nn.toString());
    			
    				System.out.println("============================");
    			}
    			
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	System.out.println("Total items with results: " + k);
    	
    }
   
}
