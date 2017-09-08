package tei.com.hchl.util;

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
import java.util.concurrent.ExecutionException;

import tei.com.hchl.domain.KboGame;
import tei.com.hchl.domain.KboGameDate;

public class GameUtil {
    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(calendar.getTime());
    }

    public KboGameDate getKboGameDate(String date) {
        KboGameDate kboGameDate = null;

        try {
            kboGameDate = new KboGameDateTask().execute(date).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return kboGameDate;
    }

    public KboGame getBeforeBeforeKboGame(String teamCode) {
        KboGameDate kboGameDate = getKboGameDate(getDate());
        kboGameDate = getKboGameDate(kboGameDate.getBEFORE_G_DT());
        return getKboGame(teamCode, kboGameDate.getBEFORE_G_DT());
    }

    public KboGame getBeforeKboGame(String teamCode) {
        KboGameDate kboGameDate = getKboGameDate(getDate());
        return getKboGame(teamCode, kboGameDate.getBEFORE_G_DT());
    }

    public KboGame getKboGame(String teamCode, String date) {
        List<KboGame> gameList = getKboGameList(date);

        if (gameList != null) {
            for (KboGame game : gameList) {
                if (game.getHOME_ID().equals(teamCode) || game.getAWAY_ID().equals(teamCode)) {
                    return game;
                }
            }
        }

        return null;
    }

    public List<KboGame> getKboGameList(String date) {
        try {
            return new KboGameListTask().execute(date).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class KboGameDateTask extends AsyncTask<String, Void, KboGameDate> {
        @Override
        protected KboGameDate doInBackground(String... params) {
            try {
                String urlStr = "http://www.koreabaseball.com/ws/Main.asmx/GetKboGameDate?leId=1&srId=0&date=" + params[0];
                URL url = new URL(urlStr);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer sb = new StringBuffer();

                String data;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }

                JsonElement jsonElement = new JsonParser().parse(sb.toString().trim()).getAsJsonObject();
                Gson gson = new Gson();
                KboGameDate kboGameDate = gson.fromJson(jsonElement, KboGameDate.class);

                if (!params[0].equals(kboGameDate.getNOW_G_DT())) {
                    kboGameDate.setNOW_G_DT("");
                    kboGameDate.setNOW_G_DT_TEXT("");
                }

                return kboGameDate;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class KboGameListTask extends AsyncTask<String, Void, List<KboGame>> {
        @Override
        protected List<KboGame> doInBackground(String... params) {
            try {
                String urlStr = "http://www.koreabaseball.com/ws/Main.asmx/GetKboGameList?leId=1&srId=0,1,3,4,5,7,9&date=" + params[0];
                URL url = new URL(urlStr);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer sb = new StringBuffer();

                String data;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }

                JsonArray jsonArray = new JsonParser().parse(sb.toString().trim()).getAsJsonObject().get("game").getAsJsonArray();
                Gson gson = new Gson();
                List<KboGame> kboGameList = new ArrayList<>();

                for (JsonElement element : jsonArray) {
                    kboGameList.add(gson.fromJson(element, KboGame.class));
                }

                return kboGameList;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
