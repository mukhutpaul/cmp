package com.cm_policier.effectifs.dto;

import java.util.List;

import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.model.User;

public class MobileLoginResponse {
    private String token;
    private User user;
    private Seance seanceActive;
    private Session sessionActive;
    private List<DetailUnite> unites;
}