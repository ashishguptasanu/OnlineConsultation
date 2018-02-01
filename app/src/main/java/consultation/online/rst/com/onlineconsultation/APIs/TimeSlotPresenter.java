package consultation.online.rst.com.onlineconsultation.APIs;


public class TimeSlotPresenter implements TimeSlotContract.Presenter, TimeSlotContract.OnTaskListener {
    private TimeSlotContract.View mTimeSlotView;
    private TimeSlotInteractor mTimeSlotInteractor;
    public TimeSlotPresenter(TimeSlotContract.View timeSlotView) {
        this.mTimeSlotView = timeSlotView;
        mTimeSlotInteractor = new TimeSlotInteractor(this);
    }
    @Override
    public void timeSlotTask(String orderId, String process, String deviceType, String callStatus, String fcmToken) {
        mTimeSlotInteractor.performTimeSlotTask(orderId, process, deviceType, callStatus, fcmToken);
    }
    @Override
    public void onSuccess(String message) {
        mTimeSlotView.onTaskSuccess(message);
    }
    @Override
    public void onFailure(String message) {
        mTimeSlotView.onTaskFailure(message);
    }
}
