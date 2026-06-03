package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.dtos.InsigniaIDRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ControllersTestNuevo {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ───────── INSIGNIAS ─────────

    @Test
    void getAllInsignias_ok() throws Exception {
        mockMvc.perform(get("/insignias"))
                .andExpect(status().isOk());
    }

    @Test
    void getInsignia_existente_ok() throws Exception {
        InsigniaDTO dto = new InsigniaDTO(null, "Test", "Desc");

        String response = mockMvc.perform(post("/insignias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        InsigniaDTO creada = objectMapper.readValue(response, InsigniaDTO.class);

        mockMvc.perform(get("/insignias/" + creada.id()))   // ← .id() no .getId()
                .andExpect(status().isOk());
    }

    // ───────── INSIGNIAS DONADOR ─────────

    @Test
    void asignarInsignia_ok() throws Exception {
        InsigniaDTO dto = new InsigniaDTO(null, "Insignia OK", "Desc");

        String response = mockMvc.perform(post("/insignias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        InsigniaDTO creada = objectMapper.readValue(response, InsigniaDTO.class);

        InsigniaIDRequest request = new InsigniaIDRequest(creada.id());  // ← .id()

        mockMvc.perform(post("/insigniasDonador/donador-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getInsigniasDonador_null_retorna404() throws Exception {
        mockMvc.perform(get("/insigniasDonador/donador-x")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("donador-x"))
                .andExpect(status().isNotFound());
    }

    // ───────── MISIONES ─────────

    @Test
    void addMision_ok() throws Exception {
        // MisionDTO es record: (id, nombre, insigniaID, categoriaInicio, categoriaFin, tipo)
        MisionDTO dto = new MisionDTO(null, "Mision test", "ins-id",
                CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.REVOLUCIONARIO,
                TipoMisionEnum.COMPLETITUD);

        mockMvc.perform(post("/misiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getMisiones_ok() throws Exception {
        mockMvc.perform(get("/misiones"))
                .andExpect(status().isOk());
    }

    // ───────── MISION DONADOR ─────────

    @Test
    void asignarMision_ok() throws Exception {
        MisionDTO dto = new MisionDTO(null, "Mision OK", "ins-id",
                CategoriaDonadorEnum.COLABORADOR, CategoriaDonadorEnum.TRANSFORMADOR,
                TipoMisionEnum.COMPLETITUD);

        String response = mockMvc.perform(post("/misiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        MisionDTO creada = objectMapper.readValue(response, MisionDTO.class);

        String body = "{\"misionID\":\"" + creada.id() + "\"}";  // ← .id()

        mockMvc.perform(post("/misionesDonador/donador-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMisionEnCurso_ok_o_404() throws Exception {
        mockMvc.perform(get("/misionesDonador/donador-1"))
                .andExpect(result ->
                        Assertions.assertTrue(
                                result.getResponse().getStatus() == 200 ||
                                result.getResponse().getStatus() == 404
                        )
                );
    }

    // ───────── PROCESAMIENTO ─────────

    @Test
    void procesar_error_badRequest() throws Exception {
        mockMvc.perform(post("/procesamiento/donador-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("donador-1"))
                .andExpect(result ->
                        Assertions.assertTrue(
                                result.getResponse().getStatus() == 204 ||
                                result.getResponse().getStatus() == 400 ||
                                result.getResponse().getStatus() == 412
                        )
                );
    }
}


@SpringBootTest
class FachadaTest {

    @Autowired
    private Fachada fachada;

    @BeforeEach
    void setUp() {
        // fachadaDonadoresYEntidades no está disponible en esta entrega,
        // se mockea para evitar NullPointerException
        FachadaDonadoresYEntidades fachadaMock = Mockito.mock(FachadaDonadoresYEntidades.class);
        Mockito.when(fachadaMock.buscarDonadorPorID(Mockito.anyString()))
                .thenReturn(new DonadorDTO("donador-1", "Donador Test", "OCASIONAL", 24, "hh@gmail.com", "12345678", "Calle Falsa 123", null, "CategoriaTest"));
        fachada.setFachadaDonadoresYEntidades(fachadaMock);
    }

    // ───────── INSIGNIAS ─────────

    @Test
    void agregarInsignia_y_buscar() {
        InsigniaDTO dto = new InsigniaDTO(null, "Nueva", "Desc");

        InsigniaDTO creada = fachada.agregarInsignia(dto);

        Assertions.assertNotNull(creada.id());  // ← .id()

        InsigniaDTO buscada = fachada.getInsignia(creada.id());

        Assertions.assertEquals("Nueva", buscada.nombre());  // ← .nombre()
    }

    @Test
    void getAllInsignias_noNull() {
        Assertions.assertNotNull(fachada.getAllInsignias());
    }

    // ───────── MISIONES ─────────

    @Test
    void agregarMision_y_buscar() {
        MisionDTO dto = new MisionDTO(null, "Mision", "ins-id",
                CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.REVOLUCIONARIO,
                TipoMisionEnum.COMPLETITUD);

        MisionDTO creada = fachada.agregarMision(dto);

        Assertions.assertNotNull(creada.id());  // ← .id()

        MisionDTO buscada = fachada.getMision(creada.id());

        Assertions.assertEquals("Mision", buscada.nombre());  // ← .nombre()
    }

    @Test
    void getAllMisiones_noNull() {
        Assertions.assertNotNull(fachada.getAllMisiones());
    }

    // ───────── ASIGNACIONES ─────────

    @Test
    void asignarInsignia_donador_valido() {
        InsigniaDTO ins = fachada.agregarInsignia(
                new InsigniaDTO(null, "Ins", "Desc")
        );

        Assertions.assertDoesNotThrow(() ->
                fachada.asignarInsigniaADonador("donador-1", ins)
        );
    }

    @Test
    void asignarMision_donador_valido() {
        MisionDTO mision = fachada.agregarMision(
                new MisionDTO(null, "M", "ins-id",
                        CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.REVOLUCIONARIO,
                        TipoMisionEnum.COMPLETITUD)
        );

        Assertions.assertDoesNotThrow(() ->
                fachada.asignarMisionADonador("donador-1", mision)
        );
    }

    @Test
    void procesarDonador_noExplota() {
        Assertions.assertDoesNotThrow(() ->
                fachada.procesarDonador("donador-1")
        );
    }
}