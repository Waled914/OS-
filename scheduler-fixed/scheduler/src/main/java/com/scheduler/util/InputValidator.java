package com.scheduler.util;

import com.scheduler.model.Process;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputValidator {

    public static String validate(List<Process> processes, int quantum) {
        if (processes == null || processes.isEmpty())
            return "أدخل process واحدة على الأقل.";

        if (quantum <= 0)
            return "الـ Time Quantum لازم يكون أكبر من صفر.";

        Set<String> ids = new HashSet<>();
        for (Process p : processes) {
            if (p.getId() == null || p.getId().trim().isEmpty())
                return "كل process لازم يكون ليها ID.";

            if (!ids.add(p.getId().trim()))
                return "ID مكرر: " + p.getId();

            if (p.getArrivalTime() < 0)
                return p.getId() + ": Arrival Time لازم يكون >= 0";

            if (p.getBurstTime() <= 0)
                return p.getId() + ": Burst Time لازم يكون > 0";
        }

        return null; // null = كل حاجة تمام
    }
}
