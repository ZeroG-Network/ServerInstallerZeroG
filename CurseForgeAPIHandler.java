import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class CurseForgeAPIHandler {
    private static final String API_URL = "https://api.curseforge.com/v1/mods";
    private static final String API_KEY = "your-curseforge-api-key"; // Replace with your actual API key

    public static List<String> fetchModPacks(String... authors) {
        try {
            String authorsQuery = String.join(",", authors);
            URI uri = new URI(API_URL + "/search?gameId=432&author=" + authorsQuery);
            URL url = uri.toURL(); // Convert URI to URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            String jsonResponse = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray dataArray = responseObject.getJSONArray("data");

            return dataArray.toList().stream()
                    .map(obj -> ((JSONObject) obj).getString("name"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error fetching modpacks");
        }
    }

    public static List<String> fetchModPackVersions(String modPackName) {
        try {
            URI uri = new URI(API_URL + "?gameId=432&slug=" + modPackName);
            URL url = uri.toURL(); // Convert URI to URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            String jsonResponse = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray versionsArray = responseObject.getJSONObject("data").getJSONArray("versions");

            return versionsArray.toList().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error fetching versions");
        }
    }
}
