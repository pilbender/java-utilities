package org.raescott.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Richard Scott Smith <pilbender@gmail.com>
 */
public class IpFilter {
	static Map iptable = new HashMap();
	static int totalNumberOfIPAddresses;
	static String ipaddress;
	static Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

	public static void main(String[] args) throws IOException {
		for (String filename: args) {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while(bufferedReader.ready()) {
				if (identifyIpAddress(bufferedReader.readLine())) {
					if (iptable.containsKey(ipaddress)) {
						iptable.put(ipaddress, (Integer)iptable.get(ipaddress) + 1);
					} else {
						iptable.put(ipaddress, 1);
					}
				}
			}
		}

		System.out.println("IP Table:");
		// Sort the map by value
		Stream<Map.Entry<String, Integer>> sortedStream = iptable.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		LinkedHashMap<String, Integer> sortedMap = sortedStream.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(e1, e2) -> e1,
				LinkedHashMap::new
		));
		for (String key : sortedMap.keySet()) {
			StringBuilder spaces = new StringBuilder(key);
			spaces.append(" ");
			for (int i = key.length(); i < 20; ++i) {
				spaces.append("-");
			}
			spaces.append(" ");
			spaces.append(sortedMap.get(key));
			System.out.println(spaces);
		}
		System.out.println("\nTotal IP addresses scanned: " + totalNumberOfIPAddresses);
	}

	public static boolean identifyIpAddress(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			++totalNumberOfIPAddresses;
			ipaddress = matcher.group();
			return true;
		} else {
			return false;
		}
	}
}
