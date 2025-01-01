package com.mri.uiapp.ApiConnector;


import com.mri.uiapp.History;
import com.mri.uiapp.User;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:5000/FRapi";

    public static List<History> getHistory() {
        try {
            URL url = new URL(BASE_URL + "/history");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                List<History> history = new ArrayList<History>();
                List<User> users = getUser();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray personData = jsonArray.getJSONArray(i);
                    int id = personData.getInt(0);
                    String dateTime = personData.getString(1);
                    int idUser = personData.getInt(2);

                    String firstname = personData.get(3).toString();
                    String secondname = personData.get(4).toString();


                    history.add(new History(id, dateTime, getUserToHistory(idUser, users, firstname,secondname)));
                }
                return history;
            } else {
                System.out.println("Failed to retrieve data. HTTP Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public static List<User> getUser() {
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                List<User> users = new ArrayList<User>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray personData = jsonArray.getJSONArray(i);
                    int id = personData.getInt(0);
                    String firstName = personData.getString(1);
                    String secondName = (personData.getString(2));

                    users.add(new User(id, firstName, secondName));
                }
                return users;
            } else {
                System.out.println("Failed to retrieve data. HTTP Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }


    private static User getUserToHistory(int userId, List<User> users, String firstname, String lastname) {
//        List<User> users = ApiClient.getUser();
        for(User u : users) {
            if (u.getId() == userId) {
                return u;
            }
        }
        return new User(userId,firstname,lastname);
    }

}


