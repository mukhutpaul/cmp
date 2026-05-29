package com.cm_policier.effectifs.dto;

import lombok.*;
import java.util.List;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPayload {

    private List<Session> sessions;
    private List<Seance> seances;

    private List<Controle> controles;
    private List<Controle> absences;

    private List<DocumentSyncDTO> documents;
}