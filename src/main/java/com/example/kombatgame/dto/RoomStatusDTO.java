package com.example.kombatgame.dto;

import java.util.List;

public class RoomStatusDTO {
    public boolean guestJoined;
    public boolean configSubmitted;
    public boolean hostAgreed;
    public boolean guestAgreed;
    public List<MinionTypeDTO> hostConfig;
}
