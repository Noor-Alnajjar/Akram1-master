package com.libraries.dataparser;

import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.models.DataResponse;
import com.libraries.ssl.MGHTTPClient;
import com.models.Interest;
import com.models.Merchant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataParser {
	
	public InputStream retrieveStream(String url,Context context) {
        try {
            HttpClient httpClient = MGHTTPClient.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
//            DefaultHttpClient  httpClient = new DefaultHttpClient();
//            HttpPost httpPost = MGHTTPClient.getNonHttpsPost(url);
			UserSession userSession = UserAccessSession.getInstance(context).getUserSession();
			if(userSession.getId()!=null)
				httpPost.setHeader("x-api-key",userSession.getApikey());
            HttpResponse httpResponse = httpClient.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Status Code", "Error " + statusCode + " for URL " + url);
                return null;
             }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            InputStream stream = getResponseEntity.getContent();
           return stream;
        } 
        catch (IOException e) {
        	
           Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }
        return null;
	}

	public DataResponse getData(String url,Context context)	{
		InputStream source = retrieveStream(url,context);
		if(source == null)
			return null;

		JsonFactory f = new JsonFactory();
        f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

        ObjectMapper mapper = new ObjectMapper(f);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		DataResponse data = new DataResponse();

		try  {
			Log.e("sourse",source.toString());
			data = mapper.readValue(source, DataResponse.class);
			Log.e("sourse1",source.toString());
		}
		catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

//	public UserInfo getDataUser(String url,Context context)	{
//		InputStream source = retrieveStream(url,context);
//		if(source == null)
//			return null;
//
//		JsonFactory f = new JsonFactory();
//		f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
//
//		ObjectMapper mapper = new ObjectMapper(f);
//		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//		UserInfo data = new UserInfo();
//
//		try  {
//			Log.e("sourse",source.toString());
//			data = mapper.readValue(source, UserInfo.class);
//			Log.e("sourse1",source.toString());
//		}
//		catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return data;
//	}

	public static Merchant getJSONFromUrlWithPostRequestUser(String url, List<NameValuePair> params,Context context) {


		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			UserSession userSession = UserAccessSession.getInstance(context).getUserSession();
			if(userSession.getId()!=null)
				httpPost.setHeader("x-api-key",userSession.getApikey());
			if(params != null)
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("Status Code", "Error " + statusCode + " for URL " + url);
				return null;
			}else if(statusCode == HttpStatus.SC_UNAUTHORIZED){
                Log.w("Status Code", "Error " + statusCode + " for URL " + url);
                return null;
            }

			HttpEntity getResponseEntity = httpResponse.getEntity();

			String source = "";
			source= new String(EntityUtils.toString(getResponseEntity));
			Log.i("GET RESPONSE", source);
			JSONObject jsonObj = new JSONObject(source);
			JSONObject results = (JSONObject) jsonObj.get("marchant");
			source =  results.toString();

			JsonFactory f = new JsonFactory();
			f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
			ObjectMapper mapper = new ObjectMapper(f);
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			Merchant data = null;
			try  {
				data = mapper.readValue(source, Merchant.class);
				return data;
			}
			catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


    public static Interest getJSONFromUrlWithPostCategorys(String url, List<NameValuePair> params, Context context) {


		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			UserSession userSession = UserAccessSession.getInstance(context).getUserSession();
			if(userSession.getId()!=null)
				httpPost.setHeader("x-api-key",userSession.getApikey());
			if(params != null)
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("Status Code", "Error " + statusCode + " for URL " + url);
				return null;
			}

			HttpEntity getResponseEntity = httpResponse.getEntity();

			String source = "";
			source= new String(EntityUtils.toString(getResponseEntity));
			Log.e("GET RESPONSE", source);
			//Log.e("sourese",source.toString());
			JsonFactory f = new JsonFactory();
			f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
			ObjectMapper mapper = new ObjectMapper(f);
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			Interest data = null;
			try  {
				data = mapper.readValue(source, Interest.class);
				return data;
			}
			catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

	public static DataResponse getJSONFromUrlWithPostRequest(String url, List<NameValuePair> params,Context context) {

		
		try {
			HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
			UserSession userSession = UserAccessSession.getInstance(context).getUserSession();
			if(userSession != null)
				if( userSession.getId()!=null)
				httpPost.setHeader("x-api-key",userSession.getApikey());
            if(params != null)
            	httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Status Code", "Error " + statusCode + " for URL " + url);

                return null;
			}

            HttpEntity getResponseEntity = httpResponse.getEntity();

            String source = "";
			source= new String(EntityUtils.toString(getResponseEntity));
			Log.e("GET RESPONSE", source);
			//Log.e("sourese",source.toString());
            JsonFactory f = new JsonFactory();
            f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            ObjectMapper mapper = new ObjectMapper(f);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            DataResponse data = null;
    		try  {
    			data = mapper.readValue(source, DataResponse.class);
    			return data;
    		}
    		catch (JsonParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		catch (JsonMappingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
		catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		catch (ClientProtocolException e) {
            e.printStackTrace();
        }
		catch (IOException e) {
            e.printStackTrace();
        }
		return null;
    }

	public static DataResponse uploadFileWithParams(String url, ArrayList<NameValuePair> params, Map<String, File> files,Context context) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			UserSession userSession = UserAccessSession.getInstance(context).getUserSession();
			if(userSession != null)
				if( userSession.getId()!=null)
					httppost.setHeader("x-api-key",userSession.getApikey());

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if(files != null && files.size() > 0) {
				for (Map.Entry<String, File> entry : files.entrySet()) {
					String key = entry.getKey();
					File file = entry.getValue();
					FileBody fileBody = new FileBody(file);
					reqEntity.addPart(key, fileBody);
				}
			}

			if(params != null) {
				Charset charset = Charset.forName("UTF-8");
				for(NameValuePair pair : params) {
					StringBody stringB = new StringBody(pair.getValue(), charset);
					reqEntity.addPart(pair.getName(), stringB);
				}
			}

			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity getResponseEntity = response.getEntity();

			String source = "";
			source= new String(EntityUtils.toString(getResponseEntity));
			Log.e("GET RESPONSE", source);
			JsonFactory f = new JsonFactory();
			f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

			ObjectMapper mapper = new ObjectMapper(f);
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

			DataResponse data = null;
			try  {
				data = mapper.readValue(source, DataResponse.class);
				return data;
			}
			catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("k1",e.toString());
			}
			catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("k2",e.toString());
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("k3",e.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("k4",e.toString());
		}
		return null;
	}
}
