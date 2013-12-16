package ssu.satp.nopaper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ServerConnection extends AsyncTask<String, Void, Void> implements
		Parcelable {

	private JSONArray jsonarray = null;
	private ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	private String id;
	private String code;
	private String value;
	private String st_id;
	private boolean getEntity = false;

	public void setSt_id(String st_id) {
		this.st_id = st_id;
		postParameters.add(new BasicNameValuePair("st_id", st_id));
	}

	public void setId(String id) {
		this.id = id;
		postParameters.add(new BasicNameValuePair("id", id));
	}

	public void setCode(String code) {
		this.code = code;
		postParameters.add(new BasicNameValuePair("code", code));
	}

	public void setValue(String value) {
		this.value = value;
		postParameters.add(new BasicNameValuePair("value", value));
	}
	
	
	public String getCode() {
		return code;
	}
	public String getSt() {
		return st_id;
	}
	
	
	public String getValue() {
		return value;
	}

	public boolean isGetEntity() {
		return getEntity;
	}

	public JSONArray getJsonarray() {
		return jsonarray;
	}

	public ServerConnection() {
		super();
	}

	protected Void doInBackground(String... mUrl) {
		Log.d("back", "in");
		getEntity = false;
		HttpResponse response = null;
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		HttpPost post = new HttpPost(mUrl[0]);
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			response = client.execute(post);
			String res = EntityUtils.toString(response.getEntity());
			Log.d("response", res);
			try {
				jsonarray = new JSONArray(res);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			getEntity = true;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ServerConnection(Parcel source) {
		id = source.readString();
		code = source.readString();
		value = source.readString();
		st_id = source.readString();

		postParameters.add(new BasicNameValuePair("id", id));
		postParameters.add(new BasicNameValuePair("code", code));
		postParameters.add(new BasicNameValuePair("value", value));
		postParameters.add(new BasicNameValuePair("st_id", st_id));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(code);
		dest.writeString(value);
		dest.writeString(st_id);
	}

	public static final Parcelable.Creator<ServerConnection> CREATOR = new Creator<ServerConnection>() {
		@Override
		public ServerConnection createFromParcel(Parcel source) {
			return new ServerConnection(source);
		}

		@Override
		public ServerConnection[] newArray(int size) {
			return new ServerConnection[size];
		}
	};
}
