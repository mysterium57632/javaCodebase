package de.paull.std.files;

import de.paull.std.output.ANSI;
import de.paull.std.output.Output;
import de.paull.std.util.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public final class ConfigHandler {

    private final String configFilePath;
    private final String fileHeader;
    private final InitializeConfig iniConfig;
    private static HashMap<String, String> defaultConfig;
    private static HashMap<String, String> updateConfig;

    /**
     * Config File Handler
     */
    public ConfigHandler(String path, String fileHeader, InitializeConfig initializeConfig) {
        this.configFilePath = path;
        this.fileHeader = fileHeader;
        this.iniConfig = initializeConfig;
        iniDefaultConfig();
        iniUpdateConfig();
        saveConfig();
        printConfig();
    }

    /**
     * Gibt den Inhalt zu dem Key zurück
     * @param key Schlüssel von Config-Paar
     * @return Value von Update Config, wenn nicht vorhanden von Default Config
     */
    public static String get(String key) {
        String value = updateConfig.get(key);
        if (value != null) return value;
        System.out.println("Config Key not found: " + key + ", switch to default value");
        return defaultConfig.get(key);
    }

    /**
     * Gibt den Inhalt zu dem Key als Integer zurück
     * @param key Schlüssel von Config-Paar
     * @return Value von Update Config, wenn nicht vorhanden von Default Config als int
     * Bei falschem Format wird -1 zurückgegeben
     */
    public static int getInteger(String key) {
        try {
            String s = get(key);
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String getDefault(String key) {
        return defaultConfig.get(key);
    }

    /**
     * Speichert die Config Date ab
     */
    private void saveConfig() {
        String content = getContent();
        File f = new File(configFilePath);
        try {
            FileUtil.createFile(f);
            Writer.write(f, content, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erstellt File Inhalt mit Layout für Config-File aus den updateConfig
     * (wenn nicht vorhanden aus defaultConfig)-Werten
     * @return gibt content zurück
     */
    private String getContent() {
        String content = "[===> " + fileHeader + " <===]";
        Set<String> keys = defaultConfig.keySet();
        for (String s : keys) {
            String value = updateConfig.get(s);
            if (value == null) value = defaultConfig.get(s);
            content += "\n" + s + "=" + value;
        }
        return content.trim();
    }

    /**
     * Gibt alle Config-Daten in einer Tabelle aus.
     * Wenn keine manuelle änderung vorgenommen wurde,
     * wird der Wert aus defaultConfig genommen
     */
    private void printConfig() {
        Set<String> keySet = defaultConfig.keySet();
        String[] keys = keySet.toArray(new String[defaultConfig.size()]);
        String[][] table = new String[keys.length][2];
        for (int i = 0; i < keys.length; i++) {
            String value = updateConfig.get(keys[i]);
            if (value == null) value = defaultConfig.get(keys[i]);
            table[i][0] = keys[i];
            table[i][1] = value;
        }
        System.out.println(Table.convert(
                table, ANSI.BLUE + Output.Stream.timestamp() + ":" + ANSI.RESET + " Config Data:") + "\n");
    }

    /**
     * Ließt Inhalt aus der Config-File ein,
     * wenn valide, dann wird zur liste hinzugefügt
     */
    private void iniUpdateConfig() {
        updateConfig = new HashMap<>();
        File f = new File(configFilePath);
        if (!f.exists()) return;
        // File Inhalt einlesen
        String content;
        try {
            content = Reader.readRaw(f);
            if (content.endsWith("\uFFFF")) // Wierdes Zeichen am Ende entfernen
                content = content.substring(0, content.length()-1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // File splitten
        String[] lines = content.split("\n");
        for (String s : lines) {
            String[] split = s.split("=");
            try {
                String key = split[0].trim();
                if (!defaultConfig.containsKey(key)) continue; // nur wenn key valid ist, also in defaultConfig drin
                String value = split[1].trim();
                updateConfig.put(key, value);
            } catch (IndexOutOfBoundsException e) {
                // Ignore
            }
        }
    }

    /**
     * Default Werte-Paare für Config
     */
    private void iniDefaultConfig() {
        defaultConfig = iniConfig.iniDefaultConfig();
    }

    public interface InitializeConfig {
        HashMap<String, String> iniDefaultConfig();
    }
}
