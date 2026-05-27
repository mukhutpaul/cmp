package com.cm_policier.effectifs.dto;


import lombok.*;

import java.util.List;
import java.util.UUID;

import com.cm_policier.effectifs.syncDto.ControleDto;
import com.cm_policier.effectifs.syncDto.DocumentDto;
import com.cm_policier.effectifs.syncDto.SeanceDto;
import com.cm_policier.effectifs.syncDto.SessionDto;

@Getter
@Setter
public class SyncBatchRequest {

    private String serverAddress;

    private String deviceId;

    private UUID seanceId;

    private List<SessionDto> sessions;

    private List<SeanceDto> seances;

    private List<ControleDto> controles;

    private List<DocumentDto> documents;
}