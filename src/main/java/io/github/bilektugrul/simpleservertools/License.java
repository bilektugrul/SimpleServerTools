package io.github.bilektugrul.simpleservertools;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class License {

    private final String license;
    private final String server;
    private final Plugin plugin;
    private boolean debug = false;

    private boolean valid = false;
    private ReturnType returnType;
    private String generatedBy;
    private String licensedTo;
    private String generatedIn;


    public License(String license, String server, Plugin plugin) {
        this.license = license;
        this.server = server;
        this.plugin = plugin;
    }

    public void debug() {
        debug = true;
    }

    public void request() {
        try {
            URL url = new URL(server + "/request.php");
            URLConnection connection = url.openConnection();
            if (debug) System.out.println("[DEBUG] Connecting to request server: " + server + "/request.php");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            connection.setRequestProperty("License-Key", license);
            connection.setRequestProperty("Plugin-Name", plugin.getDescription().getName());
            // This MUST be the same as the REQUEST_KEY defined in config.php
            String requestKey = "vmLAyzmppLLDgvqMPFyHLSkWdyHYqRImNueC1OLK";
            connection.setRequestProperty("Request-Key", requestKey);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if (debug) System.out.println("[DEBUG] Reading response");
            if (debug) System.out.println("[DEBUG] Converting to string");
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String response = builder.toString();
            if (debug) System.out.println("[DEBUG] Converted");

            String[] responseSplit = response.split(";");
            if (responseSplit[0].equals("VALID")) {
                if (debug) System.out.println("[DEBUG] VALID LICENSE");
                valid = true;
                returnType = ReturnType.valueOf(responseSplit[0]);

                generatedBy = responseSplit[2];
                generatedIn = responseSplit[3];
                licensedTo = responseSplit[1];
            } else {
                if (debug) System.out.println("[DEBUG] FAILED VALIDATION");
                valid = false;
                returnType = ReturnType.valueOf(responseSplit[0]);

                if (debug) System.out.println("[DEBUG] FAILED WITH RESULT: " + returnType);
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isValid() {
        return valid;
    }

    public ReturnType getReturn() {
        return returnType;
    }

    public String getLicensedTo() {
        return licensedTo;
    }

    public String getLicense() {
        return license;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getGeneratedIn() {
        return generatedIn;
    }

    public enum ReturnType {
        LICENSE_NOT_FOUND, PLUGIN_NAME_NOT_FOUND, REQUEST_KEY_NOT_FOUND, INVALID_REQUEST_KEY, INVALID_LICENSE, TOO_MANY_IPS, VALID
    }

}
