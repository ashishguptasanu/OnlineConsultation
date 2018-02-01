package consultation.online.rst.com.onlineconsultation.APIs;


public interface TimeSlotContract {
    interface View {
        void onTaskSuccess(String message);

        void onTaskFailure(String message);
    }
    interface Presenter {
        void timeSlotTask(String orderId, String process, String deviceType, String callStatus, String fcmToken);
    }
    interface Interactor {
        void performTimeSlotTask(String orderId, String process, String deviceType, String callStatus, String fcmToken);
    }
    interface OnTaskListener {
        void onSuccess(String message);
        void onFailure(String message);
    }
}
