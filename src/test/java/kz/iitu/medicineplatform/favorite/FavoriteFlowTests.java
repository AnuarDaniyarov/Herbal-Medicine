package kz.iitu.medicineplatform.favorite;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kz.iitu.medicineplatform.plant.Plant;
import kz.iitu.medicineplatform.plant.PlantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavoriteFlowTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private PlantRepository plants;

    @Test
    void favoritesRequireAuthAndSupportAddListDelete() throws Exception {
        mockMvc.perform(get("/api/favorites")).andExpect(status().isUnauthorized());

        Csrf csrf = getCsrf();
        String email = "fav-user-" + UUID.randomUUID() + "@example.test";
        String password = "Strong-Password-2026!";

        String registration = "{\"email\":\"" + email + "\",\"displayName\":\"Fav User\",\"password\":\"" + password + "\"}";
        mockMvc.perform(post("/api/auth/register").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated());

        String login = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);

        Plant plant = plants.findAll().stream().findFirst().orElseGet(() ->
                plants.save(new Plant("Valeriana officinalis", "Валериана лекарственная", "Caprifoliaceae", "Описание")));

        mockMvc.perform(get("/api/favorites/check?plantId=" + plant.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavorite").value(false));

        String addRequest = "{\"plantId\":" + plant.getId() + "}";
        MvcResult addResult = mockMvc.perform(post("/api/favorites").session(session)
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(addRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PLANT"))
                .andExpect(jsonPath("$.targetId").value(plant.getId()))
                .andReturn();

        mockMvc.perform(get("/api/favorites/check?plantId=" + plant.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavorite").value(true));

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PLANT"));

        Number favId = com.jayway.jsonpath.JsonPath.read(addResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/favorites/" + favId).session(session)
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites/check?plantId=" + plant.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavorite").value(false));
    }

    private Csrf getCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
        String content = result.getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(content);
        if (!matcher.find()) throw new IllegalStateException("CSRF token not found in response: " + content);
        return new Csrf(cookie, matcher.group(1));
    }

    private record Csrf(Cookie cookie, String token) { }
}
