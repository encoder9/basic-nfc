import Capacitor
import CoreNFC

@objc(BasicNFCPlugin)
public class BasicNFCPlugin: CAPPlugin, NFCNDEFReaderSessionDelegate {
	private var nfcSession: NFCNDEFReaderSession?
	private var currentCall: CAPPluginCall?
	private let timeout: TimeInterval = 5.0 // 5 seconds timeout
	
	@objc func echo(_ call: CAPPluginCall) {
		guard let value = call.getString("value") else {
			call.reject("Value cannot be null")
			return
		}
		
		let result = ["value": value]
		call.resolve(result)
	}
	
	@objc func writeNFC(_ call: CAPPluginCall) {
		guard let message = call.getString("message") else {
			call.reject("Message cannot be null")
			return
		}
		
		guard NFCNDEFReaderSession.readingAvailable else {
			call.reject("NFC is not supported on this device")
			return
		}
		
		currentCall = call
		nfcSession = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: true)
		nfcSession?.alertMessage = "Hold your iPhone near an NFC tag to write data."
		nfcSession?.begin()
		
		// Timeout handling
		DispatchQueue.main.asyncAfter(deadline: .now() + timeout) { [weak self] in
			if let session = self?.nfcSession, session.isReady {
				session.invalidate(errorMessage: "Timeout: No NFC tag detected")
				self?.currentCall?.reject("Timeout: No NFC tag detected")
				self?.currentCall = nil
			}
		}
	}
	
	@objc func scanNFC(_ call: CAPPluginCall) {
		let simulatePayload = call.getString("simulatePayload") ?? ""
		
		guard NFCNDEFReaderSession.readingAvailable else {
			if !simulatePayload.isEmpty {
				// Simulate NFC scan
				let result = self.simulateNFCScan(payload: simulatePayload)
				call.resolve(result)
			} else {
				call.reject("NFC is not supported on this device")
			}
			return
		}
		
		currentCall = call
		nfcSession = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: true)
		nfcSession?.alertMessage = "Hold your iPhone near an NFC tag to scan."
		nfcSession?.begin()
		
		// Timeout handling
		DispatchQueue.main.asyncAfter(deadline: .now() + timeout) { [weak self] in
			if let session = self?.nfcSession, session.isReady {
				if !simulatePayload.isEmpty {
					let result = self?.simulateNFCScan(payload: simulatePayload) ?? [:]
					call.resolve(result)
				} else {
					session.invalidate(errorMessage: "Timeout: No NFC tag detected")
					self?.currentCall?.reject("timeout")
				}
				self?.currentCall = nil
			}
		}
	}
	
	// MARK: - NFCNDEFReaderSessionDelegate
	
	public func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
		if let call = currentCall {
			call.reject("NFC session error: \(error.localizedDescription)")
			currentCall = nil
		}
	}
	
	public func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFMessages messages: [NFCNDEFMessage]) {
		guard let call = currentCall else { return }
		
		if call.callbackId == "writeNFC" {
			// Writing operation
			guard let message = call.getString("message"),
				let ndefMessage = createNDEFMessage(from: message) else {
				call.reject("Invalid message format")
				return
			}
			
			session.connect(to: messages[0].records[0].tag!) { error in
				if let error = error {
					call.reject("Failed to connect to tag: \(error.localizedDescription)")
					return
				}
				
				let tag = messages[0].records[0].tag as! NFCNDEFTag
				tag.writeNDEF(ndefMessage) { error in
					if let error = error {
						call.reject("Failed to write to tag: \(error.localizedDescription)")
					} else {
						call.resolve(["result": "Data written to NFC tag"])
					}
					self.currentCall = nil
				}
			}
		} else {
			// Scanning operation
			let result = convertNDEFMessagesToJSON(messages: messages)
			call.resolve(result)
			currentCall = nil
		}
	}
	
	// MARK: - Helper Methods
	
	private func createNDEFMessage(from text: String) -> NFCNDEFMessage? {
		guard let textRecord = NFCNDEFRecord(payload: text.data(using: .utf8) ?? Data(),
										typeNameFormat: .nfcWellKnown,
										type: "T".data(using: .utf8) ?? Data()) else {
			return nil
		}
		return NFCNDEFMessage(records: [textRecord])
	}
	
	private func convertNDEFMessagesToJSON(messages: [NFCNDEFMessage]) -> [String: Any] {
		var recordsArray: [[String: Any]] = []
		
		for message in messages {
			for record in message.records {
				let recordDict: [String: Any] = [
					"tnf": record.typeNameFormat.rawValue,
					"type": record.type.base64EncodedString(),
					"id": record.identifier.base64EncodedString(),
					"payload": record.payload.base64EncodedString()
				]
				recordsArray.append(recordDict)
			}
		}
		
		return [
			"messages": recordsArray,
			"tagID": messages.first?.records.first?.identifier.base64EncodedString() ?? ""
		]
	}
	
	private func simulateNFCScan(payload: String) -> [String: Any] {
		guard let textRecord = NFCNDEFRecord(payload: payload.data(using: .utf8) ?? Data(),
										typeNameFormat: .nfcWellKnown,
										type: "T".data(using: .utf8) ?? Data()),
			let fakeMessage = NFCNDEFMessage(records: [textRecord]) else {
			return [:]
		}
		
		return convertNDEFMessagesToJSON(messages: [fakeMessage])
	}
}
