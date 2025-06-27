package com.live.sports.event.tracker.transfer;

import lombok.ToString;
import lombok.Value;

/**
 * Wrapper class for errors in the ApiResponse
 */
@Value
@ToString
public class ApiError {
    Integer status;
    String message;
    String path;
}
