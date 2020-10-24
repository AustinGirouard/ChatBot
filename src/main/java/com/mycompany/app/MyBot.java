package com.mycompany.app;

import java.io.IOException;
import java.util.Random;

import org.jibble.pircbot.PircBot;

public class MyBot extends PircBot// implements SpotifyAPI, WeatherAPI
{
	public String accessToken;
	public String versionNumber = "1.0.0";
	public String versionDate = "Oct. 23, 2020";
	
	public MyBot()
	{
		String[] names = {"MyBot", "BeepBot", "BeepBoopBot", "PircBot", "CoolBot", "BOT", "BeepleBot", "BotBot"};
		this.setName(names[Math.abs(new Random().nextInt()) % 8]);
	}
	
	
	public void onMessage(String channel, String sender, 
				String login, String hostname, String message)
	{
		// Commands list
		if(message.contains("!commands"))
		{
			sendMessage(channel, "List of commands");
			sendMessage(channel, "!version");
			sendMessage(channel, "1) Disconnect");
			sendMessage(channel, "2) Say hi!");
			sendMessage(channel, "3) Say \"weather\" followed by a zipcode/city name to get the weather! (Ex. weather Richardson / weather 75080)");
			sendMessage(channel, "4) Say \"Spotify New Releases\" followed by the number of releases! MAX: 10 (Ex. Spotify New Releases 5)");
		}
		
		if(message.toLowerCase().equals("!version") && !sender.toLowerCase().contains("bot"))
		{
			sendMessage(channel, versionNumber + " " + versionDate);
		}

		// Disconnect from channel
		if(message.toLowerCase().contains("disconnect") && !sender.toLowerCase().contains("bot"))
		{
			sendMessage(channel, "Disconnecting...");
			
			// Pause to make sure disconnect message is sent
			try 
			{
				Thread.sleep(1250);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			// Disconnect from channel
			dispose();
			
		}
		
		// Greetings
		if((message.toLowerCase().contains("hi") || message.toLowerCase().contains("hello")) && !sender.toLowerCase().contains("bot"))
		{
			sendMessage(channel, "Hello, " + sender + "!");
		}
		
		// Weather info
		if(message.toLowerCase().contains("weather")  && !sender.toLowerCase().contains("bot"))
		{
			// Get the zip or city and test the validity
			// Check if the input is "weather zip/city name"
			if(message.split(" ").length == 1)
			{
				sendMessage(channel, "Make sure to include a name or zipcode!");
			}
			
			// If the input is formatted properly, check the validity of the zip/city name
			else
			{
				// Create new WeatherAPI to use WeatherAPI methods
				WeatherAPI weatherapi = new WeatherAPI();
				
				// Get the zip/city name from message
				String zipOrCity = message.split(" ", 2)[1];
				
				// Check validity of zip/city name
				boolean validZipOrCity;
				validZipOrCity = weatherapi.testZipOrCity(zipOrCity);
				
				// If invalid zip/city name, display error message
				if(!validZipOrCity)
				{
					sendMessage(channel, "Sorry, that is not a valid zipcode or city! Try again.");
				}
				
				// If valid, display weather info
				else
				{
					try 
					{
						// Fill String[] with city weather info
						String[] cityInfo = weatherapi.getWeather(zipOrCity);
						
						// Display name of city
						sendMessage(channel, "Weather in " + cityInfo[0]);
						
						// Display temperature in city
						sendMessage(channel, "Temperature: " + cityInfo[1] + "F");
						
						// Display "feels like" temperature in city
						sendMessage(channel, "Feels like: " + cityInfo[2] + "F");
						
						// Display weather conditions in city
						sendMessage(channel, "Weather conditions: " + cityInfo[3]);
						
					} catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		// Spotify new releases
		if((message.toLowerCase().contains("spotify new releases") && !sender.toLowerCase().contains("bot")))
		{
			if(Integer.parseInt(message.split(" ")[3]) >= 10)
			{
				sendMessage(channel, "Number of new releases must not exceed 10! Try again.");
			}
			else
			{
				try {
					// Create new SpotifyAPI to use SpotifyAPI methods
					SpotifyAPI spotifyapi = new SpotifyAPI();
					
					// Get number of releases to display from message
					int numSongs = Integer.parseInt(message.split(" ")[3]);
					
					// Fill releases with new spotify releases
					String[] releases = spotifyapi.getNewSpotifyReleases(numSongs);
					
					for(int i = 0; i < numSongs; i++)
					{
						sendMessage(channel, releases[i]);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
