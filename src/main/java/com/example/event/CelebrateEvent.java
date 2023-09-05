package com.example.event;

import lombok.Getter;
import lombok.Value;
import lombok.With;

@Value
@With
@Getter
public class CelebrateEvent {
    String occasion;
}
