package com.eudhari.dao;

import com.eudhari.model.ComplaintModel;
import java.util.List;

public interface ComplaintDAO {
    void saveComplaint(ComplaintModel complaint);
    List<ComplaintModel> getComplaintsByUserId(String userId);
    List<ComplaintModel> getAllComplaints();
    ComplaintModel getComplaintById(String complaintId);
    void updateComplaintStatusAndResponse(String complaintId, String status, String adminResponse);
}
