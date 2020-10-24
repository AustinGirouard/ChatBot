package com.mycompany.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*
 * This class is used for reading weather information
 * from the WeatherAPI provided by openweathermap.org
 */

public class WeatherAPI 
{
	/* This function parses string content into a Json object
	 * Parameters: String containing content from an API URL
	 * Return: JsonObject containing the Json formatted version of the content
	 */
	public JsonObject JsonParse(String content) 
	{
		// Parse API string content into json object
		JsonObject jsonObj = new JsonParser().parse(content).getAsJsonObject();
		return jsonObj;
	}
	
	
	/* This function tests if a zip or city name is valid.
	 * Parameters: String - zip or name of city
	 * Return: boolean - if the zip or city is valid 
	 */
	public boolean testZipOrCity(String zipOrCity)
	{
		try
		{
			// Attempt to set up BufferedReader to read API content from URL
			// If this errors, the URL is invalid
			new BufferedReader(new InputStreamReader(setupWeatherURLConnection(zipOrCity).getInputStream()));
			
			return true;
		}
		
		// If the URL connection is not able to be made, the URL must be invalid. Return false
		catch(Exception e)
		{
			return false;
		}
		
		// Return true if the connection was made
	}
	
	
	/* This function returns the weather for the given zipcode or city
	 * Parameters: String - zip or name of city
	 */
	public String[] getWeather(String zipOrCity) throws IOException
	{
		// Defining variables
		HttpURLConnection connect;
		String content;
		String[] cityInfo = new String[4];
		
		// Connect to the weather API for the zipcode/city name
		connect = setupWeatherURLConnection(zipOrCity);
		
		// Get string content from the URL Connection
		content = getStringContent(connect);
		
		// Convert string content into a Json object
		JsonObject jsonObj = JsonParse(content);
		
		// [0] = name of city
		cityInfo[0] = getNameOfCity(jsonObj);
		
		// [1] = temperature in city
		cityInfo[1] = getTempOfCity(jsonObj);
		
		// [2] = "feels like" temperature in city
		cityInfo[2] = getFeelsLikeTempOfCity(jsonObj);
		
		// [3] = weather conditions in city
		cityInfo[3] = getWeatherCondOfCity(jsonObj);
		
		return cityInfo;
	}
	
	
	/* This function sets up an HttpURLConnection to the API for the city
	 * Parameters: String - zip or name of city
	 * Return: HttpURLConnection that is connected to the API for the city
	 */
	public HttpURLConnection setupWeatherURLConnection(String zipOrCity) throws IOException
	{
		String apiURL;
		
		// Check if the input is a zipcode
		if(zipOrCity.matches("\\d*"))
		{
			apiURL = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipOrCity + ",us&appid=263be66c201229f7e59da5302a71485b";
		}
		
		// If not a zipcode, the input is the name of a city
		else
		{
			zipOrCity = zipOrCity.trim().replaceAll(" ", "+");
			apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + zipOrCity + ",us&appid=263be66c201229f7e59da5302a71485b";
		}
		
		// Establishes connection to weatherAPI URL
		URL url = new URL(apiURL);
		HttpURLConnection connect = (HttpURLConnection) url.openConnection();
		connect.setRequestMethod("GET");
		
		return connect;
	}
	
	
	/* This function reads content from an API URL into a string
	 * Parameters: HttpURLConnection object that is connected to the API URL
	 * Return: String that contains the contents from the API
	 */
	public String getStringContent(HttpURLConnection connect) throws IOException
	{
		// Sets up a BufferedReader "in" to read in input from URL
		BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
		String inputJSON;
		String content = new String();
		
		// Reads in all info from BufferedReader "in"
		while((inputJSON = in.readLine()) != null)
		{
			content += inputJSON;
		}
		
		// Closes connections
		in.close();
		connect.disconnect();
		
		return content;
	}
	
	
	/* This function returns the name of a city given the Json object
	 * of the API.
	 * Parameters: JsonObject
	 * Return: String name of city
	 */
	public String getNameOfCity(JsonObject jsonObj)
	{
		return jsonObj.get("name").getAsString();
	}
	
	
	/* This function returns the temperature of a city in fahrenheit.
	 * Parameters: JsonObject
	 * Return: String temperature of city
	 */
	public String getTempOfCity(JsonObject jsonObj)
	{
		// Convert Kelvin temp to Fahrenheit
		double temp = jsonObj.getAsJsonObject("main").get("temp").getAsDouble();
		temp = (temp - 273.15) * (9.0 / 5.0) + 32;
		
		// Form to 2 places after decimal
		DecimalFormat d = new DecimalFormat("##.##");
		
		return d.format(temp);
	}
	
	
	/* This function returns the "feels like" temperature of a city in fahrenheit.
	 * Parameters: JsonObject
	 * Return: String "feels like" temperature of city
	 */
	public String getFeelsLikeTempOfCity(JsonObject jsonObj)
	{
		// Convert Kelvin temp to Fahrenheit
		double temp = jsonObj.getAsJsonObject("main").get("feels_like").getAsDouble();
		temp = (temp - 273.15) * (9.0 / 5.0) + 32;
		
		// Form to 2 places after decimal
		DecimalFormat d = new DecimalFormat("##.##");
		
		return d.format(temp);
	}
	
	
	/* This function returns the weather conditions of a city
	 * Parameters: JsonObject
	 * Return: String weather conditions of city
	 */
	public String getWeatherCondOfCity(JsonObject jsonObj)
	{
		return ((JsonObject) jsonObj.getAsJsonArray("weather").get(0)).get("description").getAsString();
	}
}