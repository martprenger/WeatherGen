package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsFile
{
	private File settingsFile;
	private HashMap<String, String> settingsMap;

	public SettingsFile(File settingsFile) {
		this.settingsFile = settingsFile;
		this.settingsMap = new HashMap<String, String>();
	}

	public String getValue(String name) {
		return this.settingsMap.get(name.toLowerCase());
	}

	public void setValue(String name, String value) {
		this.settingsMap.put(name.toLowerCase(), value);
	}

	public void load() {
		try {
			BufferedReader infile = new BufferedReader(new FileReader(this.settingsFile));
			String line = null;
			while ((line = infile.readLine()) != null) {
				String[] setting = line.split(":", 2);
				setValue(setting[0].trim(), setting[1].trim());
			} 
			infile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void save() {
		try {
			BufferedWriter outfile = new BufferedWriter(new FileWriter(this.settingsFile));
			String format = "%s: %s\n";
			for (Map.Entry<String, String> setting : this.settingsMap.entrySet()) {
				String name = setting.getKey();
				String value = setting.getValue();
				outfile.write(String.format(format, new Object[] { name, value }));
			} 
			outfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}