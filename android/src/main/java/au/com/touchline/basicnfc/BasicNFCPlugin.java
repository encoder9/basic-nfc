package au.com.touchline.basicnfc;

import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Handler;
import android.os.Looper;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.NfcAdapter;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.Ndef;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
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
					Log.e("NFC", "Error converting record to JSON", e);
				}
			}
			
			JSObject ret = new JSObject();
			ret.put("records", recordsArray.toString());
			ret.put("tagID", tagID);
			
			// Notify listeners with the JSON data
			notifyListeners("nfcRawDataReceived", ret);
		}
	}
	
	private static final int TIMEOUT = 5000; // 5 seconds timeout
	
	@PluginMethod
	public void writeNFC(PluginCall call) {
		String message = call.getString("message"); // Assuming you're passing a string to write
		
		if (message == null) {
			call.reject("Message cannot be null");
			return;
		}
		
		if (NfcAdapter.getDefaultAdapter(getContext()) != null) {
			final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
			final AtomicBoolean writeSuccess = new AtomicBoolean(false);
			
			NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
				@Override
				public void onTagDiscovered(Tag tag) {
					writeNdefMessage(tag, message, call);
					writeSuccess.set(true); // Signal that the write was successful
				}
			};
			
			// Enable reader mode for writing NFC
			nfcAdapter.enableReaderMode(getActivity(), readerCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B, null);
			
			// Schedule to disable reader mode after timeout
			final Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					nfcAdapter.disableReaderMode(getActivity());
					
					if (!writeSuccess.get()) { // If the write hasn't happened yet
						call.reject("Timeout: No NFC tag detected");
					}
				}
			}, TIMEOUT);
		} else {
			call.reject("NFC is not supported on this device.");
		}
	}
	
	private void writeNdefMessage(Tag tag, String message, PluginCall call) {
		Ndef ndef = Ndef.get(tag);
		
		try {
			if (ndef == null) {
				// Let's try to format the tag if it's not NDEF compatible
				NdefFormatable format = NdefFormatable.get(tag);
				
				if (format != null) {
					format.connect();
					format.format(createNdefMessage(message));
					format.close();
					
					JSObject ret = new JSObject();
					ret.put("result", "NFC tag formatted and data written");
					call.resolve(ret);
					return;
				} else {
					call.reject("Tag is not NDEF capable and cannot be formatted.");
					return;
				}
			}
			
			ndef.connect();
			
			if (ndef.isWritable()) {
				ndef.writeNdefMessage(createNdefMessage(message));
				JSObject ret = new JSObject();
				ret.put("result", "Data written to NFC tag");
				call.resolve(ret);
			} else {
				call.reject("Tag is read-only");
			}
			
			ndef.close();
		} catch (Exception e) {
			call.reject("Error writing to NFC tag: " + e.getMessage());
		}
	}
	
	private NdefMessage createNdefMessage(String message) {
		byte[] langBytes = "en".getBytes(Charset.forName("US-ASCII"));
		byte[] textBytes = message.getBytes(Charset.forName("UTF-8"));
		byte[] payload = new byte[1 + langBytes.length + textBytes.length];
		payload[0] = (byte) langBytes.length;
		
		System.arraycopy(langBytes, 0, payload, 1, langBytes.length);
		System.arraycopy(textBytes, 0, payload, 1 + langBytes.length, textBytes.length);
		
		NdefRecord rtdTextRecord = new NdefRecord(
			NdefRecord.TNF_WELL_KNOWN, 
			NdefRecord.RTD_TEXT, 
			new byte[0], 
			payload
		);
		
		return new NdefMessage(new NdefRecord[]{rtdTextRecord});
	}
	
	@PluginMethod
	public void scanNFC(PluginCall call) {
		// Get the simulatePayload parameter from the call, defaulting to empty string if not provided
		String simulatePayload = call.getString("simulatePayload", "");
		
		if (NfcAdapter.getDefaultAdapter(getContext()) != null) {
			final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
			final AtomicBoolean scanSuccess = new AtomicBoolean(false);
			
			NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
				@Override
				public void onTagDiscovered(Tag tag) {
					Ndef ndef = Ndef.get(tag);
					
					if (ndef != null) {
						try {
							ndef.connect();
							NdefMessage ndefMessage = ndef.getNdefMessage();
							ndef.close();
							
							if (ndefMessage != null) {
								// Convert the NDEF message to JSON and send back
								JSObject ret = new JSObject();
								ret.put("messages", convertNdefMessageToJson(ndefMessage));
								ret.put("tagID", android.util.Base64.encodeToString(tag.getId(), android.util.Base64.NO_WRAP));
								call.resolve(ret);
							} else {
								call.reject("Error: Tag has no data");
							}
							
							scanSuccess.set(true); // Signal that scanning was successful
						} catch (Exception e) {
							call.reject("Error: " + e.getMessage());
						}
					} else {
						call.reject("Error: Tag does not support NDEF");
					}
					
					// Disable reader mode after processing the tag
					nfcAdapter.disableReaderMode(getActivity());
				}
			};
			
			// Enable reader mode for scanning NFC
			nfcAdapter.enableReaderMode(getActivity(), readerCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B, null);
			
			// Set a timeout if you want to end scanning after a certain period
			final Handler handler = new Handler(Looper.getMainLooper());
			
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					nfcAdapter.disableReaderMode(getActivity());
					
					if (!scanSuccess.get()) { // If no tag was detected
						if (!simulatePayload.isEmpty()) {
							try {
								// Create a fake NDEF record with the simulatePayload
								NdefRecord textRecord = NdefRecord.createTextRecord("en", simulatePayload);
								NdefMessage fakeMessage = new NdefMessage(new NdefRecord[]{textRecord});
								
								// Simulate a successful scan
								JSObject ret = new JSObject();
								ret.put("messages", convertNdefMessageToJson(fakeMessage));
								// Simulate a fake tag ID (you can customize this as needed)
								String fakeTagId = android.util.Base64.encodeToString("SIMULATED_TAG".getBytes(), android.util.Base64.NO_WRAP);
								ret.put("tagID", fakeTagId);
								call.resolve(ret);
							} catch (Exception e) {
								call.reject("Error simulating payload: " + e.getMessage());
							}
						} else {
							call.reject("timeout");
						}
					}
				}
			}, TIMEOUT); // Using the existing TIMEOUT constant
		} else {
			if (!simulatePayload.isEmpty()) {
				try {
					// Create a fake NDEF record with the simulatePayload
					NdefRecord textRecord = NdefRecord.createTextRecord("en", simulatePayload);
					NdefMessage fakeMessage = new NdefMessage(new NdefRecord[]{textRecord});
					
					// Simulate a successful scan
					JSObject ret = new JSObject();
					ret.put("messages", convertNdefMessageToJson(fakeMessage));
					// Simulate a fake tag ID (you can customize this as needed)
					String fakeTagId = android.util.Base64.encodeToString("SIMULATED_TAG".getBytes(), android.util.Base64.NO_WRAP);
					ret.put("tagID", fakeTagId);
					call.resolve(ret);
				} catch (Exception e) {
					call.reject("Error simulating payload: " + e.getMessage());
				}
			} else {
				call.reject("Error: NFC is not supported on this device.: " + simulatePayload);
			}
		}
	}
	
	private String convertNdefMessageToJson(NdefMessage ndefMessage) {
		JSONArray recordsArray = new JSONArray();
		
		for (NdefRecord record : ndefMessage.getRecords()) {
			try {
				JSONObject recordJson = new JSONObject();
				recordJson.put("tnf", record.getTnf());
				recordJson.put("type", android.util.Base64.encodeToString(record.getType(), android.util.Base64.NO_WRAP));
				recordJson.put("id", android.util.Base64.encodeToString(record.getId(), android.util.Base64.NO_WRAP));
				recordJson.put("payload", android.util.Base64.encodeToString(record.getPayload(), android.util.Base64.NO_WRAP));
				recordsArray.put(recordJson);
			} catch (JSONException e) {
				Log.e("NFC", "Error converting record to JSON", e);
			}
		}
		
		return recordsArray.toString();
	}
}
