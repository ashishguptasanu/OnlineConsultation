package consultation.online.rst.com.onlineconsultation.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ashish on 24-10-2017.
 */

public class Slot {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("slot")
    @Expose
    private String slot;
    @SerializedName("slab")
    @Expose
    private String slab;
    @SerializedName("indian_slot_date")
    @Expose
    private String indian_slot_date;
    @SerializedName("original_slot_date")
    @Expose
    private String original_slot_date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getSlab() {
        return slab;
    }
    public String getOriginal_slot_date(){return original_slot_date;}
    public String getIndian_slot_date(){return indian_slot_date;}
    public void setSlab(String slab) {
        this.slab = slab;
    }
    public Slot(String status, String msg, String slot, String slab, String originalSlotDate, String indianSlotDate){
        this.status = status;
        this.msg = msg;
        this.slot = slot;
        this.slab = slab;
        this.original_slot_date = originalSlotDate;
        this.indian_slot_date = indianSlotDate;
    }
}
