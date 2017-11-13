package consultation.online.rst.com.onlineconsultation.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ashish on 13-10-2017.
 */
public class Mode {

    @SerializedName("mode_id")
    @Expose
    private String modeId;
    @SerializedName("mode_name")
    @Expose
    private String modeName;
    @SerializedName("lawyer_id")
    @Expose
    private String lawyerId;
    @SerializedName("mode_price")
    @Expose
    private String modePrice;

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public String getModeName() {
        return modeName;
    }
    public String getLawyerId(){return lawyerId;}
    public String getModePrice(){return modePrice;}

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }
    public Mode(String modeId, String modeName, String lawyerId, String modePrice){
        this.modeId = modeId;
        this.modeName = modeName;
        this.lawyerId = lawyerId;
        this.modePrice = modePrice;
    }

}
