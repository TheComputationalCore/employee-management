package com.empmgmt.service;

import com.empmgmt.dto.KPIDTO;
import java.util.List;

public interface KPIService {

    KPIDTO create(KPIDTO dto);

    List<KPIDTO> getAll();
}
