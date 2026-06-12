package ar.edu.utn.dds.k3003.componentes;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Verifica que Incentivos pueda obtener las donaciones de un donador desde una fecha:
 * arma bien el path GET /donaciones/search?donadorID={id}&fechaInicio={YYYY-MM-DD}
 * y deserializa el array de DonacionDTO de la respuesta.
 */
class DonacionesProxyTest {

  /** Liga un MockRestServiceServer al RestTemplate interno del proxy (campo privado). */
  private MockRestServiceServer bindMockServer(DonacionesProxy proxy) throws Exception {
    Field field = DonacionesProxy.class.getDeclaredField("restTemplate");
    field.setAccessible(true);
    RestTemplate restTemplate = (RestTemplate) field.get(proxy);
    return MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void buscaPorDonadorYFechaInicio_armaElPathYDeserializaLaRespuesta() throws Exception {
    DonacionesProxy proxy = new DonacionesProxy("http://donaciones.test");
    MockRestServiceServer server = bindMockServer(proxy);

    String json =
        "[{\"id\":\"d1\",\"donadorID\":\"donador1\",\"depositoID\":\"dep1\","
            + "\"descripcion\":\"arroz\",\"productoID\":\"prod1\",\"cantidad\":5,"
            + "\"estado\":\"ACEPTADA\"}]";

    server
        .expect(requestTo("http://donaciones.test/donaciones/search?donadorID=donador1&fechaInicio=2025-01-01"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

    List<DonacionDTO> resultado =
        proxy.buscarPorDonadorYFechaInicio("donador1", LocalDate.of(2025, 1, 1));

    server.verify();
    Assertions.assertEquals(1, resultado.size());
    DonacionDTO d = resultado.get(0);
    Assertions.assertEquals("d1", d.id());
    Assertions.assertEquals("donador1", d.donadorID());
    Assertions.assertEquals(5, d.cantidad());
    Assertions.assertEquals(EstadoDonacionEnum.ACEPTADA, d.estado());
  }

  @Test
  void buscaPorDonadorYFechaInicio_respuestaVaciaDevuelveListaVacia() throws Exception {
    DonacionesProxy proxy = new DonacionesProxy("http://donaciones.test");
    MockRestServiceServer server = bindMockServer(proxy);

    server
        .expect(requestTo("http://donaciones.test/donaciones/search?donadorID=x&fechaInicio=1900-01-01"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

    List<DonacionDTO> resultado =
        proxy.buscarPorDonadorYFechaInicio("x", LocalDate.of(1900, 1, 1));

    server.verify();
    Assertions.assertTrue(resultado.isEmpty());
  }

  @Test
  void sinUrlConfigurada_trabajaEnModoMockYDevuelveListaVacia() {
    DonacionesProxy proxy = new DonacionesProxy("");

    List<DonacionDTO> resultado =
        proxy.buscarPorDonadorYFechaInicio("donador1", LocalDate.of(2025, 1, 1));

    Assertions.assertTrue(resultado.isEmpty());
  }
}
