package tei.com.hchl.game;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameUtil {
    public static Game getGame(String teamCode) {
        List<Game> gameList = getGameList();
        if (gameList != null) {
            for (Game game : gameList) {
                if (game.getHOME_ID().equals(teamCode) || game.getAWAY_ID().equals(teamCode)) {
                    return game;
                }
            }
        }

        return null;
    }

    public static List<Game> getGameList() {
        List<Game> gameList = null;

        try {
            gameList = new AsyncTask<Void,Void,List<Game>>() {
                @Override
                protected List<Game> doInBackground(Void... params) {
                    List<Game> tmpGameList = null;

                    try {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                        String urlStr = "http://www.koreabaseball.com/ws/Main.asmx/GetKboGameList?leId=1&srId=0,1,3,4,5,7,9&date="+dateFormat.format(calendar.getTime());
                        URL url = new URL(urlStr);
                        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuffer sb = new StringBuffer();

                        String data;
                        while ((data = br.readLine()) != null) {
                            sb.append(data);
                        }

                        JsonArray jsonArray = new JsonParser().parse(sb.toString().trim()).getAsJsonObject().get("game").getAsJsonArray();
                        Gson gson = new Gson();
                        tmpGameList = new ArrayList<>();

                        for (JsonElement element : jsonArray) {
                            tmpGameList.add(gson.fromJson(element, Game.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return tmpGameList;
                }

                @Override
                protected void onPostExecute(List<Game> gameList) {
                    super.onPostExecute(gameList);
                }
            }.execute().get();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return gameList;
    }
}
