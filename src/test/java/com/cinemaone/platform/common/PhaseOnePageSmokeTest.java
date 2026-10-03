package com.cinemaone.platform.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class PhaseOnePageSmokeTest {
    @Autowired MockMvc mockMvc;

    @Test
    void rendersLegacyPhaseOnePage() throws Exception {
        mockMvc.perform(get("/phase-1"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Foundation")));
    }
}
