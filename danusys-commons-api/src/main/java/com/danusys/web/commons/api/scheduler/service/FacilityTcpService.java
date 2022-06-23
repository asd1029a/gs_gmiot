package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.app.StrUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityTcpService {
    private final FacilityRepository facilityRepository;
    private final FacilityOptRepository facilityOptRepository;

    public List<Facility> addGuardianCctv(int count, String nodeId, String facilityName, double latitude, double longitude, String vmsSvrNo, Map<String, Object> optData) {
        List<Facility> facilityList = new ArrayList<>();
        Long codeSeq = facilityRepository.findCommonCode("CCTV");
        for (int i = 1; i <= count; i++) {
            String faciliyId = nodeId + "_" + vmsSvrNo + "_" + i;
            String administZone = facilityRepository.getEmdCode(longitude, latitude);
            Facility origin = facilityRepository.findByFacilityId(faciliyId);
            if (origin == null) {
                origin = Facility.builder()
                        .facilityId(faciliyId).facilityName(facilityName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .facilityKind(codeSeq)
                        .administZone(administZone).build();
            } else {
                origin.setLatitude(latitude);
                origin.setLongitude(longitude);
                origin.setAdministZone(administZone);
            }
            origin = facilityRepository.save(origin);
            Long facilitySeq = origin.getFacilitySeq();

            String no = StrUtils.getStr(i);

            optData.entrySet().stream().forEach(f -> {
                String value = f.getKey().equals("rtsp_url") ? f.getValue() + no : StrUtils.getStr(f.getValue());
                FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facilitySeq, f.getKey());
                if (facilityOpt == null) {
                    facilityOpt = FacilityOpt.builder()
                            .facilitySeq(facilitySeq)
                            .facilityOptName(f.getKey()).facilityOptType(53)
                            .facilityOptValue(value).build();
                } else {
                    facilityOpt.setFacilityOptValue(value);
                }

                facilityOptRepository.save(facilityOpt);
            });

            facilityList.add(origin);
        }

        return facilityRepository.saveAll(facilityList);
    }

    public List<Facility> addGroupCctv(int count, String nodeId, String facilityName, double latitude, double longitude, String vmsSvrNo, Map<String, Object> optData) {
        List<Facility> facilityList = new ArrayList<>();
        Long codeSeq = facilityRepository.findCommonCode("CCTV");
        for (int i = 1; i <= count; i++) {
            String faciliyId = nodeId + "_" + vmsSvrNo + "_" + i;
            String administZone = facilityRepository.getEmdCode(longitude, latitude);
            Facility origin = facilityRepository.findByFacilityId(faciliyId);
            if (origin == null) {
                origin = Facility.builder()
                        .facilityId(faciliyId).facilityName(facilityName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .facilityKind(codeSeq)
                        .administZone(administZone).build();
            } else {
                origin.setLatitude(latitude);
                origin.setLongitude(longitude);
                origin.setAdministZone(administZone);
            }
            origin = facilityRepository.save(origin);
            Long facilitySeq = origin.getFacilitySeq();

            String no = StrUtils.getStr(i);

            optData.entrySet().stream().forEach(f -> {
                String value = f.getKey().equals("rtsp_url") ? f.getValue() + no : StrUtils.getStr(f.getValue());
                FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facilitySeq, f.getKey());
                if (facilityOpt == null) {
                    facilityOpt = FacilityOpt.builder()
                            .facilitySeq(facilitySeq)
                            .facilityOptName(f.getKey()).facilityOptType(53)
                            .facilityOptValue(value).build();
                } else {
                    facilityOpt.setFacilityOptValue(value);
                }

                facilityOptRepository.save(facilityOpt);
            });

            facilityList.add(origin);
        }

        return facilityRepository.saveAll(facilityList);
    }

    public Facility addCctv(String nodeId, String facilityName, double latitude, double longitude, String vmsSvrNo, Map<String, Object> optData) {
        Long codeSeq = facilityRepository.findCommonCode("CCTV");
        String faciliyId = nodeId + "_" + vmsSvrNo + "_1";
        String administZone = facilityRepository.getEmdCode(longitude, latitude);
        Facility origin = facilityRepository.findByFacilityId(faciliyId);
        if (origin == null) {
            origin = Facility.builder()
                    .facilityId(faciliyId).facilityName(facilityName)
                    .latitude(latitude)
                    .longitude(longitude)
                    .facilityKind(codeSeq)
                    .administZone(administZone).build();
        } else {
            origin.setLatitude(latitude);
            origin.setLongitude(longitude);
            origin.setAdministZone(administZone);
        }
        origin = facilityRepository.save(origin);
        Long facilitySeq = origin.getFacilitySeq();

        optData.entrySet().stream().forEach(f -> {
            String value = f.getKey().equals("rtsp_url") ? f.getValue() + "1" : StrUtils.getStr(f.getValue());
            FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facilitySeq, f.getKey());
            if (facilityOpt == null) {
                facilityOpt = FacilityOpt.builder()
                        .facilitySeq(facilitySeq)
                        .facilityOptName(f.getKey()).facilityOptType(53)
                        .facilityOptValue(value).build();
            } else {
                facilityOpt.setFacilityOptValue(value);
            }

            facilityOptRepository.save(facilityOpt);
        });

        return origin;
    }
}
