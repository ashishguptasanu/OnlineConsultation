package consultation.online.rst.com.onlineconsultation.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ashish on 13-10-2017.
 */

public class LawyerList {
    @SerializedName("per_id")
    @Expose
    private String perId;
    @SerializedName("per_name")
    @Expose
    private String perName;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("per_email")
    @Expose
    private String perEmail;
    @SerializedName("per_about")
    @Expose
    private String perAbout;
    @SerializedName("per_img")
    @Expose
    private String perImg;
    @SerializedName("per_slot_time")
    @Expose
    private String perDesignation;
    @SerializedName("per_designation")
    @Expose
    private String perLocation;
    @SerializedName("per_address")
    @Expose
    private String perSlotTime;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("modes")
    @Expose
    private List<Mode> modes = null;

    public String getPerId() {
        return perId;
    }

    public void setPerId(String perId) {
        this.perId = perId;
    }

    public String getPerName() {
        return perName;
    }

    public void setPerName(String perName) {
        this.perName = perName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPerEmail() {
        return perEmail;
    }

    public void setPerEmail(String perEmail) {
        this.perEmail = perEmail;
    }

    public String getPerAbout() {
        return perAbout;
    }

    public void setPerAbout(String perAbout) {
        this.perAbout = perAbout;
    }

    public String getPerImg() {
        return perImg;
    }

    public void setPerImg(String perImg) {
        this.perImg = perImg;
    }

    public String getPerSlotTime() {
        return perSlotTime;
    }

    public void setPerSlotTime(String perSlotTime) {
        this.perSlotTime = perSlotTime;
    }
    public String getVideo(String video){ return video;}
    public String getAudio(String audio){return audio;}

    public List<Mode> getModes() {
        return modes;
    }

    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }
    public String getPerDesignation(){return perDesignation;}
    public String getPerLocation(){return perLocation;}
    public LawyerList(String perId, String perName, String perEmail, String contact, String perImg, String perAbout, String perSlotTime, List<Mode> modes, String perDesignation, String perLocation){
        this.perId = perId;
        this.perName = perName;
        this.perEmail = perEmail;
        this.perImg = perImg;
        this.perAbout = perAbout;
        this.perSlotTime = perSlotTime;
        this.contact = contact;
        this.modes = modes;
        this.perDesignation = perDesignation;
        this.perLocation = perLocation;
    }
}
