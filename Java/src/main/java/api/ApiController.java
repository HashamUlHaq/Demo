package api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
@RequestMapping("/json")
public class ApiController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private String filename = "./datafile.ser";
	private ArrayList<Message> data;

	
	public boolean ReadFromFile() {
		try {

			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			data = (ArrayList<Message>) objectIn.readObject();
			objectIn.close();
			fileIn.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void CreateDummyData() {
		data = new ArrayList<Message>();
		for ( int i = 0 ; i < 10; i++) {
			data.add(
					new Message(i, "Dummy Subject "+ Integer.toString(i), "Dummy Message body "+Integer.toString(i))
				);
		}
	}
	
	@RequestMapping(value = "/corpusData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> corpusData() {

		if (data == null) {
			boolean response = ReadFromFile();
			if (response == false) {
				CreateDummyData();
			}
		}
		return new ResponseEntity<Object>(data, HttpStatus.OK);
		
	}

	@RequestMapping(value = "/corpusUpdate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")

	// Note: Manually parsing json in this function to check for any exception like
	// missing key-value pair

	public ResponseEntity<Object> corpusUpdate(@RequestBody String input // Message input
	) {

		boolean updated = false;

		if (data == null) {
			boolean response = ReadFromFile();
			if (response == false) {
				CreateDummyData();
			}
		}

		JSONObject responseMessage = new JSONObject();
		List<Message> responselist = new ArrayList<Message>();
		JSONObject json;
		JSONParser parser = new JSONParser();
		Message request;
		try {
			json = (JSONObject) parser.parse(input);
			request = new Message((int) json.getAsNumber("message_id"), json.getAsString("subject"),
					json.getAsString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage.put("response", "invalid JSON");
			return new ResponseEntity<Object>(responseMessage, HttpStatus.OK);
		}

		// Note: For optimization, store and query from a db.
		
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).MessageID == request.MessageID) {
				data.set(i, request);
				responseMessage.put("response", "Record Updated");
				updated = true;
			}
		}
		
		if (updated == false){
			data.add(request);
			responseMessage.put("response", "New Record Added");
		}

		try {

			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(data);
			objectOut.flush();
			objectOut.close();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<Object>(responseMessage, HttpStatus.OK);

	}

}