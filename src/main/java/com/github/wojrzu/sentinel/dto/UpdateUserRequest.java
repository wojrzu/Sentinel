package com.github.wojrzu.sentinel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    Boolean accountActive;
    Integer ownedPlan;
    Boolean subscriptionActive;
    Integer status;
}
