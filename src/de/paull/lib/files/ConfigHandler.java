package de.paull.lib.files;

import de.paull.lib.output.ANSI;
import de.paull.lib.output.Output;
import de.paull.lib.util.Table;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handles the creation, loading, and access of configuration key-value pairs from a config file.
 * Useful for managing persistent application settings across sessions.
 * <p>
 * This class automatically initializes a configuration file using default values provided
 * via the InitializeConfig interface. It reads existing config values from the file,
 * overrides defaults where applicable, and ensures missing values are restored to defaults.
 * Values can be retrieved using static getter methods, and the full configuration is
 * printed in a readable table format.
 */
public final class ConfigHandler {

    private final String configFilePath;
    private final String fileHeader;
    private final InitializeConfig iniConfig;
    private final InitializeHiddenConfig hidConfig;
    private static HashMap<String, String> defaultConfig;
    private static HashMap<String, String> updateConfig;
    private static List<String> hidden = new ArrayList<>();

    /**
     * Initializes the ConfigHandler
     * @param path the path the config file will be generated of found in
     * @param fileHeader the Header for the file
     * @param initializeConfig an Interface in wich the HashMap with the default values will be initialized.
     *                         The Method should contain all key-value pairs which should be saved in the config.
     */
    public ConfigHandler(String path, String fileHeader, InitializeConfig initializeConfig) {
        this.configFilePath = path;
        this.fileHeader = fileHeader;
        this.iniConfig = initializeConfig;
        this.hidConfig = null;
        ini();
    }

    /**
     * Initializes the ConfigHandler
     * @param path the path the config file will be generated of found in
     * @param fileHeader the Header for the file
     * @param initializeConfig an Interface in wich the HashMap with the default values will be initialized.
     *                         The Method should contain all key-value pairs which should be saved in the config.
     * @param iniHidCfg same functionality as the initializeConfig,
     *                  but the value of the fields added here will not be shown in plain text during the printConfig().
     */
    public ConfigHandler(String path, String fileHeader, InitializeConfig initializeConfig, InitializeHiddenConfig iniHidCfg) {
        this.configFilePath = path;
        this.fileHeader = fileHeader;
        this.iniConfig = initializeConfig;
        this.hidConfig = iniHidCfg;
        ini();
    }

    private void ini() {
        iniDefaultConfig();
        iniHiddenFields();
        iniUpdateConfig();
        saveConfig();
    }

    /**
     * Returns the value for the specified key
     * @param key key of config pair
     * @return Value from the config file if the key-value pair and the file exist.
     *      Otherwise, the default value defined in the initializeConfig.
     */
    public static String get(String key) {
        String value = updateConfig.get(key);
        if (value != null) return value;
        System.out.println("Config Key not found: " + key + ", switch to default value");
        return defaultConfig.get(key);
    }

    /**
     * Returns the value as an int for the specified key
     * @param key key of config pair
     * @return Value from the config file if the key-value pair and the file exist.
     *      Otherwise, the default value defined in the initializeConfig.
     *      If a NumberFormat exception occurs, -1 will be returned.
     */
    public static int getInteger(String key) {
        try {
            String s = get(key);
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the default value for the given key
     * @param key key of config pair
     * @return the corresponding value of the key-value pair defined in the initializeConfig.
     */
    public static String getDefault(String key) {
        return defaultConfig.get(key);
    }

    /**
     * Gibt alle Config-Daten in einer Tabelle aus.
     * Wenn keine manuelle änderung vorgenommen wurde,
     * wird der Wert aus defaultConfig genommen
     */
    public static void printConfig() {
        Set<String> keySet = defaultConfig.keySet();
        String[] keys = keySet.toArray(new String[defaultConfig.size()]);
        String[][] table = new String[keys.length][2];
        for (int i = 0; i < keys.length; i++) {
            String value = updateConfig.get(keys[i]);
            if (value == null) value = defaultConfig.get(keys[i]);
            table[i][0] = keys[i];
            table[i][1] = value;
            if (!hidden.contains(keys[i])) continue;
            String v = "";
            while (v.length() < value.length()) v += "*";
            table[i][1] = v;
        }
        System.out.println(Table.convert(
                table, ANSI.BLUE + Output.Stream.timestamp() + ":" + ANSI.RESET + " Config Data:") + "\n");
    }

    /**
     * Saves the config file
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
            if (content.endsWith("\uFFFF")) // Weirdes Zeichen am Ende entfernen
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

    private void iniHiddenFields() {
        if (hidConfig == null) return;
        HashMap<String, String> map = hidConfig.iniHiddenConfig();
        map.entrySet().forEach(e -> {
            defaultConfig.put(e.getKey(), e.getValue());
            hidden.add(e.getKey());
        });
    }

    /**
     * The Method should contain all key-value pairs which should be saved in the config.
     */
    public interface InitializeConfig {
        HashMap<String, String> iniDefaultConfig();
    }

    /**
     * The Method should contain all key-value pairs which should be saved in the config but without showing the values' content inside of the print function. The other functionality remain the same.
     */
    public interface InitializeHiddenConfig {
        HashMap<String, String> iniHiddenConfig();
    }
}
