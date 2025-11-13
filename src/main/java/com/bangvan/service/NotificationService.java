package com.bangvan.service;

import com.bangvan.dto.response.seller.NotificationSummaryResponse;
import java.security.Principal;

public interface NotificationService {


    NotificationSummaryResponse getNotificationSummary(Principal principal);
}