package au.com.touchline.basicnfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@CapacitorPlugin(name = "BasicNFC")
public class BasicNFCPlugin extends Plugin {
	private BasicNFC implementation = new BasicNFC();
	
	@PluginMethod
	public void echo(PluginCall call) {
		String value = call.getString("value");
		
		JSObject ret = new JSObject();
		ret.put("value", implementation.echo(value));
		call.resolve(ret);
	}
	
	// Method to process NFC messages from intents
	public void processNfcMessage(NdefMessage ndefMessage, String tagID) {
		if (ndefMessage != null) {
			NdefRecord[] records = ndefMessage.getRecords();
			JSONArray recordsArray = new JSONArray();
			
			for (NdefRecord record : records) {
				try {
					JSONObject recordJson = new JSONObject();
					recordJson.put("tnf", record.getTnf());
					recordJson.put("type", android.util.Base64.encodeToString(record.getType(), android.util.Base64.NO_WRAP));
					recordJson.put("id", android.util.Base64.encodeToString(record.getId(), android.util.Base64.NO_WRAP));
					recordJson.put("payload", android.util.Base64.encodeToString(record.getPayload(), android.util.Base64.NO_WRAP));
					recordsArray.put(recordJson);
				} catch (JSONException e) {
					Log.e("Capacitor", "Error converting record to JSON", e);
				}
			}
			
			JSObject ret = new JSObject();
			ret.put("records", recordsArray.toString());
			ret.put("tagID", tagID);
			
			// Notify listeners with the JSON data
			notifyListeners("nfcRawDataReceived", ret);
		}
	}
}
