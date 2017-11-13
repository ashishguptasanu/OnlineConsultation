package consultation.online.rst.com.onlineconsultation.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ashish on 27-10-2017.
 */

public class TimeZone {

    @SerializedName("countryId")
    @Expose
    private String countryId;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("zoneName")
    @Expose
    private String zoneName;
    @SerializedName("gmtOffset")
    @Expose
    private String gmtOffset;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(String gmtOffset) {
        this.gmtOffset = gmtOffset;
    }
    public TimeZone(String countryId, String countryName, String countryCode, String zoneName){
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.zoneName = zoneName;
    }

}
