package tei.com.hchl.domain;

/**
 * Created by ktds on 2017-09-06.
 */

public class KboGameDate {
    String BEFORE_G_DT;
    String NOW_G_DT;
    String  NOW_G_DT_TEXT;
    String AFTER_G_DT;

    public String getBEFORE_G_DT() {
        return BEFORE_G_DT;
    }

    public void setBEFORE_G_DT(String BEFORE_G_DT) {
        this.BEFORE_G_DT = BEFORE_G_DT;
    }

    public String getNOW_G_DT() {
        return NOW_G_DT;
    }

    public void setNOW_G_DT(String NOW_G_DT) {
        this.NOW_G_DT = NOW_G_DT;
    }

    public String getNOW_G_DT_TEXT() {
        return NOW_G_DT_TEXT;
    }

    public void setNOW_G_DT_TEXT(String NOW_G_DT_TEXT) {
        this.NOW_G_DT_TEXT = NOW_G_DT_TEXT;
    }

    public String getAFTER_G_DT() {
        return AFTER_G_DT;
    }

    public void setAFTER_G_DT(String AFTER_G_DT) {
        this.AFTER_G_DT = AFTER_G_DT;
    }

    @Override
    public String toString() {
        return "KboGameDate{" +
                "BEFORE_G_DT='" + BEFORE_G_DT + '\'' +
                ", NOW_G_DT='" + NOW_G_DT + '\'' +
                ", NOW_G_DT_TEXT='" + NOW_G_DT_TEXT + '\'' +
                ", AFTER_G_DT='" + AFTER_G_DT + '\'' +
                '}';
    }
}
