package com.eudhari.model;

public class ActivityModel {
    private String iconType;
    private String activity;
    private String timeAgo;

    public ActivityModel(String iconType, String activity, String timeAgo) {
        this.iconType = iconType;
        this.activity = activity;
        this.timeAgo = timeAgo;
    }

    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
}
