package consultation.online.rst.com.onlineconsultation.Model;

/**
 * Created by Ashish on 03-11-2017.
 */

public class BookedTimeSlot {
    String email, initCallTime, orderId, fcmToken;
    int isCallInitiated, callStatus;
    public BookedTimeSlot(String email, String initCallTime, String orderId, int isCallInitiated, int callStatus, String fcmToken){
        this.email = email;
        this.initCallTime = initCallTime;
        this.orderId = orderId;
        this.isCallInitiated = isCallInitiated;
        this.callStatus = callStatus;
        this.fcmToken = fcmToken;
    }
}
