/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author Debasis
 */
public class JSON_MAIN {

    private static final String filePath = "input-message2.jsn";
    public static void main(String[] args) {

		try {
			// read the json file
			FileReader reader = new FileReader(filePath);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

                        JSONObject structure_header = (JSONObject) jsonObject.get("header");
			System.out.println("Order id: " + structure_header.get("order id"));
                        System.out.println("Net Amount: " + structure_header.get("netAmount"));
                        
                        JSONObject structure_customer = (JSONObject) structure_header.get("customer");
			System.out.println("user Name: " + structure_customer.get("userName"));
                        System.out.println("First Name: " + structure_customer.get("firstName"));
                        
			// get an array from the JSON object
			JSONArray orderItem= (JSONArray) jsonObject.get("orderItem");
			
			// take the elements of the json array
			for(int i=0; i<orderItem.size(); i++){
				System.out.println("The " + i + " element of the array: "+orderItem.get(i));
			}
			Iterator i = orderItem.iterator();

			// take each value from the json array separately
			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				System.out.println("Product Id: "+ innerObj.get("productId") + 
						" Amount " + innerObj.get("price"));
			}
			// handle a structure into the json object
			

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

	}
}
